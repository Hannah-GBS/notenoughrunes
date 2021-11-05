package com.notenoughrunes.types;

import java.util.Set;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
class NERShop
{
	private final String name;
	private final String sellMultiplier;
	private final String location;
	private final boolean isMembers;
	private final Set<NERShopItem> items;
}
