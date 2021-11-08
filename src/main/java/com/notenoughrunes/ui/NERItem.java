package com.notenoughrunes.ui;

import com.notenoughrunes.types.NERInfoItem;
import lombok.Value;
import net.runelite.client.util.AsyncBufferedImage;

@Value
class NERItem
{
	private final AsyncBufferedImage icon;
	private final NERInfoItem infoItem;
}
