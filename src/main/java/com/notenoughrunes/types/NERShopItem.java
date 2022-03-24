package com.notenoughrunes.types;

import jdk.internal.jline.internal.Nullable;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NERShopItem
{
	private final String name;
	private final String currency;
	private final String stock;

	@Nullable
	private final String buyPrice;

	@Nullable
	private final String sellPrice;
}
