package com.notenoughrunes.ui;

import com.notenoughrunes.types.NERProductionMaterial;
import com.notenoughrunes.types.NERProductionRecipe;
import com.notenoughrunes.types.NERProductionSkill;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.LINE_START;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import net.runelite.api.Client;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

public class NERRecipePanel extends JPanel
{

	private static final Insets NO_INSETS = new Insets(0, 0, 0, 0);

	public NERRecipePanel(NERProductionRecipe recipe, Client client)
	{
		// client would be used for images

		setMaximumSize(new Dimension(PluginPanel.PANEL_WIDTH, 800));
		setBackground(Color.black);
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createLineBorder(Color.white, 1));

		int row = 0;
		add(new JLabel(recipe.getFacilities()), new GridBagConstraints(0, row++, 4, 1, 1.0, 0.0, CENTER, NONE, NO_INSETS, 4, 4));
		// todo images? i've left col0 unused all the way down

		for (NERProductionSkill skill : recipe.getSkills())
		{
			add(new JLabel(skill.getName()), new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, LINE_START, NONE, NO_INSETS, 4, 4));
			add(new JLabel("Lv" + skill.getLevel()), new GridBagConstraints(2, row, 1, 1, 0.2, 0.0, LINE_START, NONE, NO_INSETS, 4, 4));
			add(new JLabel(skill.getExperience() + "xp"), new GridBagConstraints(3, row++, 1, 1, 0.0, 0.0, LINE_START, NONE, NO_INSETS, 4, 4));
		}

		JSeparator actionSeparator = new JSeparator(JSeparator.HORIZONTAL);
		actionSeparator.setBackground(ColorScheme.LIGHT_GRAY_COLOR);
		actionSeparator.setPreferredSize(new Dimension(1, 10));
		add(actionSeparator, new GridBagConstraints(0, row++, 4, 1, 1.0, 0.0, CENTER, HORIZONTAL, NO_INSETS, 0, 0));

		for (NERProductionMaterial material : recipe.getMaterials())
		{
			add(new JLabel(material.getName()), new GridBagConstraints(1, row, 2, 1, 1.0, 0.0, WEST, NONE, NO_INSETS, 4, 4));
			add(new JLabel(material.getQuantity()), new GridBagConstraints(3, row++, 1, 1, 0.0, 0.0, CENTER, NONE, NO_INSETS, 4, 4));
		}

		JSeparator resultSeparator = new JSeparator(JSeparator.HORIZONTAL);
		resultSeparator.setBackground(ColorScheme.LIGHT_GRAY_COLOR);
		resultSeparator.setPreferredSize(new Dimension(1, 10));
		add(resultSeparator, new GridBagConstraints(0, row++, 4, 1, 1.0, 0.0, CENTER, HORIZONTAL, NO_INSETS, 0, 0));

		add(new JLabel(recipe.getOutput().getName()), new GridBagConstraints(1, row, 2, 1, 1.0, 0.0, WEST, NONE, NO_INSETS, 4, 4));
		add(new JLabel(recipe.getOutput().getQuantity()), new GridBagConstraints(3, row++, 1, 1, 0.0, 0.0, CENTER, NONE, NO_INSETS, 4, 4));
		add(new JLabel(recipe.getOutput().getSubtext()), new GridBagConstraints(0, row++, 4, 1, 0.0, 0.0, LINE_START, NONE, NO_INSETS, 4, 4));
	}

}
