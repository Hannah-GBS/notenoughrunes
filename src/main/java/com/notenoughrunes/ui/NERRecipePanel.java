package com.notenoughrunes.ui;

import com.notenoughrunes.types.NERData;
import com.notenoughrunes.types.NERProductionMaterial;
import com.notenoughrunes.types.NERProductionRecipe;
import com.notenoughrunes.types.NERProductionSkill;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.LINE_END;
import static java.awt.GridBagConstraints.LINE_START;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.REMAINDER;
import static java.awt.GridBagConstraints.WEST;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.ImageUtil;

public class NERRecipePanel extends JPanel
{

	private static final Insets NO_INSETS = new Insets(0, 0, 0, 0);
	private final ItemManager itemManager;
	private final NERData nerData;
	private final ClientThread clientThread;

	public NERRecipePanel(NERProductionRecipe recipe, ItemManager itemManager, NERData nerData, ClientThread clientThread)
	{
		this.itemManager = itemManager;
		this.nerData = nerData;
		this.clientThread = clientThread;
		// client would be used for images

		setMaximumSize(new Dimension(PluginPanel.PANEL_WIDTH, 800));
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createLineBorder(ColorScheme.LIGHT_GRAY_COLOR, 1));

		int row = 0;
		add(new JLabel(recipe.getFacilities()), new GridBagConstraints(0, row++, 4, 1, 1.0, 0.0, CENTER, NONE, NO_INSETS, 4, 4));
		// todo images? i've left col0 unused all the way down

		for (NERProductionSkill skill : recipe.getSkills())
		{
			String iconPath = "/skill_icons/" + skill.getName().toLowerCase() + ".png";

			add(new JLabel(new ImageIcon(ImageUtil.loadImageResource(getClass(), iconPath))), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, CENTER, NONE, NO_INSETS, 4, 4));
			add(new JLabel(skill.getName()), new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, LINE_START, NONE, NO_INSETS, 4, 4));
			add(new JLabel("Lv" + skill.getLevel()), new GridBagConstraints(2, row, 1, 1, 0.2, 0.0, LINE_START, NONE, NO_INSETS, 4, 4));
			add(new JLabel(skill.getExperience() + "xp"), new GridBagConstraints(3, row++, 1, 1, 0.0, 0.0, LINE_END, NONE, NO_INSETS, 4, 4));
		}

		JSeparator actionSeparator = new JSeparator(JSeparator.HORIZONTAL);
		actionSeparator.setBackground(ColorScheme.LIGHT_GRAY_COLOR);
		actionSeparator.setPreferredSize(new Dimension(1, 6));
		add(actionSeparator, new GridBagConstraints(0, row++, 4, 1, 1.0, 0.0, CENTER, HORIZONTAL, NO_INSETS, 0, 0));
		add(new JLabel("Materials"), new GridBagConstraints(0, row++, 4, 1, 1.0, 0.0, CENTER, NONE, NO_INSETS, 4, 4));


		for (NERProductionMaterial material : recipe.getMaterials())
		{
			JLabel iconLabel = new JLabel();
			setItemImage(iconLabel, material.getName());
			add(iconLabel, new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(0, 8, 0, 0), 4, 4));
			add(new JLabel(material.getName()), new GridBagConstraints(1, row, 2, 1, 1.0, 0.0, WEST, NONE, NO_INSETS, 4, 4));
			add(new JLabel("x" + material.getQuantity()), new GridBagConstraints(3, row++, 1, 1, 0.0, 0.0, EAST, NONE, NO_INSETS, 4, 4));
		}

		JSeparator resultSeparator = new JSeparator(JSeparator.HORIZONTAL);
		resultSeparator.setBackground(ColorScheme.LIGHT_GRAY_COLOR);
		resultSeparator.setPreferredSize(new Dimension(1, 2));
		add(resultSeparator, new GridBagConstraints(0, row++, 4, 1, 1.0, 0.0, CENTER, HORIZONTAL, NO_INSETS, 0, 0));

		add(new JLabel("Output"), new GridBagConstraints(0, row++, 4, 1, 1.0, 0.0, CENTER, NONE, NO_INSETS, 4, 4));
		JLabel outputIcon = new JLabel();
		setItemImage(outputIcon, recipe.getOutput().getName());
		add(outputIcon, new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(0, 8, 0, 0), 4, 4));
		add(new JLabel(recipe.getOutput().getName()), new GridBagConstraints(1, row, 2, 1, 1.0, 0.0, WEST, NONE, NO_INSETS, 4, 4));
		add(new JLabel("x" + recipe.getOutput().getQuantity()), new GridBagConstraints(3, row++, 1, 1, 0.0, 0.0, LINE_END, NONE, NO_INSETS, 4, 4));
		if (recipe.getOutput().getQuantityNote() != null)
		{
			add(new JLabel(String.format("<html><body style=\"text-align:center;\">%s</body></html> ", recipe.getOutput().getQuantityNote().replaceAll("[\\[\\]]|<[^>]*>",""))), new GridBagConstraints(0, row++, REMAINDER, 1, 1.0, 0.0, CENTER, HORIZONTAL, NO_INSETS, 4, 4));
		}
	}

	private void setItemImage(JLabel label, String itemName)
	{
		int itemId = Objects.requireNonNull(nerData.getItemInfoData().stream()
			.filter(item -> item.getName().equals(itemName))
			.findFirst()
			.orElse(null))
			.getItemID();

		clientThread.invokeLater(() -> {
			AsyncBufferedImage itemImage = this.itemManager.getImage(itemManager.canonicalize(itemId));
			SwingUtilities.invokeLater(() -> label.setIcon(new ImageIcon(itemImage)));
		});
	}

}
