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
			"WHERE I.SEARCH_NAME LIKE ? " +
			"LIMIT 200";
	}

	@Override
	public void setParams(PreparedStatement ps) throws SQLException
	{
		ps.setString(1, "%" + searchTerms.toLowerCase() + "%");
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
				rs.getString("NAME"),
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
