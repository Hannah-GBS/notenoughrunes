package com.notenoughrunes.types;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NERInfoItem
{
	private final String name;
	private final String examineText;
	private final String group;
	private final String itemID;
	private final boolean isMembers;
	private final boolean isTradeable;
}
