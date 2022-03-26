package com.notenoughrunes.ui;

import com.notenoughrunes.types.NERData;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.BOTH;
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
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.materialtabs.MaterialTab;
import net.runelite.client.ui.components.materialtabs.MaterialTabGroup;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;

@Slf4j
class NERItemPanel extends JPanel
{
	private static final Dimension ICON_SIZE = new Dimension(38, 32);

	private static final BufferedImage WIKI_ICON_DESELECTED = ImageUtil.loadImageResource(NERItemPanel.class, "wiki_icon_deselected.png");
	private static final BufferedImage WIKI_ICON_SELECTED = ImageUtil.loadImageResource(NERItemPanel.class, "wiki_icon_selected.png");

	@Getter
	private final NERSourcesPanel sourcesPanel;

	@Getter
	private final NERUsesPanel usesPanel;
	public NERItem item;


	NERItemPanel(NERItem item, ItemManager itemManager, NERData nerData, ClientThread clientThread, NERPanel mainPanel)
	{
		this.item = item;
		this.sourcesPanel = new NERSourcesPanel(item, itemManager, nerData, clientThread, mainPanel);
		this.usesPanel = new NERUsesPanel(item, itemManager, nerData, clientThread, mainPanel);

		log.debug("Creating item panel: " + item.getInfoItem().getName());

		String useName = item.getInfoItem().getName().length() > item.getInfoItem().getGroup().length()
			? item.getInfoItem().getName()
			: item.getInfoItem().getGroup();

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(0, 0, 10, 0));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		JLabel itemIcon = new JLabel(new ImageIcon(item.getIcon()), SwingConstants.RIGHT);
		itemIcon.setPreferredSize(ICON_SIZE);

		JLabel wikiIcon = new JLabel(new ImageIcon(WIKI_ICON_DESELECTED));
		wikiIcon.setPreferredSize(new Dimension(40, 20));
		wikiIcon.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent mouseEvent)
			{
				LinkBrowser.browse(item.getInfoItem().getUrl());
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				wikiIcon.setIcon(new ImageIcon(WIKI_ICON_SELECTED));
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				wikiIcon.setIcon(new ImageIcon(WIKI_ICON_DESELECTED));
			}
		});

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

		JPanel itemInfo = new JPanel();
		itemInfo.setLayout(new GridBagLayout());
		itemInfo.setBorder(new EmptyBorder(5, 5, 5, 0));
		itemInfo.add(itemIcon, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, LINE_START, NONE, new Insets(0, 0, 0, 0), 4, 4));
		itemInfo.add(wikiIcon, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, LINE_START, NONE, new Insets(0, 0, 0, 0), 4, 10));
		itemInfo.add(itemInfoRight, new GridBagConstraints(1, 0, 1, 2, 1.0, 0.0, LINE_END, BOTH, new Insets(0, 0, 0, 0), 4, 4));


		JPanel tabDisplay = new JPanel();
		MaterialTabGroup tabGroup = new MaterialTabGroup(tabDisplay);
		MaterialTab sourcesTab = new MaterialTab("Sources", tabGroup, sourcesPanel);
		MaterialTab usesTab = new MaterialTab("Uses", tabGroup, usesPanel);

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
