package com.notenoughrunes;

import com.notenoughrunes.config.MenuLookupMode;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("notenoughrunes")
public interface NotEnoughRunesConfig extends Config
{
	@ConfigItem(
		keyName = "invLookupMode",
		name = "Inventory Lookup",
		description = "Whether to show a NER Lookup option on inventory items, and when",
		position = 0
	)
	default MenuLookupMode invLookupMode()
	{
		return MenuLookupMode.RIGHT;
	}

	@ConfigItem(
		keyName = "bankLookupMode",
		name = "Bank Lookup",
		description = "Whether to show a NER Lookup option on bank items, and when",
		position = 1
	)
	default MenuLookupMode bankLookupMode()
	{
		return MenuLookupMode.RIGHT;
	}


}
