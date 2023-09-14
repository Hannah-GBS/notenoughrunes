package com.notenoughrunes.db.queries;

import com.notenoughrunes.types.NERShop;
import com.notenoughrunes.types.NERShopItem;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
			"JOIN SHOP_ITEMS SI ON S.ID = SI.SHOP_ID " +
			"WHERE EXISTS (" +
			"	SELECT 1 FROM SHOP_ITEMS SI " +
			"	WHERE S.ID = SI.SHOP_ID " +
			"		AND SI.ITEM_NAME = ?" +
			"		AND (SI.ITEM_VERSION IS NULL OR ? IS NULL OR SI.ITEM_VERSION = ?))";
	}

	@Override
	public void setParams(PreparedStatement ps) throws SQLException
	{
		ps.setString(1, name);
		ps.setString(2, version);
		ps.setString(3, version);
	}

	@Override
	public NERShop convertRow(ResultSet rs) throws SQLException
	{
		int id = rs.getInt("SHOPS.ID");
		NERShop res = new NERShop(
			rs.getString("SHOPS.NAME"),
			rs.getString("SHOPS.SELL_MULTIPLIER"),
			rs.getString("SHOPS.LOCATION"),
			rs.getBoolean("SHOPS.IS_MEMBERS"),
			new ArrayList<>()
		);

		do
		{
			if (rs.getInt("SHOP_ITEMS.ID") != 0)
			{
				NERShopItem shopItem = new NERShopItem(
					rs.getString("SHOP_ITEMS.ITEM_NAME"),
					rs.getString("SHOP_ITEMS.ITEM_VERSION"),
					rs.getString("SHOP_ITEMS.CURRENCY"),
					rs.getString("SHOP_ITEMS.STOCK"),
					rs.getString("SHOP_ITEMS.BUY_PRICE"),
					rs.getString("SHOP_ITEMS.SELL_PRICE")
				);

				if (!res.getItems().contains(shopItem))
				{
					res.getItems().add(shopItem);
				}
			}
		} while (rs.next() && rs.getInt("SHOPS.ID") == id);

		// caller will call next() immediately after this, so prevent skipping a row
		rs.previous();
		return res;
	}
}
