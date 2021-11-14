package com.notenoughrunes.types;

import com.google.gson.annotations.SerializedName;
import javax.annotation.Nullable;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NERProductionOutput
{
	private final String name;
	private final String quantity;

	@Nullable
	@SerializedName("subtxt")
	private final String subtext;

	@Nullable
	@SerializedName("quantitynote")
	private final String quantityNote;
}
