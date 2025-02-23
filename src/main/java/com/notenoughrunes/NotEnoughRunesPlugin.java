package com.notenoughrunes;

import com.google.gson.Gson;
import com.google.inject.Provides;
import com.notenoughrunes.config.MenuLookupMode;
import com.notenoughrunes.db.H2DataProvider;
import com.notenoughrunes.ui.NERPanel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.KeyCode;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.RuneLite;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.PluginMessage;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

@Slf4j
@PluginDescriptor(
	name = "Not Enough Runes",
	description = "Shows wiki-sourced information where items can be found and what they're used for",
	tags = {"recipe", "crafting", "sources", "Uses", "wiki", "ner"}
)
public class NotEnoughRunesPlugin extends Plugin
{

	public static final File NER_DATA_DIR = new File(RuneLite.RUNELITE_DIR, "not-enough-runes");

	@Inject
	private Client client;

	@Inject
	private NotEnoughRunesConfig config;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private Gson gson;

	@Inject
	private H2DataProvider dataProvider;

	@Getter(AccessLevel.PACKAGE)
	private NavigationButton navButton;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private NERPanel nerPanel;

	@Override
	protected void startUp() throws Exception
	{
		if (!NER_DATA_DIR.exists())
		{
			NER_DATA_DIR.mkdirs();
			dataProvider.init(); // async
		}
		else
		{
			dataProvider.init(); // async
		}


		nerPanel = injector.getInstance(NERPanel.class);
		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "icon.png");

		navButton = NavigationButton.builder()
			.tooltip("Not Enough Runes")
			.icon(icon)
			.priority(5)
			.panel(nerPanel)
			.build();

		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
		nerPanel = null;
		clientToolbar.removeNavigation(navButton);
	}

	@Subscribe
	public void onMenuOpened(final MenuOpened event)
	{
		final MenuEntry[] entries = event.getMenuEntries();
		for (int idx = entries.length - 1; idx >= 0; --idx)
		{
			final MenuEntry entry = entries[idx];
			final Widget w = entry.getWidget();

			boolean shouldAddInv = w != null && (WidgetUtil.componentToInterface(w.getId()) == InterfaceID.INVENTORY
					|| WidgetUtil.componentToInterface(w.getId()) == InterfaceID.BANK_INVENTORY)
				&& config.invLookupMode() != MenuLookupMode.DISABLED
					&& !(config.invLookupMode() == MenuLookupMode.SHIFT && !client.isKeyPressed(KeyCode.KC_SHIFT));

			boolean shouldAddBank = w != null && WidgetUtil.componentToInterface(w.getId()) == InterfaceID.BANK
				&& config.bankLookupMode() != MenuLookupMode.DISABLED
				&& !(config.bankLookupMode() == MenuLookupMode.SHIFT && !client.isKeyPressed(KeyCode.KC_SHIFT));

			if (w != null && (shouldAddInv || shouldAddBank)
				&& "Examine".equals(entry.getOption()) && entry.getIdentifier() == 10)
			{
				final int itemId = w.getItemId();
				client.createMenuEntry(idx)
					.setOption("NER Lookup")
					.setTarget(entry.getTarget())
					.setType(MenuAction.RUNELITE)
					.onClick(e ->
					{
						nerPanel.displayItemById(itemId);
						SwingUtilities.invokeLater(() -> clientToolbar.openPanel(navButton));

					});
			}
		}

	}

	@Subscribe
	public void onPluginMessage(PluginMessage event) {
		if (!event.getNamespace().equals("notenoughrunes")) {
			return;
		}

		Map<String, Object> data = event.getData();

		if (event.getName().equals("displayItemById")) {
			Object itemId = data.getOrDefault("itemId", null);

			if (itemId instanceof Integer) {
				nerPanel.displayItemById((int) itemId);
				SwingUtilities.invokeLater(() -> clientToolbar.openPanel(navButton));
			} else {
				log.error("Invalid type sent to displayItemById event, expected Integer");
			}
		}
		else if (event.getName().equals("searchItemName")) {
			Object itemName = data.getOrDefault("itemName", null);

			if (itemName instanceof String) {
				nerPanel.searchItemName((String) itemName);
				SwingUtilities.invokeLater(() -> clientToolbar.openPanel(navButton));
			} else {
				log.error("Invalid type sent to searchItemName event, expected String");
			}
		}
	}

	@Provides
	NotEnoughRunesConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NotEnoughRunesConfig.class);
	}
}
