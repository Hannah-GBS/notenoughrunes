package com.notenoughrunes.types;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import net.runelite.http.api.RuneLiteAPI;

@Slf4j
public class DataFetcher
{
	private static final Gson gson = RuneLiteAPI.GSON.newBuilder().registerTypeAdapter(boolean.class, new BooleanTypeAdapter()).create();

	public DataFetcher()
	{
		makeLocalDir();
	}

	public Set<NERInfoItem> getItemInfo() throws IOException
	{
		String data = getRemoteJson(NERData.itemInfoFile);
		// CHECKSTYLE:OFF
		return gson.fromJson(data, new TypeToken<Set<NERInfoItem>>() {}.getType());
		// CHECKSTYLE:ON
	}

	public Set<NERProductionRecipe> getItemProduction() throws IOException
	{
		String data = getRemoteJson(NERData.itemProductionFile);
		// CHECKSTYLE:OFF
		return gson.fromJson(data, new TypeToken<Set<NERProductionRecipe>>() {}.getType());
		// CHECKSTYLE:ON
	}

	public Set<NERShop> getItemShops() throws IOException
	{
		String data = getRemoteJson(NERData.shopItemsFile);
		// CHECKSTYLE:OFF
		return gson.fromJson(data, new TypeToken<Set<NERShop>>() {}.getType());
		// CHECKSTYLE:ON
	}

	public Set<NERSpawnGroup> getItemSpawns() throws IOException
	{
		String data = getRemoteJson(NERData.itemSpawnsFile);
		// CHECKSTYLE:OFF
		return gson.fromJson(data, new TypeToken<Set<NERSpawnGroup>>() {}.getType());
		// CHECKSTYLE:ON
	}

	private String getRemoteJson(String path) throws IOException
	{
		String url = "https://raw.githubusercontent.com/Hannah-GBS/runelite-wiki-scraper/master/output" + path;

		try (InputStream inputStream = new URL(url).openStream())
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
			String data = readAll(reader);
			FileWriter writer = new FileWriter(NERData.localDir + path);
			writer.write(data);
			writer.close();
			return data;
		}
	}

	private static String readAll(Reader rd) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1)
		{
			sb.append((char) cp);
		}
		return sb.toString();
	}

	private void makeLocalDir()
	{
		File dir = new File(NERData.localDir);
		if (!dir.exists())
		{
			dir.mkdir();
		}
	}
}
