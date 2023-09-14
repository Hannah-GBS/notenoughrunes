package com.notenoughrunes.db;

import com.google.common.io.ByteStreams;
import com.notenoughrunes.NotEnoughRunesPlugin;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

	private static final String dbFileName = "data.h2";
	private static final String remoteUrl = "https://raw.githubusercontent.com/Hannah-GBS/runelite-wiki-scraper/wiki-data/output/" + dbFileName;
	public static final File dbFile = new File(NotEnoughRunesPlugin.NER_DATA_DIR, dbFileName);

	private final OkHttpClient httpClient;

	public void fetch(Runnable callback)
	{
		// todo checksum
		if (!dbFile.exists())
		{
			downloadRemoteDb(callback);
		} else {
			callback.run();
		}
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

}
