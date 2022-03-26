package com.notenoughrunes.types;

import com.google.common.hash.Funnels;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import net.runelite.http.api.RuneLiteAPI;

@Slf4j
public class DataFetcher
{
	private static final Gson gson = RuneLiteAPI.GSON.newBuilder().registerTypeAdapter(boolean.class, new BooleanTypeAdapter()).create();
	private final Map<String, String> checksums;

	public DataFetcher() throws IOException
	{
		makeLocalDir();
		checksums = getChecksums();
	}

	public Map<String, String> getChecksums() throws IOException
	{
		String data = getRemoteJson("checksums", false);
		// CHECKSTYLE:OFF
		return gson.fromJson(data, new TypeToken<Map<String, String>>() {}.getType());
		// CHECKSTYLE:ON
	}

	public Set<NERInfoItem> getItemInfo() throws IOException
	{
		String path = NERData.itemInfoFile;
		String data = checksumMatches(path);

		// CHECKSTYLE:OFF
		return gson.fromJson(data, new TypeToken<Set<NERInfoItem>>() {}.getType());
		// CHECKSTYLE:ON
	}

	public Set<NERProductionRecipe> getItemProduction() throws IOException
	{
		String path = NERData.itemProductionFile;
		String data = checksumMatches(path);

		// CHECKSTYLE:OFF
		return gson.fromJson(data, new TypeToken<Set<NERProductionRecipe>>() {}.getType());
		// CHECKSTYLE:ON
	}

	public Set<NERShop> getItemShops() throws IOException
	{
		String path = NERData.itemShopsFile;
		String data = checksumMatches(path);

		// CHECKSTYLE:OFF
		return gson.fromJson(data, new TypeToken<Set<NERShop>>() {}.getType());
		// CHECKSTYLE:ON
	}

	public Set<NERSpawnGroup> getItemSpawns() throws IOException
	{
		String path = NERData.itemSpawnsFile;
		String data = checksumMatches(path);

		// CHECKSTYLE:OFF
		return gson.fromJson(data, new TypeToken<Set<NERSpawnGroup>>() {}.getType());
		// CHECKSTYLE:ON
	}

	public Set<NERDropItem> getItemDrops() throws IOException
	{
		String path = NERData.itemDropsFile;
		String data = checksumMatches(path);

		// CHECKSTYLE:OFF
		return gson.fromJson(data, new TypeToken<Set<NERDropItem>>() {}.getType());
		// CHECKSTYLE:ON
	}

	private String checksumMatches(String path) throws IOException
	{
		boolean matches;
		if (!(new File(NERData.localDir + path)).exists())
		{
			matches = false;
		}
		else
		{
			FileInputStream is = new FileInputStream(NERData.localDir + path);
			Hasher hasher = Hashing.sha256().newHasher();
			ByteStreams.copy(is, Funnels.asOutputStream(hasher));
			String hash = hasher.hash().toString();

			matches = hash.equals(checksums.get(path));
		}

		String data;
		if (matches)
		{
			log.debug("Checksum match for " + path);
			data = getLocalJson(path);
		}
		else
		{
			data = getRemoteJson(path, true);
			log.debug("Checksum mismatch for " + path);
		}

		return data;
	}


	private String getLocalJson(String path) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(NERData.localDir + path)));

		return readAll(reader);
	}

	private String getRemoteJson(String path, boolean saveFile) throws IOException
	{
		String url = "https://raw.githubusercontent.com/Hannah-GBS/runelite-wiki-scraper/wiki-data/output/" + path;

		try (InputStream inputStream = new URL(url).openStream())
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
			String data = readAll(reader);

			if (saveFile)
			{
				FileWriter writer = new FileWriter(NERData.localDir + path);
				writer.write(data);
				writer.close();
			}

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
