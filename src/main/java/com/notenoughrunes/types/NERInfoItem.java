package com.notenoughrunes.types;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Nullable;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NERInfoItem
{
	private final int itemID;
	private final String name;
	private final String examineText;
	private final String group;

	@Nullable
	private final String version;

	private final String url;
	private final boolean isMembers;
	private final boolean isTradeable;

	public static NERInfoItem fromResultSet(ResultSet rs, boolean expand) throws SQLException
	{
		NERInfoItem res = new NERInfoItem(
			rs.getInt("I.ID"),
			rs.getString("I.NAME"),
			rs.getString("I.EXAMINE_TEXT"),
			rs.getString("IG.NAME"),
			rs.getString("I.VERSION"),
			rs.getString("I.URL"),
			rs.getBoolean("I.IS_MEMBERS"),
			rs.getBoolean("I.IS_TRADEABLE")
		);

		return res;
	}
}
