package com.notenoughrunes.ui;

import com.notenoughrunes.types.NERData;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
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


	NERItemPanel(NERItem item, ItemManager itemManager, NERData nerData, ClientThread clientThread)
	{
		this.item = item;
		this.sourcesPanel = new NERSourcesPanel(item, itemManager, nerData, clientThread);
		this.usesPanel = new NERUsesPanel();

		String useName = item.getInfoItem().getName().length() > item.getInfoItem().getGroup().length()
			? item.getInfoItem().getName()
			: item.getInfoItem().getGroup();

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(0, 0, 10, 0));
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
		itemNameLabel.setText(useName);
		itemInfoRight.add(itemNameLabel, gbc);
		gbc.gridy++;

		JLabel itemDesc = new JLabel();
		itemDesc.setForeground(Color.WHITE);
		itemDesc.setFont(FontManager.getRunescapeSmallFont());
		itemDesc.setHorizontalAlignment(JLabel.CENTER);
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

		JPanel tabContainer = new JPanel();
		tabContainer.setLayout(new BorderLayout());
		tabContainer.add(tabGroup, BorderLayout.NORTH);
		tabContainer.add(tabDisplay, BorderLayout.CENTER);

		add(itemInfo, BorderLayout.NORTH);
		add(tabContainer, BorderLayout.CENTER);
	}
}
