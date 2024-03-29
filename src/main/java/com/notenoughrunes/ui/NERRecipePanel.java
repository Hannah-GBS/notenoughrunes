package com.notenoughrunes.ui;

import com.notenoughrunes.db.H2DataProvider;
import com.notenoughrunes.db.queries.ItemByIDQuery;
import com.notenoughrunes.types.NERInfoItem;
import com.notenoughrunes.types.NERProductionMaterial;
import com.notenoughrunes.types.NERProductionRecipe;
import com.notenoughrunes.types.NERProductionSkill;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.LINE_END;
import static java.awt.GridBagConstraints.LINE_START;
import static java.awt.GridBagConstraints.NONE;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.ImageUtil;

@Slf4j
public class NERRecipePanel extends JPanel
{

	private static final Map<String, BufferedImage> facilityImages = Stream.of(
		"Anvil",
		"Apothecary",
		"Banner easel",
		"Barbarian anvil",
		"Big compost bin",
		"Blast furnace",
		"Brewery",
		"Clay oven",
		"Compost bin",
		"Cooking range",
		"Cooking range (2018 Easter event)",
		"Crafting table 1",
		"Crafting table 2",
		"Crafting table 3",
		"Crafting table 4",
		"Dairy churn",
		"Dairy cow",
		"Demon lectern",
		"Eagle lectern",
		"Eodan",
		"Fancy Clothes Store",
		"Farming patch",
		"Furnace",
		"Loom",
		"Mahogany demon lectern",
		"Mahogany eagle lectern",
		"Metal press",
		"Oak lectern",
		"Pluming stand",
		"Pottery wheel",
		"Sandpit",
		"Sawmill",
		"Sbott",
		"Shield easel",
		"Singing bowl",
		"Spinning wheel",
		"Tannery",
		"Taxidermist",
		"Teak demon lectern",
		"Teak eagle lectern",
		"Thakkrad Sigmundson",
		"Water",
		"Whetstone",
		"Windmill",
		"Woodcutting stump",
		"Workbench"
	).collect(Collectors.toMap(
		Function.identity(),
		name ->
		{
			try
			{
				return ImageUtil.loadImageResource(NERRecipePanel.class, "recipe_facility_icons/" + name + ".png");
			}
			catch (Exception e)
			{
				return new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
			}
		}
	));

	private static final Insets NO_INSETS = new Insets(0, 0, 0, 0);
	private final ItemManager itemManager;
	private final ClientThread clientThread;
	private final NERPanel mainPanel;
	private final H2DataProvider dataProvider;

	public NERRecipePanel(NERProductionRecipe recipe, ItemManager itemManager, ClientThread clientThread, NERPanel mainPanel, String useName, H2DataProvider dataProvider)
	{
		this.itemManager = itemManager;
		this.clientThread = clientThread;
		this.mainPanel = mainPanel;
		this.dataProvider = dataProvider;
		// client would be used for images

		setMaximumSize(new Dimension(PluginPanel.PANEL_WIDTH, 800));
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setLayout(new GridBagLayout());
		setBorder(new EmptyBorder(7, 7, 6, 7));
		int row = 0;

		if (recipe.getFacilities() != null)
		{
			JLabel facilityLabel = new JLabel(recipe.getFacilities());
			facilityLabel.setMaximumSize(new Dimension(0, 20));
			facilityLabel.setPreferredSize(new Dimension(0, 20));
			facilityLabel.setHorizontalAlignment(SwingConstants.CENTER);

			if (facilityImages.containsKey(recipe.getFacilities()))
			{
				facilityLabel.setIcon(new ImageIcon(facilityImages.get(recipe.getFacilities())));
			}

			add(facilityLabel, new GridBagConstraints(0, row++, 4, 1, 1.0, 0.0, CENTER, BOTH, NO_INSETS, 4, 4));
		}

		JPanel skillsPanel = new JPanel(new GridBagLayout());
		skillsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		int skillsRow = 0;

		for (NERProductionSkill skill : recipe.getSkills())
		{
			String iconPath = "/skill_icons/" + skill.getName().toLowerCase() + ".png";

			skillsPanel.add(new JLabel(new ImageIcon(ImageUtil.loadImageResource(getClass(), iconPath))), new GridBagConstraints(0, skillsRow, 1, 1, 0.0, 0.0, CENTER, NONE, NO_INSETS, 4, 4));
			skillsPanel.add(new JLabel(skill.getName()), new GridBagConstraints(1, skillsRow, 1, 1, 1.0, 0.0, LINE_START, NONE, NO_INSETS, 4, 4));
			skillsPanel.add(new JLabel("<html><body style=\"text-align:right\">Lv" + skill.getLevel() + "</body></html>"), new GridBagConstraints(2, skillsRow, 1, 1, 0.2, 0.0, LINE_END, NONE, NO_INSETS, 4, 4));
			skillsPanel.add(new JLabel(skill.getExperience() + "xp"), new GridBagConstraints(3, skillsRow++, 1, 1, 0.0, 0.0, LINE_END, NONE, NO_INSETS, 4, 4));
		}

		add(skillsPanel, new GridBagConstraints(0, row++, 4, 1, 0.0, 0.0, LINE_START, BOTH, NO_INSETS, 4, 4));

		JLabel materialHeader = new JLabel("Materials");
		add(materialHeader, new GridBagConstraints(0, row++, 4, 1, 1.0, 0.0, CENTER, NONE, NO_INSETS, 4, 4));

		if (recipe.getTools() != null)
		{
			addTooltip(materialHeader, "Tools: " + recipe.getTools());
		}

		for (NERProductionMaterial material : recipe.getMaterials())
		{
			JPanel materialRow = new JPanel(new GridBagLayout());
			materialRow.setBackground(ColorScheme.DARKER_GRAY_COLOR);
			JLabel materialLabel = new JLabel();
			setItemImage(materialLabel, material.getItemID());
			materialLabel.setText(material.getName());
			materialLabel.setToolTipText(material.getName());
			materialLabel.setMaximumSize(new Dimension(0, 30));
			materialLabel.setPreferredSize(new Dimension(0, 30));
			materialRow.add(materialLabel, new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0, LINE_START, BOTH, NO_INSETS, 4, 4));

			materialRow.add(new JLabel("x" + material.getQuantity()), new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, EAST, NONE, new Insets(0, 8, 0, 0), 4, 4));

			addMouseAdapter(materialLabel, material.getItemID(), useName);

			add(materialRow, new GridBagConstraints(0, row++, 4, 1, 0.0, 0.0, CENTER, BOTH, NO_INSETS, 4, 4));
		}



		add(new JLabel("Output"), new GridBagConstraints(0, row++, 4, 1, 1.0, 0.0, CENTER, NONE, NO_INSETS, 4, 4));
		JLabel outputLabel = new JLabel();
		setItemImage(outputLabel, recipe.getOutputItemID());
		outputLabel.setText(recipe.getOutputItemName());
		outputLabel.setToolTipText(recipe.getOutputItemName());
		outputLabel.setMaximumSize(new Dimension(0, 20));
		outputLabel.setPreferredSize(new Dimension(0, 20));

		add(outputLabel, new GridBagConstraints(0, row, 3, 1, 1.0, 0.0, LINE_START, BOTH, NO_INSETS, 4, 4));

		addMouseAdapter(outputLabel, recipe.getOutputItemID(), useName);

		JLabel quantityLabel = new JLabel("x" + recipe.getOutputQuantity());
		add(quantityLabel, new GridBagConstraints(3, row, 1, 1, 0.0, 0.0, LINE_END, NONE, new Insets(0, 4, 0, 0), 4, 4));
		if (recipe.getOutputQuantityNote() != null)
		{
			quantityLabel.setText("<html><body style=\"border-bottom: 1px dotted #ffffff\">" + quantityLabel.getText() + "*");
			String tooltipText = recipe.getOutputQuantityNote().replaceAll("[\\[\\]]|<[^>]*>", "");
			quantityLabel.setToolTipText(String.format("<html><p width=\"%d\">%s</p></html>", 200, tooltipText));
		}
	}

	private void setItemImage(JLabel label, int itemId)
	{
		clientThread.invokeLater(() ->
		{
			AsyncBufferedImage itemImage = this.itemManager.getImage(itemManager.canonicalize(itemId));
			SwingUtilities.invokeLater(() -> label.setIcon(new ImageIcon(itemImage)));
		});
	}

	private void displayItem(NERInfoItem itemInfo)
	{
		clientThread.invokeLater(() -> {
			AsyncBufferedImage icon = this.itemManager.getImage(itemManager.canonicalize(itemInfo.getItemID()));
			NERItem nerItem = new NERItem(icon, itemInfo);

			SwingUtilities.invokeLater(() -> mainPanel.displayItem(nerItem));
		});

	}

	private void addTooltip(JLabel toUnderline, String tooltip)
	{
		// add underline to text
		Font f = toUnderline.getFont();
		Map<TextAttribute, Object> attributes = new HashMap<>(f.getAttributes());
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_DOTTED);
		toUnderline.setFont(f.deriveFont(attributes));
		toUnderline.setText(toUnderline.getText());

		// create hover tooltip
		toUnderline.setToolTipText(tooltip);
	}

	private void addMouseAdapter(JLabel materialLabel, int itemID, String useName)
	{
		materialLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				dataProvider.executeSingle(new ItemByIDQuery(itemID), itemInfo ->
					SwingUtilities.invokeLater(() ->
					{
						if (itemInfo.getName().equals(useName) || itemInfo.getGroup().equals(useName)) {
							return;
						}
						displayItem(itemInfo);
					}));
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
	}
}
