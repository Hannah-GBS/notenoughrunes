package com.notenoughrunes.db.queries;

import com.notenoughrunes.types.NERShop;
import com.notenoughrunes.types.NERShopItem;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ItemCurrencyQuery extends ModeledQuery<NERShop>
{
	
	private final String name;

	@Override
	public String getSql()
	{
		//language=SQL
		return "SELECT S.*, SI.*, S.ID AS SHOP_ID, S.NAME AS SHOP_NAME, SI.ID AS SHOP_ITEM_ID FROM SHOPS S " +
			"JOIN SHOP_ITEMS SI ON S.ID = SI.SHOP_ID " +
//			"WHERE EXISTS (" +
//			"	SELECT 1 FROM SHOP_ITEMS SI " +
//			"	WHERE S.ID = SI.SHOP_ID " +
//			"		AND SI.CURRENCY = ?)";
			// don't *need* all shop items, just matching ones
			"WHERE SI.CURRENCY = ?";
	}

	@Override
	public void setParams(PreparedStatement ps) throws SQLException
	{
		ps.setString(1, name);
	}

	@Override
	public NERShop convertRow(ResultSet rs) throws SQLException
	{
		int id = rs.getInt("SHOP_ID");
		NERShop res = new NERShop(
			rs.getString("SHOP_NAME"),
			rs.getString("SELL_MULTIPLIER"),
			rs.getString("LOCATION"),
			rs.getBoolean("IS_MEMBERS"),
			rs.getString("COORDS"),
			rs.getString("PLANE"),
			rs.getString("MAP_ID"),
			new ArrayList<>()
		);

		do
		{
			if (rs.getInt("SHOP_ITEM_ID") != 0)
			{
				NERShopItem shopItem = new NERShopItem(
					rs.getString("ITEM_NAME"),
					rs.getString("ITEM_VERSION"),
					rs.getInt("ITEM_ID"),
					rs.getString("CURRENCY"),
					rs.getString("STOCK"),
					rs.getString("BUY_PRICE"),
					rs.getString("SELL_PRICE")
				);

				if (!res.getItems().contains(shopItem))
				{
					res.getItems().add(shopItem);
				}
			}
		} while (rs.next() && rs.getInt("SHOP_ID") == id);

		return res;
	}
}
