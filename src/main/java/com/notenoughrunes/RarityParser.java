package com.notenoughrunes;

import com.google.common.collect.ImmutableMap;
import com.notenoughrunes.types.NERDropSource;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RarityParser
{
	
	private final Map<String, Double> PRESET_RARITY_NAMES = ImmutableMap.<String, Double>builder()
		.put("Always", 1D)
		.put("Common", 1 / 16D)
		.put("Uncommon", 1 / 64D)
		.put("Rare", 1 / 256D)
		.put("Very rare", 1 / 1024D)
		.put("Random", 1 / 4096D)
		.put("Varies", 1 / 4096D)
		.build();
	
	public double calculateRarity(NERDropSource dropSource)
	{
		return calculateRarity(dropSource.getRarity());
	}
	
	public double calculateRarity(String rarityString)
	{
		if (PRESET_RARITY_NAMES.containsKey(rarityString))
		{
			return PRESET_RARITY_NAMES.get(rarityString);
		}
			
		try {
			String[] fractionComponents = rarityString.split("/");
			return Double.parseDouble(fractionComponents[0].trim()) / Double.parseDouble(fractionComponents[1].trim());
		}
		catch (NumberFormatException | ArithmeticException | ArrayIndexOutOfBoundsException e)
		{
			return 1 / 65536D;
		}
	}
	
}
