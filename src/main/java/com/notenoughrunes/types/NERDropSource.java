package com.notenoughrunes.types;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NERDropSource
{
	private final String source;
	private final int quantityLow;
	private final int quantityHigh;
	private final String rarity;
	private final String dropLevel;
	private final String dropType;
}
