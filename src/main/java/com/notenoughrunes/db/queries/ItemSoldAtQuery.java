package com.notenoughrunes.db.queries;

import com.notenoughrunes.types.NERShop;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ItemSoldAtQuery extends ModeledQuery<NERShop>
{
	
	private final String name;
	private final String version;

	@Override
	public String getSql()
	{
		//language=SQL
		return "SELECT * FROM SHOPS S " +
			"WHERE (" +
			"	SELECT COUNT(*) FROM SHOP_ITEMS SI " +
			"	WHERE S.ID = SI.SHOP_ID " +
			"		AND SI.ITEM_NAME = ?" +
			"		AND (SI.ITEM_VERSION IS NULL OR ? IS NULL OR SI.ITEM_VERSION = ?))) AS S " +
			"JOIN SHOP_ITEMS SI ON S.ID = SI.SHOP_ID " +
			"	AND (SELECT COUNT(*) FROM SHOP_ITEMS WHERE)" +
			"WHERE SI.";
	}

	@Override
	public void setParams(PreparedStatement ps) throws SQLException
	{

	}

	@Override
	public NERShop convertRow(ResultSet rs) throws SQLException
	{
		return null;
	}
}
