package com.notenoughrunes.db.queries;

import com.notenoughrunes.types.NERProductionMaterial;
import com.notenoughrunes.types.NERProductionRecipe;
import com.notenoughrunes.types.NERProductionSkill;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ItemProducedByQuery extends ModeledQuery<NERProductionRecipe>
{

	private final String name;
	private final String version;

	@Override
	public String getSql()
	{
		//language=SQL
		return "SELECT * " +
			"FROM PRODUCTION_RECIPES PR " +
			"LEFT JOIN PRODUCTION_MATERIALS PM on PR.ID = PM.RECIPE_ID " +
			"LEFT JOIN PRODUCTION_SKILLS PS on PR.ID = PS.RECIPE_ID " +
			"WHERE PR.OUTPUT_ITEM_NAME = ? " +
			"	AND (PR.OUTPUT_ITEM_VERSION IS NULL OR ? IS NULL OR PR.OUTPUT_ITEM_VERSION = ?) " +
			"ORDER BY PR.ID " +
			"LIMIT 100";
	}

	@Override
	public void setParams(PreparedStatement ps) throws SQLException
	{
		ps.setString(1, name);
		ps.setString(2, version);
		ps.setString(3, version);
	}

	@Override
	public NERProductionRecipe convertRow(ResultSet rs) throws SQLException
	{
		int id = rs.getInt("PR.ID");
		NERProductionRecipe res = new NERProductionRecipe(
			rs.getString("PR.TICKS"),
			new ArrayList<>(),
			rs.getString("PR.FACILITIES"),
			rs.getString("PR.TOOLS"),
			new ArrayList<>(),
			rs.getBoolean("PR.IS_MEMBERS"),
			rs.getString("PR.OUTPUT_ITEM_NAME"),
			rs.getString("PR.OUTPUT_ITEM_VERSION"),
			rs.getString("PR.OUTPUT_QUANTITY"),
			rs.getString("PR.OUTPUT_QUANTITY_NOTE"),
			rs.getString("PR.OUTPUT_SUBTEXT")
		);

		do
		{
			if (rs.getInt("PM.ID") != 0)
			{
				res.getMaterials().add(new NERProductionMaterial(
					rs.getString("PM.ITEM_NAME"),
					rs.getString("PM.ITEM_VERSION"),
					rs.getString("PM.QUANTITY")
				));
			}

			if (rs.getInt("PS.ID") != 0)
			{
				res.getSkills().add(new NERProductionSkill(
					rs.getString("PS.NAME"),
					rs.getString("PS.LEVEL"),
					rs.getString("PS.EXPERIENCE"),
					rs.getBoolean("PS.IS_BOOSTABLE")
				));
			}
		} while (rs.next() && rs.getInt("PR.ID") == id);

		// caller will call next() immediately after this, so prevent skipping a row
		rs.previous();
		return res;
	}
}
