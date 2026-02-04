package com.notenoughrunes.ui;

import com.notenoughrunes.db.H2DataProvider;
import com.notenoughrunes.db.queries.ItemGroupQuery;
import com.notenoughrunes.types.NERSpawnGroup;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;

@Slf4j
class NERSearchItemPanel extends JPanel
{
	private static final Dimension ICON_SIZE = new Dimension(32, 32);

	NERSearchItemPanel(NERItemGroup itemGroup, NERPanel mainPanel)
	{
			log.debug("creating search item panel group: {}", itemGroup.getGroup());
			Optional<NERItem> searchedItemOpt = itemGroup.getItems().stream().filter((groupItem) -> Objects.equals(groupItem.getInfoItem().getVersion(), itemGroup.getSelectedVersion())).findFirst();
			NERItem searchedItem = searchedItemOpt.orElseGet(() -> itemGroup.getItems().get(0));
			log.debug(String.valueOf(searchedItem));
			log.debug("creating search item panel: {}", searchedItem.getInfoItem().getName());

			BorderLayout layout = new BorderLayout();
			layout.setHgap(5);
			setLayout(layout);
			setToolTipText(searchedItem.getInfoItem().getName());
			setBackground(ColorScheme.DARKER_GRAY_COLOR);

			Color background = getBackground();
			List<JPanel> panels = new ArrayList<>();
			panels.add(this);

			MouseAdapter itemPanelMouseListener = new MouseAdapter()
			{
				@Override
				public void mouseEntered(MouseEvent e)
				{
					for (JPanel panel : panels)
					{
						matchComponentBackground(panel, ColorScheme.DARK_GRAY_HOVER_COLOR);
					}
					setCursor(new Cursor(Cursor.HAND_CURSOR));
				}

				@Override
				public void mouseExited(MouseEvent e)
				{
					for (JPanel panel : panels)
					{
						matchComponentBackground(panel, background);
					}
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}

				@Override
				public void mouseClicked(MouseEvent e)
				{
//				log.info("Clicked " + item.getInfoItem().getName());
					mainPanel.displayItem(itemGroup);
				}

			};

			addMouseListener(itemPanelMouseListener);

			setBorder(new EmptyBorder(5, 5, 5, 0));

			JLabel itemIcon = new JLabel();
			itemIcon.setPreferredSize(ICON_SIZE);
			if (searchedItem.getIcon() != null)
			{
				searchedItem.getIcon().addTo(itemIcon);
			}
			add(itemIcon, BorderLayout.LINE_START);

			// Item details panel
			JPanel rightPanel = new JPanel(new GridLayout(1, 1));
			panels.add(rightPanel);
			rightPanel.setBackground(background);

			JLabel itemName = new JLabel();
			itemName.setForeground(Color.WHITE);
			itemName.setMaximumSize(new Dimension(0, 0));
			itemName.setPreferredSize(new Dimension(0, 0));
			itemName.setText(searchedItem.getInfoItem().getGroup());
			rightPanel.add(itemName);

			add(rightPanel, BorderLayout.CENTER);
		;

	}

	private void matchComponentBackground(JPanel panel, Color color)
	{
		panel.setBackground(color);
		for (Component c : panel.getComponents())
		{
			c.setBackground(color);
		}
	}
}
