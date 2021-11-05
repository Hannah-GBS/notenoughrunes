package com.notenoughrunes.types;

import com.google.gson.annotations.SerializedName;
import java.util.Set;
import javax.annotation.Nullable;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NERProductionRecipe
{
	private final String ticks;
	private final Set<NERProductionMaterial> materials;

	@Nullable
	private final String facilities;

	@Nullable
	private final String tools;

	private final Set<NERProductionSkill> skills;

	@SerializedName("members")
	private final boolean isMembers;

	private final NERProductionOutput output;

}
