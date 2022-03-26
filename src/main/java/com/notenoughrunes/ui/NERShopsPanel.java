package com.notenoughrunes.ui;

import com.notenoughrunes.types.NERShop;
import com.notenoughrunes.types.NERShopItem;
import static com.notenoughrunes.ui.NERPanel.MAX_ENTRIES;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.LINE_END;
import static java.awt.GridBagConstraints.LINE_START;
import static java.awt.GridBagConstraints.NONE;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;

@Slf4j
public class NERShopsPanel extends JPanel
{

	private static final Insets NO_INSETS = new Insets(0, 0, 0, 0);
	private final NERItem nerItem;
	private final ItemManager itemManager;
	private final ClientThread clientThread;


	public NERShopsPanel(Set<NERShop> shops, NERItem nerItem, ItemManager itemManager, ClientThread clientThread, boolean isCurrency)
	{
		this.nerItem = nerItem;
		this.itemManager = itemManager;
		this.clientThread = clientThread;

		String useName = nerItem.getInfoItem().getName().length() > nerItem.getInfoItem().getGroup().length()
			? nerItem.getInfoItem().getName()
			: nerItem.getInfoItem().getGroup();

		setMaximumSize(new Dimension(220, 80));
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//		setBorder(BorderFactory.createLineBorder(ColorScheme.LIGHT_GRAY_COLOR, 1));

		int row = 0;
		int entries = 0;

		shopLoop:
		for (NERShop shop : shops)
		{
			Set<NERShopItem> shopItems;
			if (!isCurrency)
			{
				shopItems = shop.getItems().stream()
					.filter(item -> item.getName().equals(useName))
					.collect(Collectors.toSet());
			}
			else
			{
				shopItems = shop.getItems().stream()
					.filter(item -> item.getCurrency().equals(useName))
					.collect(Collectors.toSet());
			}

			for (NERShopItem shopItem : shopItems)
			{
				if (entries >= MAX_ENTRIES)
				{
					break shopLoop;
				}
				JPanel container = new JPanel(new BorderLayout());
				container.setBorder(new EmptyBorder(0, 0, 7, 0));
				JPanel shopPanel = new JPanel(new GridBagLayout());
				shopPanel.setBorder(new EmptyBorder(7, 7, 6, 7));
				shopPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

				JLabel shopName = new JLabel();
				shopName.setMaximumSize(new Dimension(0, 20));
				shopName.setPreferredSize(new Dimension(0, 20));
				shopName.setText(shop.getName());
				shopName.setToolTipText(shop.getName());

				if (shop.isMembers())
				{
					shopName.setForeground(new Color(209, 174, 59));
				}

				shopPanel.add(shopName, new GridBagConstraints(0, row, 1, 1, 1.0, 0.0, LINE_START, BOTH, new Insets(0,4,0,0), 4, 4));
				int finalRow = row;
				clientThread.invokeLater(() -> {
					String sellPrice;
					if (shop.getSellMultiplier() != null)
					{
						sellPrice = String.valueOf(itemManager.getItemComposition(
							itemManager.canonicalize(nerItem.getInfoItem().getItemID())).getPrice() * (Integer.parseInt(shop.getSellMultiplier()) / 1000));
					}
					else
					{
						sellPrice = shopItem.getSellPrice();
					}

					if (shopItem.getCurrency().equals("Coins"))
					{
						sellPrice += "gp";
					}
					else
					{
						sellPrice += " " + shopItem.getCurrency();
					}
					String finalSellPrice = sellPrice;
					SwingUtilities.invokeLater(() ->
					{
						shopPanel.add(new JLabel(finalSellPrice), new GridBagConstraints(1, finalRow, 1, 1, 0.1, 0.0, LINE_END, NONE, NO_INSETS, 4, 4));
					});
				});
				row++;

				if (isCurrency)
				{
					shopPanel.add(new JLabel(shopItem.getName()), new GridBagConstraints(0, row, 2, 1, 0.0, 0.0, LINE_END, NONE, NO_INSETS, 4, 4));
					row++;
				}

				JLabel location = new JLabel();
				location.setMaximumSize(new Dimension(0, 20));
				location.setPreferredSize(new Dimension(0, 20));
				location.setText(shop.getLocation());
				location.setToolTipText(shop.getLocation());
				shopPanel.add(location, new GridBagConstraints(0, row, 1, 1, 1.0, 0.0, LINE_START, BOTH, new Insets(0,4,0,0), 4, 4));
				shopPanel.add(new JLabel("x" + shopItem.getStock()), new GridBagConstraints(1, row++, 1, 1, 0.1, 0.0, LINE_END, NONE, NO_INSETS, 4, 4));

				container.add(shopPanel);
				add(container);

				entries++;

//				JSeparator actionSeparator = new JSeparator(JSeparator.HORIZONTAL);
//				actionSeparator.setBackground(ColorScheme.LIGHT_GRAY_COLOR);
//				actionSeparator.setPreferredSize(new Dimension(1, 2));
////				add(actionSeparator, new GridBagConstraints(0, row++, 4, 1, 1.0, 0.0, CENTER, HORIZONTAL, NO_INSETS, 0, 0));
//				add(actionSeparator);

			}
		}
	}
}
