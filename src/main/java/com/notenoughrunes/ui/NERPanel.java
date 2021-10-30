package com.notenoughrunes.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.ui.components.materialtabs.MaterialTab;
import net.runelite.client.ui.components.materialtabs.MaterialTabGroup;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.ImageUtil;
import sun.tools.jstat.Alignment;

@Slf4j
public class NERPanel extends PluginPanel
{
	private final GridBagConstraints constraints = new GridBagConstraints();

	private final IconTextField searchBar = new IconTextField();

	private final JPanel tabDisplay = new JPanel();

	private final MaterialTabGroup tabGroup = new MaterialTabGroup(tabDisplay);
	private final MaterialTab sourcesTab;
	private final MaterialTab usesTab;

	private static final Dimension ICON_SIZE = new Dimension(32, 32);

	@Inject
	private NERPanel(NERSourcesPanel sourcesPanel, NERUsesPanel usesPanel)
	{
		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		JPanel container = new JPanel();
		container.setLayout(new BorderLayout(5, 5));
		container.setBorder(new EmptyBorder(0, 10, 10, 10));
		container.setBackground(ColorScheme.DARK_GRAY_COLOR);

		searchBar.setIcon(IconTextField.Icon.SEARCH);
		searchBar.setPreferredSize(new Dimension(100, 30));
		searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);

		BufferedImage itemImage = ImageUtil.loadImageResource(getClass(), "arrow_shaft.png");
		JLabel itemIcon = new JLabel();
		itemIcon.setPreferredSize(ICON_SIZE);
		itemIcon.setIcon(new ImageIcon(itemImage));

		JPanel itemInfoRight = new JPanel(new GridLayout(2, 1));
		JLabel itemName = new JLabel();
		itemName.setForeground(ColorScheme.BRAND_ORANGE);
		itemName.setHorizontalAlignment(JLabel.CENTER);
		Font nameFont = FontManager.getRunescapeBoldFont();
		Map<TextAttribute, Object> attributes = new HashMap<>(nameFont.getAttributes());
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		itemName.setFont(FontManager.getRunescapeBoldFont().deriveFont(attributes).deriveFont(20f));
		itemName.setMaximumSize(new Dimension(0, 0));
		itemName.setPreferredSize(new Dimension(0, 0));
		itemName.setText("Arrow shaft");
		itemInfoRight.add(itemName);

		JLabel itemDesc = new JLabel();
		itemDesc.setForeground(Color.WHITE);
		itemDesc.setFont(FontManager.getRunescapeSmallFont());
		itemDesc.setHorizontalAlignment(JLabel.CENTER);
		itemDesc.setMaximumSize(new Dimension(0, 0));
		itemDesc.setPreferredSize(new Dimension(0, 0));
		itemDesc.setText("A wooden arrow shaft");
		itemInfoRight.add(itemDesc);

		BorderLayout itemLayout = new BorderLayout();
		itemLayout.setHgap(5);
		JPanel itemInfo = new JPanel();
		itemInfo.setLayout(itemLayout);
		itemInfo.setBorder(new EmptyBorder(5, 5, 5, 0));
		itemInfo.add(itemIcon, BorderLayout.LINE_START);
		itemInfo.add(itemInfoRight, BorderLayout.CENTER);
		itemInfoRight.setBackground(getBackground());

		sourcesTab = new MaterialTab("Sources", tabGroup, sourcesPanel);
		usesTab = new MaterialTab("Uses", tabGroup, usesPanel);

		tabGroup.setBorder(new EmptyBorder(0, 0, 0, 0));
		tabGroup.addTab(sourcesTab);
		tabGroup.addTab(usesTab);
		tabGroup.select(sourcesTab);


		add(searchBar, BorderLayout.NORTH);
		container.add(itemInfo, BorderLayout.NORTH);
		container.add(tabGroup, BorderLayout.CENTER);

		add(container, BorderLayout.CENTER);

	}
	
	
}
