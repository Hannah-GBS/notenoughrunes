package com.notenoughrunes;

import com.google.gson.Gson;
import com.google.inject.Provides;
import com.notenoughrunes.types.DataFetcher;
import com.notenoughrunes.types.NERData;
import com.notenoughrunes.types.NERInfoItem;
import com.notenoughrunes.ui.NERPanel;
import java.awt.image.BufferedImage;
import java.util.Set;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

@Slf4j
@PluginDescriptor(
	name = "Not Enough Runes"
)
public class NotEnoughRunesPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private NotEnoughRunesConfig config;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private Gson gson;

	private NERData nerData;

	@Getter(AccessLevel.PACKAGE)
	private NavigationButton navButton;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private NERPanel nerPanel;

	@Override
	protected void startUp() throws Exception
	{
		nerPanel = injector.getInstance(NERPanel.class);
		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "icon.png");

		navButton = NavigationButton.builder()
			.tooltip("Not Enough Runes")
			.icon(icon)
			.panel(nerPanel)
			.build();

		clientToolbar.addNavigation(navButton);

		nerData = new NERData(new DataFetcher());
	}

	@Override
	protected void shutDown() throws Exception
	{
//		nerPanel.shutdown();
		clientToolbar.removeNavigation(navButton);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{

	}

	@Provides
	NotEnoughRunesConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NotEnoughRunesConfig.class);
	}
}
