package com.notenoughrunes.ui;

import com.notenoughrunes.NotEnoughRunesPlugin;
import com.notenoughrunes.types.NERData;
import com.notenoughrunes.types.NERProductionRecipe;
import com.notenoughrunes.types.NERShop;
import com.notenoughrunes.types.NERSpawnItem;
import static com.notenoughrunes.ui.NERPanel.MAX_ENTRIES;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;

@Slf4j
class NERSourcesPanel extends JPanel
{
	private static final ImageIcon SECTION_EXPAND_ICON;
	private static final ImageIcon SECTION_EXPAND_ICON_HOVER;
	private static final ImageIcon SECTION_RETRACT_ICON;
	private static final ImageIcon SECTION_RETRACT_ICON_HOVER;

	static final ImageIcon BACK_ICON;
	static final ImageIcon BACK_ICON_HOVER;

	private final NERItem nerItem;
	private final NERData nerData;
	private final ItemManager itemManager;
	private final ClientThread clientThread;
	private final String useName;
	private final NERPanel mainPanel;


	@AllArgsConstructor
	enum SectionType
	{
		RECIPES("Recipes", "Item recipes"),
		DROPS("Drops", "Item drop sources"),
		SHOPS("Shops", "Item shop sources"),
		SPAWNS("Spawns", "Item world spawns");

		private final String sectionName;
		private final String sectionDesc;
	}

	static
	{
		final BufferedImage backIcon = ImageUtil.loadImageResource(NotEnoughRunesPlugin.class, "ui/config_back_icon.png");
		BACK_ICON = new ImageIcon(backIcon);
		BACK_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(backIcon, -100));

		BufferedImage sectionRetractIcon = ImageUtil.loadImageResource(NotEnoughRunesPlugin.class, "ui/arrow_right.png");
		sectionRetractIcon = ImageUtil.luminanceOffset(sectionRetractIcon, -121);
		SECTION_EXPAND_ICON = new ImageIcon(sectionRetractIcon);
		SECTION_EXPAND_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(sectionRetractIcon, -100));
		final BufferedImage sectionExpandIcon = ImageUtil.rotateImage(sectionRetractIcon, Math.PI / 2);
		SECTION_RETRACT_ICON = new ImageIcon(sectionExpandIcon);
		SECTION_RETRACT_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(sectionExpandIcon, -100));
	}

	NERSourcesPanel(NERItem nerItem, ItemManager itemManager, NERData nerData, ClientThread clientThread, NERPanel mainPanel)
	{
		this.nerItem = nerItem;
		this.nerData = nerData;
		this.itemManager = itemManager;
		this.clientThread = clientThread;
		this.mainPanel = mainPanel;

		this.useName = nerItem.getInfoItem().getName().length() > nerItem.getInfoItem().getGroup().length()
			? nerItem.getInfoItem().getName()
			: nerItem.getInfoItem().getGroup();

		setLayout(new BorderLayout());

		GridBagConstraints containerGbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.LINE_START, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
		JPanel recipeSection = createSection(SectionType.RECIPES);
		JPanel container = new JPanel(new GridBagLayout());
		container.add(recipeSection, containerGbc);
		containerGbc.gridy++;
		JPanel dropsSection = createSection(SectionType.DROPS);
		container.add(dropsSection, containerGbc);
		containerGbc.gridy++;
		JPanel shopsSection = createSection(SectionType.SHOPS);
		container.add(shopsSection, containerGbc);
		containerGbc.gridy++;
		JPanel spawnsSection = createSection(SectionType.SPAWNS);
		container.add(spawnsSection, containerGbc);
		containerGbc.gridy++;


		JPanel wrapper = new JPanel(new BorderLayout());

		wrapper.setMaximumSize(new Dimension(220, Integer.MAX_VALUE));
		wrapper.add(container, BorderLayout.NORTH);

		JScrollPane scrollWrapper = new JScrollPane(wrapper);
		scrollWrapper.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
		scrollWrapper.getVerticalScrollBar().setBorder(new EmptyBorder(0, 5, 0, 0));
		scrollWrapper.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH, 1000));
		scrollWrapper.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		add(scrollWrapper, BorderLayout.CENTER);
	}

	private JPanel createSection(SectionType sectionType)
	{
		ArrayList<JPanel> sectionItems = new ArrayList<>();
		switch (sectionType)
		{
			case RECIPES:
				Set<NERProductionRecipe> recipes = nerData.getItemProductionData().stream()
					.filter(itemRecipe -> itemRecipe.getOutput().getName().equals(useName) && (itemRecipe.getOutput().getVersion() == null || nerItem.getInfoItem().getVersion() == null || itemRecipe.getOutput().getVersion().equals(nerItem.getInfoItem().getVersion())))
					.limit(MAX_ENTRIES)
					.collect(Collectors.toSet());

				recipes.forEach((recipe) ->
				{
					NERRecipePanel panel = new NERRecipePanel(recipe, itemManager, nerData, clientThread, mainPanel, useName);
					sectionItems.add(panel);
				});
				break;

			case DROPS:
				nerData.getItemDropData().stream()
					.filter(dropItem -> dropItem.getName().equals(useName))
					.findFirst()
					.map(NERDropsPanel::new)
					.ifPresent(sectionItems::add);
				break;

			case SHOPS:
				Set<NERShop> shops = nerData.getItemShopData().stream()
					.filter(shop -> shop.getItems().stream()
						.anyMatch(item -> item.getName().equals(useName) && (item.getVersion() == null || nerItem.getInfoItem().getVersion() == null || item.getVersion().equals(nerItem.getInfoItem().getVersion()))))
					.collect(Collectors.toSet());

				if (shops.size() < 1)
				{
					break;
				}

				NERShopsPanel panel = new NERShopsPanel(shops, nerItem, itemManager, clientThread, false);
				sectionItems.add(panel);

				break;

			case SPAWNS:
				nerData.getItemSpawnData().stream()
					.flatMap(spawnGroup -> spawnGroup.getSpawns().stream()
						.filter(spawnItem -> spawnItem.getName().equals(useName)))
					.distinct()
					.sorted(Comparator.comparing(NERSpawnItem::getLocation))
					.limit(MAX_ENTRIES)
					.map(NERSpawnPanel::new)
					.forEachOrdered(sectionItems::add);
				break;
		}

		if (sectionItems.size() < 1)
		{
			JPanel emptyPanel = new JPanel();
			emptyPanel.setMinimumSize(new Dimension(0, 0));
			emptyPanel.setMaximumSize(new Dimension(0, 0));
			emptyPanel.setPreferredSize(new Dimension(0, 0));
			return emptyPanel;
		}

		final JPanel section = new JPanel();
		section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
		section.setMinimumSize(new Dimension(210, 0));
		section.setMaximumSize(new Dimension(220, 0));


		final JPanel sectionHeader = new JPanel();
		sectionHeader.setLayout(new BorderLayout());
		sectionHeader.setMinimumSize(new Dimension(220, 0));
		// For whatever reason, the header extends out by a single pixel when closed. Adding a single pixel of
		// border on the right only affects the width when closed, fixing the issue.
		sectionHeader.setBorder(new CompoundBorder(
			new MatteBorder(0, 0, 1, 0, ColorScheme.MEDIUM_GRAY_COLOR),
			new EmptyBorder(0, 0, 3, 1)));
		section.add(sectionHeader, BorderLayout.NORTH);

		final JButton sectionToggle = new JButton(SECTION_EXPAND_ICON);
		sectionToggle.setRolloverIcon(SECTION_EXPAND_ICON_HOVER);
		sectionToggle.setPreferredSize(new Dimension(18, 0));
		sectionToggle.setBorder(new EmptyBorder(0, 0, 0, 5));
		sectionToggle.setToolTipText("Expand");
		SwingUtil.removeButtonDecorations(sectionToggle);
		sectionHeader.add(sectionToggle, BorderLayout.WEST);

		final JLabel sectionName = new JLabel(sectionType.sectionName);
		sectionName.setForeground(ColorScheme.BRAND_ORANGE);
		sectionName.setFont(FontManager.getRunescapeBoldFont());
		sectionName.setToolTipText(sectionType.sectionDesc);
		sectionHeader.add(sectionName, BorderLayout.CENTER);

		final JPanel sectionContents = new JPanel();
		sectionContents.setLayout(new DynamicGridLayout(0, 1, 0, 10));
		sectionContents.setMinimumSize(new Dimension(220, 0));
		sectionContents.setBorder(new CompoundBorder(
			new MatteBorder(0, 0, 1, 0, ColorScheme.MEDIUM_GRAY_COLOR),
			new EmptyBorder(PluginPanel.BORDER_OFFSET, 0, PluginPanel.BORDER_OFFSET, 0)));
		sectionContents.setVisible(false);
		section.add(sectionContents, BorderLayout.SOUTH);

		final MouseAdapter sectionAdapter = new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				toggleSection(sectionToggle, sectionContents);
			}
		};
		sectionToggle.addActionListener(actionEvent -> toggleSection(sectionToggle, sectionContents));
		sectionName.addMouseListener(sectionAdapter);
		sectionHeader.addMouseListener(sectionAdapter);

		sectionItems.forEach(sectionContents::add);

		return section;
	}

	private void toggleSection(JButton button, JPanel contents)
	{
		boolean newState = !contents.isVisible();
		contents.setVisible(newState);
		button.setIcon(newState ? SECTION_RETRACT_ICON : SECTION_EXPAND_ICON);
		button.setRolloverIcon(newState ? SECTION_RETRACT_ICON_HOVER : SECTION_EXPAND_ICON_HOVER);
		button.setToolTipText(newState ? "Retract" : "Expand");
		SwingUtilities.invokeLater(contents::revalidate);
	}
}
