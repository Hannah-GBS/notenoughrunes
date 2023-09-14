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
			"WHERE LOWER(PR.OUTPUT_ITEM_NAME) = LOWER(?) " +
			"	AND (PR.OUTPUT_ITEM_VERSION IS NULL OR ? IS NULL OR LOWER(PR.OUTPUT_ITEM_VERSION) = LOWER(?)) " +
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
		int id = rs.getInt("PRODUCTION_RECIPES.ID");
		NERProductionRecipe res = new NERProductionRecipe(
			rs.getString("PRODUCTION_RECIPES.TICKS"),
			new ArrayList<>(),
			rs.getString("PRODUCTION_RECIPES.FACILITIES"),
			rs.getString("PRODUCTION_RECIPES.TOOLS"),
			new ArrayList<>(),
			rs.getBoolean("PRODUCTION_RECIPES.IS_MEMBERS"),
			rs.getString("PRODUCTION_RECIPES.OUTPUT_ITEM_NAME"),
			rs.getString("PRODUCTION_RECIPES.OUTPUT_ITEM_VERSION"),
			rs.getString("PRODUCTION_RECIPES.OUTPUT_QUANTITY"),
			rs.getString("PRODUCTION_RECIPES.OUTPUT_QUANTITY_NOTE"),
			rs.getString("PRODUCTION_RECIPES.OUTPUT_SUBTEXT")
		);

		do
		{
			if (rs.getInt("PRODUCTION_MATERIALS.ID") != 0)
			{
				NERProductionMaterial material = new NERProductionMaterial(
					rs.getString("PRODUCTION_MATERIALS.ITEM_NAME"),
					rs.getString("PRODUCTION_MATERIALS.ITEM_VERSION"),
					rs.getString("PRODUCTION_MATERIALS.QUANTITY")
				);

				if (!res.getMaterials().contains(material)) {
					res.getMaterials().add(material);
				}
			}

			if (rs.getInt("PRODUCTION_SKILLS.ID") != 0)
			{
				NERProductionSkill skill = new NERProductionSkill(
					rs.getString("PRODUCTION_SKILLS.NAME"),
					rs.getString("PRODUCTION_SKILLS.LEVEL"),
					rs.getString("PRODUCTION_SKILLS.EXPERIENCE"),
					rs.getBoolean("PRODUCTION_SKILLS.IS_BOOSTABLE")
				);

				if (!res.getSkills().contains(skill)) {
					res.getSkills().add(skill);
				}
			}
		} while (rs.next() && rs.getInt("PRODUCTION_RECIPES.ID") == id);

		// caller will call next() immediately after this, so prevent skipping a row
		rs.previous();
		return res;
	}
}
