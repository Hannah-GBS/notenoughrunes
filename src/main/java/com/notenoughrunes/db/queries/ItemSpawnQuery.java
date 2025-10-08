package com.notenoughrunes.db.queries;

import com.notenoughrunes.types.NERSpawnItem;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
		return "SELECT IG.NAME AS ITEM_GROUP_NAME, SI.* FROM ITEM_GROUPS IG " +
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
		try
		{
			return new NERSpawnItem(
				rs.getString("ITEM_GROUP_NAME"),
				rs.getString("NAME"),
				rs.getString("COORDS"),
				rs.getString("LOCATION"),
				rs.getBoolean("IS_MEMBERS"),
				rs.getString("PLANE"),
				rs.getString("MAP_ID")
			);
		}
		finally
		{
			rs.next();
		}
	}
}
