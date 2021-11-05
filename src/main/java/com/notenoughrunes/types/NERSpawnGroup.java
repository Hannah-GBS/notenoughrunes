package com.notenoughrunes.types;

import java.util.Set;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NERSpawnGroup
{
	private final String group;
	private final Set<NERSpawnItem> spawns;
}
