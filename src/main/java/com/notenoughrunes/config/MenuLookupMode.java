package com.notenoughrunes.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MenuLookupMode
{
	DISABLED("Disabled"),
	RIGHT("Right click"),
	SHIFT("Shift-right click");

	private final String name;

	@Override
	public String toString()
	{
		return name;
	}
}
