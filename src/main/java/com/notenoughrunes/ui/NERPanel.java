package com.notenoughrunes.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;

@Slf4j
public class NERPanel extends PluginPanel
{
	private final IconTextField searchBar = new IconTextField();

	@Getter
	private final NERSearchResultsPanel searchResultsPanel;

	@Inject
	private NERPanel(ScheduledExecutorService executor, NERSearchResultsPanel searchResultsPanel)
	{
		super(false);
		this.searchResultsPanel = searchResultsPanel;

		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setBorder(new EmptyBorder(5, 5, 5, 5));

		searchBar.setIcon(IconTextField.Icon.SEARCH);
		searchBar.setPreferredSize(new Dimension(100, 30));
		searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
		searchBar.addActionListener(e -> executor.execute(this::itemSearch));
		searchBar.addClearListener(() -> searchResultsPanel.updateSearch(searchBar));

		add(searchBar, BorderLayout.NORTH);
		add(searchResultsPanel);
	}

	private void itemSearch()
	{
		searchResultsPanel.itemSearch(searchBar);
		searchBar.setEditable(true);
	}
}
