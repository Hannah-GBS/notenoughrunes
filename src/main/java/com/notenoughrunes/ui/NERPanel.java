package com.notenoughrunes.ui;

import com.google.common.base.Strings;
import com.notenoughrunes.NotEnoughRunesConfig;
import com.notenoughrunes.NotEnoughRunesPlugin;
import com.notenoughrunes.types.NERData;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;

@Slf4j
public class NERPanel extends PluginPanel
{
	private final NotEnoughRunesPlugin plugin;
	private final NotEnoughRunesConfig config;
	private final IconTextField searchBar = new IconTextField();
	private final ClientThread clientThread;
	private final ItemManager itemManager;
	private final NERData nerData;

	public final static int MAX_ENTRIES = 100;

	private NERItemPanel itemPanel;
//	private NERItemPanel prevItemPanel;

	private JPanel currentPanel;

	@Getter
	private final NERSearchResultsPanel searchResultsPanel;

	@Inject
	private NERPanel(ScheduledExecutorService executor, NotEnoughRunesPlugin plugin, NotEnoughRunesConfig config, Client client, ClientThread clientThread, ItemManager itemManager, NERData nerData)
	{
		super(false);
		this.plugin = plugin;
		this.config = config;
		this.searchResultsPanel = new NERSearchResultsPanel(plugin, client, clientThread, itemManager, this);
		this.clientThread = clientThread;
		this.itemManager = itemManager;
		this.nerData = nerData;

		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setBorder(new EmptyBorder(5, 5, 5, 5));

		searchBar.setIcon(IconTextField.Icon.SEARCH);
		searchBar.setPreferredSize(new Dimension(100, 30));
		searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
		searchBar.addActionListener(e -> executor.execute(this::itemSearch));
		searchBar.addClearListener(this::updateSearch);

		add(searchBar, BorderLayout.NORTH);
		add(searchResultsPanel);
	}

	private void updateSearch()
	{
		searchResultsPanel.updateSearch(searchBar);

//		if (prevItemPanel != null)
//		{
//			log.info("previous item exists");
//			SwingUtilities.invokeLater(() ->
//			{
//				itemPanel = new NERItemPanel(prevItemPanel.item, itemManager, nerData, clientThread, this);
//				remove(searchResultsPanel);
//				add(itemPanel, BorderLayout.CENTER);
//				this.updateUI();
//				prevItemPanel = null;
//			});
//
//		}
	}

	private void itemSearch()
	{
		if (Strings.isNullOrEmpty(searchBar.getText()))
		{
			updateSearch();
			return;
		}

		if (itemPanel != null)
		{
			remove(itemPanel);
			SwingUtilities.invokeLater(() ->
			{
//				prevItemPanel = new NERItemPanel(itemPanel.item, itemManager, nerData, clientThread, this);
				this.updateUI();
				itemPanel = null;
			});
		}
		currentPanel = searchResultsPanel;
		add(searchResultsPanel);
		searchResultsPanel.itemSearch(searchBar);
		searchBar.setEditable(true);
	}

	void displayItem(NERItem item)
	{
		itemPanel = new NERItemPanel(item, itemManager, nerData, clientThread, this);
		remove(currentPanel);
		currentPanel = itemPanel;
		add(itemPanel, BorderLayout.CENTER);
		updateUI();
	}
}
