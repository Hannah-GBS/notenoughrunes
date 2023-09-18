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
public class ItemProducesQuery extends ModeledQuery<NERProductionRecipe>
{

	private final int itemID;

	@Override
	public String getSql()
	{
		//language=SQL
		return "SELECT PR.*, PS.*, PM.*, PR.ID AS PR_ID, PS.ID AS PS_ID, PM.ID AS PM_ID " +
			"FROM PRODUCTION_MATERIALS PM_QUERY " +
			"JOIN PRODUCTION_RECIPES PR ON PM_QUERY.RECIPE_ID = PR.ID " +
			"LEFT JOIN PRODUCTION_SKILLS PS ON PR.ID = PS.RECIPE_ID " +
			"LEFT JOIN PRODUCTION_MATERIALS PM ON PR.ID = PM.RECIPE_ID " +
			"WHERE PM_QUERY.ITEM_ID = ? " +
			"LIMIT 100";
	}

	@Override
	public void setParams(PreparedStatement ps) throws SQLException
	{
		ps.setInt(1, itemID);
	}

	@Override
	public NERProductionRecipe convertRow(ResultSet rs) throws SQLException
	{
		int id = rs.getInt("PR_ID");
		NERProductionRecipe res = new NERProductionRecipe(
			rs.getString("TICKS"),
			new ArrayList<>(),
			rs.getString("FACILITIES"),
			rs.getString("TOOLS"),
			new ArrayList<>(),
			rs.getBoolean("IS_MEMBERS"),
			rs.getString("OUTPUT_ITEM_NAME"),
			rs.getString("OUTPUT_ITEM_VERSION"),
			rs.getInt("OUTPUT_ITEM_ID"),
			rs.getString("OUTPUT_QUANTITY"),
			rs.getString("OUTPUT_QUANTITY_NOTE"),
			rs.getString("OUTPUT_SUBTEXT")
		);

		do
		{
			if (rs.getInt("PM_ID") != 0)
			{
				NERProductionMaterial material = new NERProductionMaterial(
					rs.getString("ITEM_NAME"),
					rs.getString("ITEM_VERSION"),
					rs.getInt("ITEM_ID"),
					rs.getString("QUANTITY")
				);

				if (!res.getMaterials().contains(material)) {
					res.getMaterials().add(material);
				}
			}

			if (rs.getInt("PS_ID") != 0)
			{
				NERProductionSkill skill = new NERProductionSkill(
					rs.getString("NAME"),
					rs.getString("LEVEL"),
					rs.getString("EXPERIENCE"),
					rs.getBoolean("IS_BOOSTABLE")
				);

				if (!res.getSkills().contains(skill)) {
					res.getSkills().add(skill);
				}
			}
		} while (rs.next() && rs.getInt("PR_ID") == id);

		return res;
	}
}
