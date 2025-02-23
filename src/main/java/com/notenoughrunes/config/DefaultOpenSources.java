package com.notenoughrunes.config;

import com.notenoughrunes.SourceSectionType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DefaultOpenSources
{
	RECIPES(SourceSectionType.RECIPES),
	DROPS(SourceSectionType.DROPS),
	SHOPS(SourceSectionType.SHOPS),
	SPAWNS(SourceSectionType.SPAWNS);

	private final SourceSectionType sectionType;

	@Override
	public String toString()
	{
		return sectionType.sectionName;
	}

	public static DefaultOpenSources of(SourceSectionType type) {
		switch (type) {
			case RECIPES:
				return RECIPES;
			case DROPS:
				return DROPS;
			case SHOPS:
				return SHOPS;
			case SPAWNS:
				return SPAWNS;
			default:
				throw new IllegalArgumentException();
		}
	}
}
