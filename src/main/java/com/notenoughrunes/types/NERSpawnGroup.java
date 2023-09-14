package com.notenoughrunes.types;

import java.util.List;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NERSpawnGroup
{
	private final String group;
	private final List<NERSpawnItem> spawns;
}
