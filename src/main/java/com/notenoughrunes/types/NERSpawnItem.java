package com.notenoughrunes.types;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NERSpawnItem
{
	private final String name;
	private final String coords;
	private final String location;
	private final boolean isMembers;
}
