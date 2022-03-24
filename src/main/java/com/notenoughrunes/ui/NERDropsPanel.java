package com.notenoughrunes.ui;

import com.notenoughrunes.RarityParser;
import com.notenoughrunes.types.NERDropItem;
import com.notenoughrunes.types.NERDropSource;
import static com.notenoughrunes.ui.NERPanel.MAX_ENTRIES;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

@Slf4j
public class NERDropsPanel extends JPanel
{
	
	private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("#.##%");

	private static final Map<String, BufferedImage> dropTypeImages = Stream.of(
		"combat",
		"reward",
		"hunter",
		"mining",
		"woodcutting",
		"fishing",
		"thieving",
		"farming",
		""
	).collect(Collectors.toMap(
		Function.identity(),
		name ->
		{
			try
			{
				if (name.equals(""))
				{
					return ImageUtil.loadImageResource(NERDropsPanel.class, "drop_source_types/combat.png");
				}
				else
				{
					return ImageUtil.loadImageResource(NERDropsPanel.class, "drop_source_types/" + name + ".png");
				}
			} catch (Exception e)
			{
				return new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
			}
		}
	));

	public NERDropsPanel(NERDropItem dropItem)
	{
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		dropItem.getDropSources().stream()
			.sorted(Comparator.comparing(RarityParser::calculateRarity, Comparator.reverseOrder()))
			.limit(MAX_ENTRIES)
			.forEachOrdered(dropSource ->
			{
				JPanel container = new JPanel(new BorderLayout());
				container.setBorder(new EmptyBorder(0, 0, 7, 0));
				JPanel dropPanel = new JPanel(new GridBagLayout());
				dropPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
				dropPanel.setBorder(new EmptyBorder(7, 7, 6, 7));

				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridy = 0;
				gbc.gridx = 0;
				gbc.anchor = GridBagConstraints.LINE_START;
				gbc.gridwidth = 3;
				gbc.fill = GridBagConstraints.BOTH;
				gbc.weightx = 1.0;
				gbc.insets = new Insets(0, 4, 0, 4);
				JLabel dropName = new JLabel(dropSource.getSource());
				dropName.setPreferredSize(new Dimension(0, 20));
				dropPanel.add(dropName, gbc);

				gbc.gridx = 0;
				gbc.gridy++;
				gbc.gridwidth = 1;
				gbc.weightx = 0.0;
				gbc.fill = GridBagConstraints.NONE;
				dropPanel.add(new JLabel(buildQuantityString(dropSource)), gbc);

				JLabel dropType = new JLabel(dropSource.getDropLevel(), new ImageIcon(dropTypeImages.get(dropSource.getDropType())), SwingConstants.CENTER);
				dropType.setText(dropSource.getDropLevel());
				dropType.setIcon(new ImageIcon(dropTypeImages.get(dropSource.getDropType())));
				dropType.setToolTipText(dropSource.getDropLevel());
				dropType.setPreferredSize(new Dimension(20, 20));
				gbc.gridx = 1;
				gbc.weightx = 1.0;
				gbc.anchor = GridBagConstraints.CENTER;
				gbc.fill = GridBagConstraints.BOTH;
				dropPanel.add(dropType, gbc);
				
				JLabel rarityLabel = new JLabel(dropSource.getRarity());
				if (dropSource.getRarity().contains("/"))
					addTooltip(rarityLabel, PERCENT_FORMAT.format(RarityParser.calculateRarity(dropSource)));
				gbc.gridx = 2;
				gbc.anchor = GridBagConstraints.LINE_END;
				gbc.fill = GridBagConstraints.NONE;
				gbc.weightx = 0.0;
				dropPanel.add(rarityLabel, gbc);

				gbc.gridx = 0;
				gbc.gridy++;
				gbc.gridwidth = 3;
				gbc.anchor = GridBagConstraints.CENTER;
				gbc.fill = GridBagConstraints.BOTH;
				gbc.insets = new Insets(4, 0, 4, 0);

//				JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
//				separator.setBackground(ColorScheme.LIGHT_GRAY_COLOR);
//				separator.setPreferredSize(new Dimension(1, 2));
//
//				dropPanel.add(separator, gbc);
//				gbc.gridy++;

				container.add(dropPanel);
				add(container);
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
