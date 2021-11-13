package com.notenoughrunes;

import com.google.gson.Gson;
import com.google.inject.Provides;
import com.notenoughrunes.types.DataFetcher;
import com.notenoughrunes.types.NERData;
import com.notenoughrunes.types.NERDropItem;
import com.notenoughrunes.types.NERDropSource;
import com.notenoughrunes.types.NERInfoItem;
import com.notenoughrunes.ui.NERPanel;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Data;
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

	@Getter
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
//		Stream<NERDropItem> list = nerData.getItemDropData().stream().filter(item -> item.getDropSources().stream().anyMatch(source -> source.source.equals("Fire giant")));
//		Set<NERDropItem> newList = new HashSet<NERDropItem>();
//		list.forEach(item -> {
//			Set<NERDropSource> sources = item.getDropSources().stream().filter(source -> source.source.equals("Fire giant")).collect(Collectors.toSet());
//			newList.add(new NERDropItem(item.getName(), sources));
//		});

//		Set<NERDropItem> newList = nerData.getItemDropData().stream()
//			.filter(item -> item.getName().equals("Big bones"))
//			.collect(Collectors.toSet());

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
