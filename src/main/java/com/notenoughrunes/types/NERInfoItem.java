package com.notenoughrunes.types;

import javax.annotation.Nullable;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NERInfoItem
{
	private final String name;
	private final String examineText;
	private final String group;

	@Nullable
	private final String version;

	private final String url;
	private final int itemID;
	private final boolean isMembers;
	private final boolean isTradeable;
}
