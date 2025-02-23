package com.notenoughrunes;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SourceSectionType
{
	RECIPES("Recipes", "Item recipes"),
	DROPS("Drops", "Item drop sources"),
	SHOPS("Shops", "Item shop sources"),
	SPAWNS("Spawns", "Item world spawns");

	public final String sectionName;
	public final String sectionDesc;
}
