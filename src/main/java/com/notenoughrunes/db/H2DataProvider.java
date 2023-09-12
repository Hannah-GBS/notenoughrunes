package com.notenoughrunes.db;

import com.notenoughrunes.db.queries.ModeledQuery;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
@Slf4j
public class H2DataProvider implements AutoCloseable
{

	private final H2DbFetcher dbFetcher;

	private Connection db;
	private boolean initialized = false;

	public void init()
	{
		initialized = false;

		dbFetcher.fetch(() ->
		{
			try
			{
				String dbPath = H2DbFetcher.dbFile.getAbsolutePath();
				log.debug("Creating db connection to {}", dbPath);
				db = DriverManager.getConnection(buildConnectionString(
					dbPath,
					"TRACE_LEVEL_SYSTEM_OUT=1", // ERROR
					"TRACE_LEVEL_FILE=0", // OFF
					"ACCESS_MODE_DATA=r", // readonly
					"MODE=MYSQL" // compat features
				));
			}
			catch (SQLException e)
			{
				log.error("Failed to open connection to database file", e);
			}
		});

		initialized = true;
	}

	@Override
	public void close() throws Exception
	{
		initialized = false;
		db.close();
	}

	private static String buildConnectionString(String fileName, String... parameters)
	{
		StringBuilder sb = new StringBuilder("jdbc:h2:file:");
		sb.append(fileName);

		for (String p : parameters)
		{
			sb.append(';');
			sb.append(p);
		}

		return sb.toString();
	}

	public <T> T executeSingle(ModeledQuery<T> query)
	{
		List<T> res = executeMany(query);
		return res.isEmpty() ? null : res.get(0);
	}

	public <T> List<T> executeMany(ModeledQuery<T> query)
	{
		if (!initialized)
		{
			return Collections.emptyList();
		}

		try
		{
			PreparedStatement ps = createStatement(query.getSql());
			query.setParams(ps);

			try (ResultSet rs = ps.executeQuery())
			{
				List<T> res = new ArrayList<>();
				while (rs.next())
				{
					res.add(query.convertRow(rs));
				}

				return res;
			}
		}
		catch (Exception e)
		{
			log.warn("Query failed in wrapMany", e);
			return Collections.emptyList();
		}
	}

	private PreparedStatement createStatement(String sql) throws SQLException
	{
		if (!initialized)
		{
			throw new IllegalStateException("Cannot createStatement on a closed connection");
		}

		return db.prepareStatement(sql);
	}

}
