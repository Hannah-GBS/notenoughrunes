package com.notenoughrunes.types;

import javax.annotation.Nullable;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NERProductionMaterial
{
	private final String name;

	@Nullable
	private final String version;

	private final int itemID;

	@Nullable
	private final String quantity;
}
