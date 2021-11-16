package com.notenoughrunes.types;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NERShopItem
{
	private final String name;
	private final String currency;
	private final String stock;
}
