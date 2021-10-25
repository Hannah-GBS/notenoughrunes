package com.notenoughrunes;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class NotEnoughRunesPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(NotEnoughRunesPlugin.class);
		RuneLite.main(args);
	}
}