package com.notenoughrunes;

import com.notenoughrunes.config.DefaultOpenSources;
import com.notenoughrunes.config.DefaultOpenUses;
import com.notenoughrunes.config.MenuLookupMode;
import java.util.Collections;
import java.util.Set;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

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
		keyName = "equipLookupMode",
		name = "Equipment Lookup",
		description = "Whether to show a NER Lookup option on equipped items, and when",
		position = 1
	)
	default MenuLookupMode equipLookupMode() {
		return MenuLookupMode.RIGHT;
	}

	@ConfigItem(
		keyName = "bankLookupMode",
		name = "Bank Lookup",
		description = "Whether to show a NER Lookup option on bank items, and when",
		position = 2
	)
	default MenuLookupMode bankLookupMode()
	{
		return MenuLookupMode.RIGHT;
	}

	@ConfigItem(
		keyName = "clogLookupMode",
		name = "CLog Lookup",
		description = "Whether to show a NEr Lookup option on collection log items, and when",
		position = 3
	)
	default MenuLookupMode clogLookupMode() {
		return MenuLookupMode.RIGHT;
	}

	@ConfigItem(
		keyName = "ccBroadcastLookup",
		name = "Chat Broadcast Lookups",
		description = "Whether to show a NER Lookup option on clan chat loot drop broadcasts",
		position = 4
	)
	default boolean ccBroadcastLookup() {
		return true;
	}

	@ConfigItem(
		keyName = "defaultOpenSources",
		name = "Default Open Sources",
		description = "Which 'Sources' categories to open by default",
		position = 5
	)
	default Set<DefaultOpenSources> defaultOpenSources() {
		return Collections.emptySet();
	}

	@ConfigItem(
		keyName = "defaultOpenUses",
		name = "Default Open Uses",
		description = "Which 'Uses' categories to open by default",
		position = 6
	)
	default Set<DefaultOpenUses> defaultOpenUses() {
		return Collections.emptySet();
	}

	@ConfigSection(
		name = "Integrations",
		description = "Integrations with other Plugins",
		position = 7
	)
	String integrationSection = "integrationSection";

	@ConfigItem(
		keyName = "routeShopSources",
		name = "Route shops",
		description = "Include listed shops when routing to an item source",
		position = 8,
		section = integrationSection
	)
	default boolean routeShopSources()
	{
		return true;
	}

	@ConfigItem(
		keyName = "routeGroundSpawnSources",
		name = "Route ground spawns",
		description = "Include listed ground spawns when routing to an item source",
		position = 9,
		section = integrationSection
	)
	default boolean routeGroundSpawnSources()
	{
		return true;
	}

	@ConfigItem(
		keyName = "bmeNPCLookup",
		name = "Better Monster Examine",
		description = "Click NPCs in the Drops section to look them up in Better Monster Examine. This requires the Better Monster Examine plugin to be installed and enabled",
		position = 10,
		section = integrationSection
	)
	default boolean bmeNPCLookup()
	{
		return true;
	}

}
