package com.notenoughrunes.db.queries;

import com.notenoughrunes.types.NERSpawnItem;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ItemSpawnQuery extends ModeledQuery<NERSpawnItem>
{

	private final int itemId;

	@Override
	public String getSql()
	{
		//language=SQL	
		return "SELECT IG.NAME AS ITEM_GROUP_NAME, SI.* FROM ITEM_GROUPS IG " +
			"JOIN SPAWN_ITEMS SI ON IG.ID = SI.GROUP_ID " +
			"WHERE SI.ITEM_ID = ?";
	}

	@Override
	public void setParams(PreparedStatement ps) throws SQLException
	{
		ps.setInt(1, itemId);
	}

	@Override
	public NERSpawnItem convertRow(ResultSet rs) throws SQLException
	{
		try
		{
			return new NERSpawnItem(
				rs.getString("ITEM_GROUP_NAME"),
				rs.getString("NAME"),
				rs.getString("VERSION"),
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
