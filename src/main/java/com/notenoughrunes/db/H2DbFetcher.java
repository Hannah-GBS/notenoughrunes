package com.notenoughrunes.db;

import com.google.common.hash.Funnels;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import com.notenoughrunes.NotEnoughRunesPlugin;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
@Slf4j
public class H2DbFetcher
{

	private static final String dbFileName = "data.sqlite";
	private static final String remoteUrl = "https://raw.githubusercontent.com/Hannah-GBS/runelite-wiki-scraper/wiki-data/output/" + dbFileName;
	public static final File dbFile = new File(NotEnoughRunesPlugin.NER_DATA_DIR, dbFileName);

	private final OkHttpClient httpClient;

	public void fetch(Runnable callback)
	{
		downloadDbChecksum(callback);
		// todo checksum

	}

	private void downloadDbChecksum(Runnable callback) {
		Request req = new Request.Builder()
			.get()
			.url(remoteUrl + ".sha256")
			.build();

		httpClient.newCall(req).enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException e)
			{
				log.error("Failed to download remote checksum for database file");
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException
			{
				assert response.body() != null;
				InputStream remoteBytes = response.body().byteStream();
				ByteArrayOutputStream into = new ByteArrayOutputStream();
				byte[] buf = new byte[4096];
				for (int n; 0 < (n = remoteBytes.read(buf));) {
					into.write(buf, 0, n);
				}
				into.close();
				String checksum = into.toString(StandardCharsets.UTF_8).trim();

				if (!dbFile.exists() || !checksumMatches(checksum))
				{
					log.debug("Missing db or checksum mismatch. Downloading db");
					downloadRemoteDb(callback);
				} else {
					callback.run();
				}
			}
		});
	}

	private void downloadRemoteDb(Runnable callback)
	{
		Request req = new Request.Builder()
			.get()
			.url(remoteUrl)
			.build();
		
		httpClient.newCall(req).enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException e)
			{
				log.error("Failed to download remote database file");
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException
			{
				assert response.body() != null;
				InputStream remoteBytes = response.body().byteStream();

				try (FileOutputStream fos = new FileOutputStream(dbFile))
				{
					remoteBytes.transferTo(fos);
				}
				
				callback.run();
			}
		});
	}

	private boolean checksumMatches(String remote) throws IOException
	{
		boolean matches;
		if (dbFile.exists())
		{
			FileInputStream is = new FileInputStream(dbFile);
			Hasher hasher = Hashing.sha256().newHasher();
			ByteStreams.copy(is, Funnels.asOutputStream(hasher));
			String hash = hasher.hash().toString();
			matches = hash.equals(remote);
		} else {
			matches = false;
		}

		return matches;
	}

}
