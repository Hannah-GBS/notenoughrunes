package com.notenoughrunes.ui;

import com.notenoughrunes.RarityParser;
import com.notenoughrunes.types.NERDropItem;
import com.notenoughrunes.types.NERDropSource;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

public class NERDropsPanel extends JPanel
{
	
	private static DecimalFormat PERCENT_FORMAT = new DecimalFormat("#.##%");

	private static Map<String, BufferedImage> dropTypeImages = Stream.of(
		"combat",
		"reward",
		"hunter",
		"mining",
		"woodcutting",
		"fishing",
		"thieving",
		"farming"
	).collect(Collectors.toMap(
		Function.identity(),
		name ->
		{
			try
			{
				return ImageUtil.loadImageResource(NERDropsPanel.class, "drop_source_types/" + name + ".png");
			} catch (Exception e)
			{
				return new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
			}
		}
	));

	public NERDropsPanel(NERDropItem dropItem)
	{
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		dropItem.getDropSources().stream()
			.sorted(Comparator.comparing(RarityParser::calculateRarity, Comparator.reverseOrder()))
			.forEachOrdered(dropSource ->
			{
				gbc.gridx = 0;
				gbc.anchor = GridBagConstraints.LINE_START;
				gbc.gridwidth = 3;
				add(new JLabel(dropSource.getSource()), gbc);

				gbc.gridx = 0;
				gbc.gridy++;
				gbc.gridwidth = 1;
				add(new JLabel(buildQuantityString(dropSource)), gbc);

				JLabel dropType = new JLabel(dropSource.getDropLevel(), new ImageIcon(dropTypeImages.get(dropSource.getDropType())), SwingConstants.CENTER);
				dropType.setText(dropSource.getDropLevel());
				dropType.setIcon(new ImageIcon(dropTypeImages.get(dropSource.getDropType())));
				gbc.gridx = 1;
				gbc.weightx = 1;
				gbc.anchor = GridBagConstraints.CENTER;
				add(dropType, gbc);
				
				JLabel rarityLabel = new JLabel(dropSource.getRarity());
				if (dropSource.getRarity().contains("/"))
					addTooltip(rarityLabel, PERCENT_FORMAT.format(RarityParser.calculateRarity(dropSource)));
				gbc.gridx = 2;
				gbc.anchor = GridBagConstraints.LINE_END;
				add(rarityLabel, gbc);
				
				gbc.gridy++;
			});
		
	}

	private String buildQuantityString(NERDropSource dropSource)
	{
		if (dropSource.getQuantityLow() == dropSource.getQuantityHigh())
		{
			return "x" + dropSource.getQuantityLow();
		}

		return "x" + dropSource.getQuantityLow() + "-" + dropSource.getQuantityHigh();
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

}
