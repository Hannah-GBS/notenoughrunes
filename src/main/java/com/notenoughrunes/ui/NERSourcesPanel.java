package com.notenoughrunes.ui;

import com.notenoughrunes.NotEnoughRunesConfig;
import com.notenoughrunes.NotEnoughRunesPlugin;
import com.notenoughrunes.SourceSectionType;
import com.notenoughrunes.config.DefaultOpenSources;
import com.notenoughrunes.db.H2DataProvider;
import com.notenoughrunes.db.queries.ItemDropSourcesQuery;
import com.notenoughrunes.db.queries.ItemProducedByQuery;
import com.notenoughrunes.db.queries.ItemSoldAtQuery;
import com.notenoughrunes.db.queries.ItemSpawnQuery;
import com.notenoughrunes.types.NERDropItem;
import com.notenoughrunes.types.NERDropSource;
import com.notenoughrunes.types.NERProductionRecipe;
import com.notenoughrunes.types.NERShop;
import com.notenoughrunes.types.NERSpawnItem;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
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
	private final H2DataProvider dataProvider;
	private final ItemManager itemManager;
	private final ClientThread clientThread;
	private final String useName;
	private final NERPanel mainPanel;
	private final NotEnoughRunesConfig config;

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

	NERSourcesPanel(NERItem nerItem, ItemManager itemManager, H2DataProvider dataProvider, ClientThread clientThread, NERPanel mainPanel, NotEnoughRunesConfig config)
	{
		this.nerItem = nerItem;
		this.dataProvider = dataProvider;
		this.itemManager = itemManager;
		this.clientThread = clientThread;
		this.mainPanel = mainPanel;
		this.config = config;

		this.useName = nerItem.getInfoItem().getName().length() > nerItem.getInfoItem().getGroup().length()
			? nerItem.getInfoItem().getName()
			: nerItem.getInfoItem().getGroup();

		setLayout(new BorderLayout());

		dataProvider.executeMany(new ItemProducedByQuery(nerItem.getInfoItem().getItemID()), recipes ->
			dataProvider.executeMany(new ItemDropSourcesQuery(useName), dropSources ->
				dataProvider.executeMany(new ItemSoldAtQuery(nerItem.getInfoItem().getItemID()), shops ->
					dataProvider.executeMany(new ItemSpawnQuery(nerItem.getInfoItem().getName(), nerItem.getInfoItem().getGroup()), spawns ->
						SwingUtilities.invokeLater(() ->
							buildPanel(recipes, dropSources, shops, spawns))))));
	}

	private void buildPanel(List<NERProductionRecipe> recipes, List<NERDropSource> dropSources, List<NERShop> shops, List<NERSpawnItem> spawns)
	{
		GridBagConstraints containerGbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.LINE_START, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
		JPanel recipeSection = createSection(SourceSectionType.RECIPES, recipes, dropSources, shops, spawns);
		JPanel container = new JPanel(new GridBagLayout());
		container.add(recipeSection, containerGbc);
		containerGbc.gridy++;
		JPanel dropsSection = createSection(SourceSectionType.DROPS, recipes, dropSources, shops, spawns);
		container.add(dropsSection, containerGbc);
		containerGbc.gridy++;
		JPanel shopsSection = createSection(SourceSectionType.SHOPS, recipes, dropSources, shops, spawns);
		container.add(shopsSection, containerGbc);
		containerGbc.gridy++;
		JPanel spawnsSection = createSection(SourceSectionType.SPAWNS, recipes, dropSources, shops, spawns);
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
		revalidate();
	}

	private JPanel createSection(SourceSectionType sectionType, List<NERProductionRecipe> recipes, List<NERDropSource> dropSources, List<NERShop> shops, List<NERSpawnItem> spawns)
	{
		ArrayList<JPanel> sectionItems = new ArrayList<>();
		switch (sectionType)
		{
			case RECIPES:
				recipes.forEach((recipe) ->
						sectionItems.add(new NERRecipePanel(recipe, itemManager, clientThread, mainPanel, useName, dataProvider)));
				break;

			case DROPS:
				if (!dropSources.isEmpty()) {
					sectionItems.add(new NERDropsPanel(new NERDropItem(useName, dropSources)));
				}
				break;

			case SHOPS:
				if (!shops.isEmpty()) {
					sectionItems.add(new NERShopsPanel(shops, nerItem, itemManager, clientThread, false, mainPanel));
				}
				break;

			case SPAWNS:
				spawns.forEach((spawnItem) ->
						sectionItems.add(new NERSpawnPanel(spawnItem)));
//					.limit(MAX_ENTRIES)

				break;
		}

		if (sectionItems.isEmpty())
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

		if (config.defaultOpenSources().contains(DefaultOpenSources.of(sectionType))) {
			toggleSection(sectionToggle, sectionContents);
		}

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
