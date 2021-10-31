package com.notenoughrunes.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.materialtabs.MaterialTab;
import net.runelite.client.ui.components.materialtabs.MaterialTabGroup;
import net.runelite.client.util.ImageUtil;


class NERItemPanel extends JPanel
{
	private static final Dimension ICON_SIZE = new Dimension(32, 32);

	private final JPanel tabDisplay = new JPanel();
	private final MaterialTabGroup tabGroup = new MaterialTabGroup(tabDisplay);
	private final MaterialTab sourcesTab;
	private final MaterialTab usesTab;

	@Getter
	private final NERSourcesPanel sourcesPanel;

	@Getter
	private final NERUsesPanel usesPanel;


	NERItemPanel(String itemName)
	{
		this.sourcesPanel = new NERSourcesPanel();
		this.usesPanel = new NERUsesPanel();

		setLayout(new BorderLayout(5, 5));
		setBorder(new EmptyBorder(0, 10, 10, 10));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		BufferedImage itemImage = ImageUtil.loadImageResource(getClass(), "arrow_shaft.png");
		JLabel itemIcon = new JLabel();
		itemIcon.setPreferredSize(ICON_SIZE);
		itemIcon.setIcon(new ImageIcon(itemImage));

		JPanel itemInfoRight = new JPanel(new GridLayout(2, 1));
		JLabel itemNameLabel = new JLabel();
		itemNameLabel.setForeground(ColorScheme.BRAND_ORANGE);
		itemNameLabel.setHorizontalAlignment(JLabel.CENTER);
		Font nameFont = FontManager.getRunescapeBoldFont();
		Map<TextAttribute, Object> attributes = new HashMap<>(nameFont.getAttributes());
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		itemNameLabel.setFont(FontManager.getRunescapeBoldFont().deriveFont(attributes).deriveFont(20f));
		itemNameLabel.setMaximumSize(new Dimension(0, 0));
		itemNameLabel.setPreferredSize(new Dimension(0, 0));
		itemNameLabel.setText(itemName);
		itemInfoRight.add(itemNameLabel);

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

		add(itemInfo, BorderLayout.NORTH);
		add(tabGroup, BorderLayout.CENTER);
	}
}
