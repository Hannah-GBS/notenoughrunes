package com.notenoughrunes.ui;

import com.google.common.base.Strings;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.AsyncBufferedImage;

@Slf4j
class NERSearchResultsPanel extends JPanel
{
	private static final int MAX_RESULTS = 100;
	private static final String ERROR_PANEL = "ERROR_PANEL";
	private static final String RESULTS_PANEL = "RESULTS_PANEL";

	private final GridBagConstraints constraints = new GridBagConstraints();
	private final CardLayout cardLayout = new CardLayout();

	private final JPanel searchItemsPanel = new JPanel();
	private final JPanel centerPanel = new JPanel(cardLayout);
	private final PluginErrorPanel errorPanel = new PluginErrorPanel();



	private final Client client;
	private final ClientThread clientThread;
	private final ItemManager itemManager;

	private final List<NERItem> results = new ArrayList<>();


	@Value
	private static class ItemIcon
	{
		private final int modelId;
		private final short[] colorsToReplace;
		private final short[] texturesToReplace;
	}

	@Inject
	NERSearchResultsPanel(Client client, ClientThread clientThread, ItemManager itemManager)
	{
		this.client = client;
		this.clientThread = clientThread;
		this.itemManager = itemManager;

		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		/*  The main container, this holds the search bar and the center panel */
		JPanel container = new JPanel();
		container.setLayout(new BorderLayout(5, 5));
		container.setBorder(new EmptyBorder(5, 2, 0, 2));
		container.setBackground(ColorScheme.DARK_GRAY_COLOR);

		searchItemsPanel.setLayout(new GridBagLayout());
		searchItemsPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;

		/* This panel wraps the results panel and guarantees the scrolling behaviour */
		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
		wrapper.add(searchItemsPanel, BorderLayout.NORTH);

		/*  The results wrapper, this scrolling panel wraps the results container */
		JScrollPane resultsWrapper = new JScrollPane(wrapper);
		resultsWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
		resultsWrapper.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
		resultsWrapper.getVerticalScrollBar().setBorder(new EmptyBorder(0, 5, 0, 0));
		resultsWrapper.setVisible(false);

		/* This panel wraps the error panel and limits its height */
		JPanel errorWrapper = new JPanel(new BorderLayout());
		errorWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
		errorWrapper.add(errorPanel, BorderLayout.NORTH);

		errorPanel.setContent("Not Enough Runes Search",
			"Here you can search an item by its name to find its sources and uses");

		centerPanel.add(resultsWrapper, RESULTS_PANEL);
		centerPanel.add(errorWrapper, ERROR_PANEL);

		cardLayout.show(centerPanel, ERROR_PANEL);

		container.add(centerPanel, BorderLayout.CENTER);

		add(container, BorderLayout.CENTER);
	}

	boolean updateSearch(IconTextField searchBar)
	{
		String lookup = searchBar.getText();

		if (Strings.isNullOrEmpty(lookup))
		{
			searchItemsPanel.removeAll();
			errorPanel.setContent("Not Enough Runes Search",
				"Here you can search an item by its name to find its sources and uses");
			cardLayout.show(centerPanel, ERROR_PANEL);
			searchBar.setIcon(IconTextField.Icon.SEARCH);
			SwingUtilities.invokeLater(searchItemsPanel::updateUI);
			return false;
		}

		searchItemsPanel.removeAll();
		searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		searchBar.setEditable(false);
		searchBar.setIcon(IconTextField.Icon.LOADING);
		return true;
	}

	void itemSearch(IconTextField searchBar)
	{
		if (!updateSearch(searchBar))
		{
			return;
		}

		String search = searchBar.getText();
		results.clear();

		Set<ItemIcon> itemIcons = new HashSet<>();
		this.clientThread.invokeLater(() ->
		{
			for (int i = 0; i < this.client.getItemCount() && results.size() < MAX_RESULTS; i++)
			{
				ItemComposition itemComposition = this.itemManager.getItemComposition(this.itemManager.canonicalize(i));
				String name = itemComposition.getName().toLowerCase();

				if (!name.equals("null") && name.contains(search) && results.stream().noneMatch(s -> s.getItemId() == itemComposition.getId()))
				{
					ItemIcon itemIcon = new ItemIcon(itemComposition.getInventoryModel(),
						itemComposition.getColorToReplaceWith(), itemComposition.getTextureToReplaceWith());
					if (itemIcons.contains(itemIcon))
					{
						continue;
					}

					itemIcons.add(itemIcon);

					AsyncBufferedImage itemImage = this.itemManager.getImage(itemComposition.getId());
					results.add(new NERItem(itemImage, itemComposition.getName(), itemComposition.getId(), itemComposition.isMembers()));
				}
			}


			log.info(results.toString());
			if (results.isEmpty())
			{
				searchBar.setIcon(IconTextField.Icon.ERROR);
				errorPanel.setContent("No results found", "No items were found with that name, please try again.");
				cardLayout.show(centerPanel, ERROR_PANEL);
				searchBar.setEditable(true);
				return;
			}

			SwingUtilities.invokeLater(this::processResult);
			searchBar.setIcon(IconTextField.Icon.SEARCH);
		});
	}

	void processResult()
	{
		cardLayout.show(centerPanel, RESULTS_PANEL);
		int index = 0;
		if (results.size() == 0)
		{
			log.info("No results found");
			return;
		}
		for (NERItem nerItem : results)
		{
			if ((index + 1) > MAX_RESULTS)
			{
				break;
			}
			NERSearchItemPanel panel = new NERSearchItemPanel(nerItem);
			if (index++ > 0)
			{
				JPanel marginWrapper = new JPanel(new BorderLayout());
				marginWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
				marginWrapper.setBorder(new EmptyBorder(5, 0, 0, 0));
				marginWrapper.add(panel, BorderLayout.NORTH);
				searchItemsPanel.add(marginWrapper, constraints);
			}
			else
			{
				searchItemsPanel.add(panel, constraints);
			}

			constraints.gridy++;
		}
		log.info(results.toString());
	}
}
