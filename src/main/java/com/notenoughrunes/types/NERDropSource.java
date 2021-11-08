package com.notenoughrunes.types;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NERDropSource
{
	public final String source;
	public final int quantityLow;
	public final int quantityHigh;
	public final String rarity;
	public final String dropLevel;
	public final String dropType;
}
