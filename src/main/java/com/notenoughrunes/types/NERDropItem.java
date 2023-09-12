package com.notenoughrunes.types;

import java.util.List;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NERDropItem
{
	private final String name;

	private final List<NERDropSource> dropSources;
}
