package com.notenoughrunes;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UseSectionType
{
	RECIPES("Recipes", "Item recipe uses"),
	SHOPS("Shops", "Item shop uses");

	public final String sectionName;
	public final String sectionDesc;
}