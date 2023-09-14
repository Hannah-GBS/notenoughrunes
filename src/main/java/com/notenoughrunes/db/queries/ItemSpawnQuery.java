package com.notenoughrunes.db.queries;

import com.notenoughrunes.types.NERDropSource;
import com.notenoughrunes.types.NERSpawnGroup;
import com.notenoughrunes.types.NERSpawnItem;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ItemSpawnQuery extends ModeledQuery<NERSpawnItem>
{

	private final String itemName;

	private final String groupName;

	@Override
	public String getSql()
	{
		//language=SQL	
		return "SELECT * FROM ITEM_GROUPS IG " +
			"JOIN SPAWN_ITEMS SI ON IG.ID = SI.GROUP_ID " +
			"WHERE SI.NAME = ?" +
			"	AND IG.NAME = ?";
	}

	@Override
	public void setParams(PreparedStatement ps) throws SQLException
	{
		ps.setString(1, itemName);
		ps.setString(2, groupName);
	}

	@Override
	public NERSpawnItem convertRow(ResultSet rs) throws SQLException
	{
		return new NERSpawnItem(
			rs.getString("ITEM_GROUPS.NAME"),
			rs.getString("SPAWN_ITEMS.NAME"),
			rs.getString("SPAWN_ITEMS.COORDS"),
			rs.getString("SPAWN_ITEMS.LOCATION"),
			rs.getBoolean("SPAWN_ITEMS.IS_MEMBERS")
		);
	}
}
