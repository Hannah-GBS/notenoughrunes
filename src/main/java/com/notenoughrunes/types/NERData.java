package com.notenoughrunes.types;

import java.io.IOException;
import java.util.Set;
import lombok.Data;
import net.runelite.client.RuneLite;

@Data
public class NERData
{
	static final String localDir = RuneLite.RUNELITE_DIR + "/not-enough-runes/";
	static final String itemInfoFile = "items-info.min.json";
	static final String itemProductionFile = "items-production.min.json";
	static final String itemShopsFile = "items-shopitems.min.json";
	static final String itemSpawnsFile = "items-spawns.min.json";
	static final String itemDropsFile = "items-drop-sources.min.json";

	private final Set<NERInfoItem> itemInfoData;
	private final Set<NERProductionRecipe> itemProductionData;
	private final Set<NERShop> itemShopData;
	private final Set<NERSpawnGroup> itemSpawnData;
	private final Set<NERDropItem> itemDropData;


	public NERData(DataFetcher dataFetcher) throws IOException
	{
		this.itemInfoData = dataFetcher.getItemInfo();
		this.itemProductionData = dataFetcher.getItemProduction();
		this.itemShopData = dataFetcher.getItemShops();
		this.itemSpawnData = dataFetcher.getItemSpawns();
		this.itemDropData = dataFetcher.getItemDrops();
	}
}
