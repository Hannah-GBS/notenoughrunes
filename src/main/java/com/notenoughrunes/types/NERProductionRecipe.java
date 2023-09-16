package com.notenoughrunes.types;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NERProductionRecipe
{
	private final String ticks;
	private final List<NERProductionMaterial> materials;

	@Nullable
	private final String facilities;

	@Nullable
	private final String tools;

	private final List<NERProductionSkill> skills;

	@SerializedName("members")
	private final boolean isMembers;

	private final String outputItemName;
	private final String outputItemVersion;
	private final int outputItemID;
	private final String outputQuantity;
	private final String outputQuantityNote;
	private final String outputSubtext;

}
