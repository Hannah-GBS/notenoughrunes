package com.notenoughrunes.ui;

import lombok.Value;
import net.runelite.client.util.AsyncBufferedImage;

@Value
class NERItem
{
	private final AsyncBufferedImage icon;
	private final String name;
	private final int itemId;
	private final boolean isMembers;
}
