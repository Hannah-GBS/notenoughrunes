package com.notenoughrunes.ui;

import com.notenoughrunes.types.NERInfoItem;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import net.runelite.client.util.AsyncBufferedImage;

@Data
@AllArgsConstructor
class NERItem
{
	private AsyncBufferedImage icon;

	private final NERInfoItem infoItem;
}
