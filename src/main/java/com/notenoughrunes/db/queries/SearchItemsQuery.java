package com.notenoughrunes.db.queries;

import com.notenoughrunes.types.NERInfoItem;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SearchItemsQuery extends ModeledQuery<NERInfoItem>
{

	private final String searchTerms;

	@Override
	public String getSql()
	{
		//language=SQL
		return "SELECT * FROM ITEMS I " +
			"LEFT JOIN ITEM_GROUPS IG on IG.ID = I.GROUP_ID " +
			"WHERE I.NAME LIKE ? " +
			"LIMIT 200";
	}

	@Override
	public void setParams(PreparedStatement ps) throws SQLException
	{
		ps.setString(1, searchTerms);
	}

	@Override
	public NERInfoItem convertRow(ResultSet rs) throws SQLException
	{
		return new NERInfoItem(
			rs.getInt("I.ID"),
			rs.getString("I.NAME"),
			rs.getString("I.EXAMINE_TEXT"),
			rs.getString("IG.NAME"),
			rs.getString("I.VERSION"),
			rs.getString("I.URL"),
			rs.getBoolean("I.IS_MEMBERS"),
			rs.getBoolean("I.IS_TRADEABLE")
		);
	}
}
