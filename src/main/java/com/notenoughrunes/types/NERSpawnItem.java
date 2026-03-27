package com.notenoughrunes.types;

import javax.annotation.Nullable;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NERSpawnItem
{
	private final String group;
	private final String name;

	@Nullable
	private final String version;

	private final String coords;
	private final String location;
	private final boolean isMembers;
	private final String plane;
	private final String mapID;
}
