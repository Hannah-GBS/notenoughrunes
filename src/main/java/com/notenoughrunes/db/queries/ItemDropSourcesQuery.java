package com.notenoughrunes.db.queries;

import com.notenoughrunes.types.NERDropSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ItemDropSourcesQuery extends ModeledQuery<NERDropSource>
{

	private final String itemName;

	@Override
	public String getSql()
	{
		//language=SQL	
		return "SELECT * FROM DROP_SOURCES DS " +
			"WHERE ITEM_NAME = ?";
	}

	@Override
	public void setParams(PreparedStatement ps) throws SQLException
	{
		ps.setString(1, itemName);
	}

	@Override
	public NERDropSource convertRow(ResultSet rs) throws SQLException
	{
		return new NERDropSource(
			rs.getString("DROP_SOURCES.SOURCE"),
			rs.getInt("DROP_SOURCES.QUANTITY_LOW"),
			rs.getInt("DROP_SOURCES.QUANTITY_HIGH"),
			rs.getString("DROP_SOURCES.RARITY"),
			rs.getString("DROP_SOURCES.DROP_LEVEL"),
			rs.getString("DROP_SOURCES.DROP_TYPE")
		);
	}
}
