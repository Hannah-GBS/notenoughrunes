package com.notenoughrunes.db.queries;

import com.notenoughrunes.types.NERInfoItem;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ItemByIDQuery extends ModeledQuery<NERInfoItem>
{

	private final int itemID;

	@Override
	public String getSql()
	{
		//language=SQL
		return "SELECT I.*, IG.NAME AS GROUP_NAME FROM ITEMS I " +
			"LEFT JOIN ITEM_GROUPS IG on IG.ID = I.GROUP_ID " +
			"WHERE I.ID = ? " +
			"LIMIT 200";
	}

	@Override
	public void setParams(PreparedStatement ps) throws SQLException
	{
		ps.setInt(1, itemID);
	}

	@Override
	public NERInfoItem convertRow(ResultSet rs) throws SQLException
	{
		try
		{
			return new NERInfoItem(
				rs.getInt("ID"),
				rs.getString("NAME"),
				rs.getString("EXAMINE_TEXT"),
				rs.getString("GROUP_NAME"),
				rs.getString("VERSION"),
				rs.getString("URL"),
				rs.getBoolean("IS_MEMBERS"),
				rs.getBoolean("IS_TRADEABLE")
			);
		}
		finally
		{
			rs.next();
		}
	}
}
