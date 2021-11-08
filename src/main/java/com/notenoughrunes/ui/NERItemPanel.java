package com.notenoughrunes.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.materialtabs.MaterialTab;
import net.runelite.client.ui.components.materialtabs.MaterialTabGroup;


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
	public NERItem item;


	NERItemPanel(NERItem item)
	{
		this.item = item;
		this.sourcesPanel = new NERSourcesPanel();
		this.usesPanel = new NERUsesPanel();

		setLayout(new BorderLayout(5, 5));
		setBorder(new EmptyBorder(0, 10, 10, 10));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		JLabel itemIcon = new JLabel();
		itemIcon.setPreferredSize(ICON_SIZE);
		itemIcon.setIcon(new ImageIcon(item.getIcon()));

		JPanel itemInfoRight = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		JLabel itemNameLabel = new JLabel();
		itemNameLabel.setForeground(ColorScheme.BRAND_ORANGE);
		itemNameLabel.setHorizontalAlignment(JLabel.CENTER);
		Font nameFont = FontManager.getRunescapeBoldFont();
		Map<TextAttribute, Object> attributes = new HashMap<>(nameFont.getAttributes());
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		itemNameLabel.setFont(FontManager.getRunescapeBoldFont().deriveFont(attributes).deriveFont(20f));
//		itemNameLabel.setMaximumSize(new Dimension(0, 0));
//		itemNameLabel.setPreferredSize(new Dimension(0, 22));
		itemNameLabel.setText(item.getInfoItem().getName());
		itemInfoRight.add(itemNameLabel, gbc);
		gbc.gridy++;

		JLabel itemDesc = new JLabel();
		itemDesc.setForeground(Color.WHITE);
		itemDesc.setFont(FontManager.getRunescapeSmallFont());
		itemDesc.setHorizontalAlignment(JLabel.CENTER);
//		itemDesc.setMaximumSize(new Dimension(0, 0));
//		itemDesc.setPreferredSize(new Dimension(170, 0));
		itemDesc.setVerticalAlignment(JLabel.NORTH);
		itemDesc.setText(String.format("<html><body style=\"text-justify: none; text-align: center; overflow: clip;\">%s</body></html>", item.getInfoItem().getExamineText()));
		itemInfoRight.add(itemDesc, gbc);
		gbc.gridy++;
		itemInfoRight.setBackground(getBackground());

		BorderLayout itemLayout = new BorderLayout();
		itemLayout.setHgap(5);
		JPanel itemInfo = new JPanel();
		itemInfo.setLayout(itemLayout);
		itemInfo.setBorder(new EmptyBorder(5, 5, 5, 0));
		itemInfo.add(itemIcon, BorderLayout.LINE_START);
		itemInfo.add(itemInfoRight, BorderLayout.CENTER);

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
