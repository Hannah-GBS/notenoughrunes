package com.notenoughrunes.db;

import com.google.common.base.Stopwatch;
import com.notenoughrunes.db.queries.ModeledQuery;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
@Slf4j
public class H2DataProvider implements AutoCloseable
{

	private static final ExecutorService DB_THREAD = Executors.newSingleThreadExecutor();

	private final H2DbFetcher dbFetcher;

	private Connection db;
	private boolean initialized = false;

	public void init()
	{
		initialized = false;

		dbFetcher.fetch(() ->
			DB_THREAD.submit(() ->
			{
				try
				{
					String dbPath = H2DbFetcher.dbFile.getAbsolutePath();
					log.debug("Creating db connection to {}", dbPath);
					db = DriverManager.getConnection(buildConnectionString(dbPath));
				}
				catch (SQLException e)
				{
					log.error("Failed to open connection to database file", e);
				}

				initialized = true;
			}));
	}

	@Override
	public void close() throws Exception
	{
		initialized = false;
		db.close();
	}

	private static String buildConnectionString(String fileName)
	{
		return "jdbc:sqlite:" + fileName;
	}

	public <T> void executeSingle(ModeledQuery<T> query, Consumer<T> callback)
	{
		executeMany(query, res ->
			callback.accept(res.isEmpty() ? null : res.get(0)));
	}

	public <T> void executeMany(ModeledQuery<T> query, Consumer<List<T>> callback)
	{
		if (!initialized)
		{
			callback.accept(Collections.emptyList());
		}

		DB_THREAD.submit(() ->
		{
			Stopwatch sw = Stopwatch.createStarted();
			try (PreparedStatement ps = createStatement(query.getSql()))
			{
				query.setParams(ps);

				try (ResultSet rs = ps.executeQuery())
				{
					List<T> res = new ArrayList<>();
					if (rs.next())
					{
						while (!rs.isAfterLast())
						{
							res.add(query.convertRow(rs));
						}
					}

					callback.accept(res);
				}
			}
			catch (Exception e)
			{
				log.warn("Query failed in wrapMany", e);
				callback.accept(Collections.emptyList());
			}
			finally
			{
				log.debug("[{}μs] Query {}", sw.elapsed(TimeUnit.MICROSECONDS), query.getSql());
			}
		});
	}

	private PreparedStatement createStatement(String sql) throws SQLException
	{
		if (!initialized)
		{
			throw new IllegalStateException("Cannot createStatement on a closed connection");
		}

		return db.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	}

}
