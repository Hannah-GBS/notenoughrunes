package com.notenoughrunes.types;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
class NERShopItem
{
	private final String name;
	private final String currency;
	private final String stock;
}
