package com.notenoughrunes.types;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NERProductionSkill
{
	private final String name;
	private final String level;
	private final String experience;

	@SerializedName("boostable")
	private final boolean isBoostable;
}
