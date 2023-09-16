package com.notenoughrunes.types;

import javax.annotation.Nullable;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NERShopItem
{
	private final String name;

	@Nullable
	private final String version;

	private final int itemID;
	private final String currency;
	private final String stock;

	@Nullable
	private final String buyPrice;

	@Nullable
	private final String sellPrice;
}
