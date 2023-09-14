package com.notenoughrunes.db.queries;

import com.notenoughrunes.types.NERInfoItem;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WideSearchItemsQuery extends ModeledQuery<NERInfoItem>
{

	private final String searchTerms;

	@Override
	public String getSql()
	{
		//language=SQL
		return "SELECT * FROM ITEMS I " +
			"LEFT JOIN ITEM_GROUPS IG on IG.ID = I.GROUP_ID " +
			"WHERE LOWER(I.NAME) LIKE LOWER(?) " +
			"OR LOWER(?) LIKE LOWER(CONCAT('%',I.NAME,'%'))" +
			"LIMIT 200";
	}

	@Override
	public void setParams(PreparedStatement ps) throws SQLException
	{
		ps.setString(1, "%" + searchTerms + "%");
		ps.setString(2, searchTerms);
	}

	@Override
	public NERInfoItem convertRow(ResultSet rs) throws SQLException
	{
		return new NERInfoItem(
			rs.getInt("ITEMS.ID"),
			rs.getString("ITEMS.NAME"),
			rs.getString("ITEMS.EXAMINE_TEXT"),
			rs.getString("ITEM_GROUPS.NAME"),
			rs.getString("ITEMS.VERSION"),
			rs.getString("ITEMS.URL"),
			rs.getBoolean("ITEMS.IS_MEMBERS"),
			rs.getBoolean("ITEMS.IS_TRADEABLE")
		);
	}
}
