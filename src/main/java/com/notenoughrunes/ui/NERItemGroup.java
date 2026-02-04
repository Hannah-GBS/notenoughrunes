package com.notenoughrunes.ui;

import java.util.List;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class NERItemGroup
{
	private final String group;

	@Nullable
	private String selectedVersion;

	private final List<NERItem> items;
}
