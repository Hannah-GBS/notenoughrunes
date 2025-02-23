package com.notenoughrunes.config;

import com.notenoughrunes.UseSectionType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DefaultOpenUses
{
	RECIPES(UseSectionType.RECIPES),
	SHOPS(UseSectionType.SHOPS);

	private final UseSectionType sectionType;

	@Override
	public String toString()
	{
		return sectionType.sectionName;
	}

	public static DefaultOpenUses of(UseSectionType type) {
		switch (type) {
			case RECIPES:
				return RECIPES;
			case SHOPS:
				return SHOPS;
			default:
				throw new IllegalArgumentException();
		}
	}
}
