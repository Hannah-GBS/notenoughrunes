package com.notenoughrunes.ui;

import com.google.common.base.Strings;
import com.notenoughrunes.NotEnoughRunesConfig;
import com.notenoughrunes.NotEnoughRunesPlugin;
import com.notenoughrunes.db.H2DataProvider;
import com.notenoughrunes.db.queries.ItemByIDQuery;
import com.notenoughrunes.types.NERInfoItem;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.util.AsyncBufferedImage;

@Slf4j
public class NERPanel extends PluginPanel
{
	private final IconTextField searchBar = new IconTextField();
	private final ClientThread clientThread;
	private final ItemManager itemManager;
	private final H2DataProvider dataProvider;
	private final NotEnoughRunesConfig config;

	public final static int MAX_ENTRIES = 100;

	private NERItemPanel itemPanel;
//	private NERItemPanel prevItemPanel;

	private JPanel currentPanel;

	@Getter
	private final NERSearchResultsPanel searchResultsPanel;

	@Inject
	private NERPanel(
		NotEnoughRunesPlugin plugin,
		ClientThread clientThread,
		ItemManager itemManager,
		H2DataProvider dataProvider,
		ScheduledExecutorService executor,
		NotEnoughRunesConfig config
	)
	{
		super(false);
		this.searchResultsPanel = new NERSearchResultsPanel(clientThread, itemManager, dataProvider, this);
		this.clientThread = clientThread;
		this.itemManager = itemManager;
		this.dataProvider = dataProvider;
		this.config = config;

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
		currentPanel = searchResultsPanel;
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

	private void itemSearchFirst()
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
		searchResultsPanel.itemSearchFirst(searchBar);
		searchBar.setEditable(true);
	}

	void displayItem(NERItem item)
	{
		itemPanel = new NERItemPanel(item, itemManager, dataProvider, clientThread, this, config);
		remove(currentPanel);
		currentPanel = itemPanel;
		add(itemPanel, BorderLayout.CENTER);
		updateUI();
	}

	public void displayItemById(int rawItemId)
	{
		clientThread.invokeLater(() ->
		{
			int itemId = itemManager.canonicalize(rawItemId);
			dataProvider.executeSingle(new ItemByIDQuery(itemId), this::getNerItem);
		});
	}

	public void searchItemName(String itemName) {
		SwingUtilities.invokeLater(() -> {
			searchBar.setText(itemName);
			itemSearch();
		});
	}

	public void searchItemNameAndDisplay(String itemName) {
		SwingUtilities.invokeLater(() -> {
			searchBar.setText(itemName);
			itemSearchFirst();

		});
	}

	void getNerItem(NERInfoItem itemInfo)
	{
		clientThread.invokeLater(() ->
		{
			AsyncBufferedImage icon = itemManager.getImage(itemManager.canonicalize(itemInfo.getItemID()));
			NERItem nerItem = new NERItem(icon, itemInfo);

			SwingUtilities.invokeLater(() -> displayItem(nerItem));
		});
	}

//	public NERInfoItem getItemByNameAndVersion(String itemName, String version)
//	{
//		List<NERInfoItem> matchedItems = dataProvider.executeMany(new WideSearchItemsQuery(itemName));
//
//		return matchedItems.stream()
//			.min(compareNameAndGroup(itemName, version))
//			.orElse(new NERInfoItem(0, "", "", "", "", "", false, false));
//	}
//
//	private static Comparator<NERInfoItem> compareNameAndGroup(String itemName, String version)
//	{
//		return Comparator.comparing((NERInfoItem item) -> new LevenshteinDistance().apply(item.getName(), itemName))
//			.thenComparing(item -> new LevenshteinDistance().apply(item.getGroup(), itemName))
//			.thenComparing(item -> new LevenshteinDistance().apply(item.getVersion() != null ? item.getVersion() : "", version != null ? version : ""));
//
//	}
}
