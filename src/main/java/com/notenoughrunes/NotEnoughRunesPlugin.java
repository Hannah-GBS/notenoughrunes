package com.notenoughrunes;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.inject.Provides;
import com.notenoughrunes.config.MenuLookupMode;
import com.notenoughrunes.db.H2DataProvider;
import com.notenoughrunes.ui.NERPanel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.KeyCode;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.RuneLite;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.PluginMessage;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;

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
	private ClientThread clientThread;

	@Inject
	private Gson gson;

	@Inject
	private H2DataProvider dataProvider;

	@Getter(AccessLevel.PACKAGE)
	private NavigationButton navButton;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private NERPanel nerPanel;

	// Items with different collection log IDs that are not mapped by Enum 3721
	private static final Map<Integer, Integer> ITEM_ID_MAP = new ImmutableMap.Builder<Integer, Integer>()
		.put(25615, 21539) // Large water container
		.build();

	public static final int SCRIPT_REBUILD_CHATBOX = 84;
	public static final int VARC_INT_CHAT_TAB = 41;
	static final Pattern COLLECTION_LOG_REGEX = Pattern.compile(".* received a new collection log item: (.*) \\(\\d+/\\d+\\)");
	static final Pattern RAID_LOOT_REGEX = Pattern.compile(".* received special loot from a raid: (.*)\\.");
	static final Pattern DROP_REGEX = Pattern.compile(".* received a drop: (?:[\\d,]* x )*(.+?)(?: \\([\\d,]+ coins\\))*\\.");


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
		cleanUpWidgets();
	}

	@Subscribe
	public void onMenuOpened(final MenuOpened event)
	{
		final MenuEntry[] entries = event.getMenuEntries();
		for (int idx = 0; idx < entries.length; idx++)
		{
			final MenuEntry entry = entries[idx];
			final Widget w = entry.getWidget();

			if (w == null) continue;

			final int group = WidgetUtil.componentToInterface(w.getId());

			boolean shouldAddInv = (group == InterfaceID.INVENTORY
					|| group == InterfaceID.EQUIPMENT_SIDE
					|| group == InterfaceID.BANKSIDE
					|| group == InterfaceID.SHARED_BANK_SIDE)
				&& config.invLookupMode() != MenuLookupMode.DISABLED
				&& !(config.invLookupMode() == MenuLookupMode.SHIFT && !client.isKeyPressed(KeyCode.KC_SHIFT));

			boolean shouldAddEquip = (group == InterfaceID.WORNITEMS
					|| group == InterfaceID.EQUIPMENT
					|| (group == InterfaceID.BANKMAIN && w.getParentId() == InterfaceID.Bankmain.WORNITEMS_CONTAINER))
				&& config.equipLookupMode() != MenuLookupMode.DISABLED
				&& !(config.equipLookupMode() == MenuLookupMode.SHIFT && !client.isKeyPressed(KeyCode.KC_SHIFT));

			boolean shouldAddBank = ((group == InterfaceID.BANKMAIN	&& w.getParentId() == InterfaceID.Bankmain.ITEMS)
					|| group == InterfaceID.SHARED_BANK)
				&& config.bankLookupMode() != MenuLookupMode.DISABLED
				&& !(config.bankLookupMode() == MenuLookupMode.SHIFT && !client.isKeyPressed(KeyCode.KC_SHIFT));

			boolean shouldAddClog = (group == InterfaceID.COLLECTION && w.getParentId() == InterfaceID.Collection.ITEMS_CONTENTS) // Don't show on search
				&& config.clogLookupMode() != MenuLookupMode.DISABLED
				&& !(config.clogLookupMode() == MenuLookupMode.SHIFT && !client.isKeyPressed(KeyCode.KC_SHIFT));

			if (((shouldAddInv || shouldAddEquip || shouldAddBank)
				&& "Examine".equals(entry.getOption()) && entry.getIdentifier() == 10)
				|| (shouldAddClog && "Check".equals(entry.getOption()) && entry.getIdentifier() == 1))
			{
				int itemId = w.getItemId();
				if (shouldAddEquip) {
					final Widget widgetItem = w.getChild(1);
					if (widgetItem != null) {
						itemId = widgetItem.getItemId();
					}
				}

				// Handle items with different IDs in the log than in inventories
				if (shouldAddClog) {
					final EnumComposition itemMapEnum = client.getEnum(3721);
					int[] keys = itemMapEnum.getKeys();
					int[] values = itemMapEnum.getIntVals();
					for (int i = 0; i < values.length; i++){
						if (values[i] == itemId) {
							itemId = keys[i];
						}
					}

					if (ITEM_ID_MAP.containsKey(itemId)) {
						itemId = ITEM_ID_MAP.get(itemId);
					}
				}

				int finalItemId = itemId;
				client.getMenu().createMenuEntry(idx)
					.setOption("NER Lookup")
					.setTarget(entry.getTarget())
					.setType(MenuAction.RUNELITE)
					.onClick(e ->
					{
						nerPanel.displayItemById(finalItemId);
						SwingUtilities.invokeLater(() -> clientToolbar.openPanel(navButton));
					});
				return;
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

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{
		if (event.getScriptId() == SCRIPT_REBUILD_CHATBOX && config.ccBroadcastLookup())
		{
			matchMessages();
		}
	}

	@Subscribe
	public void onCommandExecuted(CommandExecuted event) {
		if (event.getCommand().equals("nerlookup")) {
			var itemId = event.getArguments()[0];
			nerPanel.displayItemById(Integer.parseInt(itemId));
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if (event.getGroup().equals("notenoughrunes")) {
			if (config.ccBroadcastLookup())
			{
				clientThread.invoke(this::matchMessages);
			}
			else
			{
				clientThread.invoke(this::cleanUpWidgets);
			}
		}
	}

	private void matchMessages()
	{
		Widget chatbox = client.getWidget(InterfaceID.Chatbox.SCROLLAREA);
		int selectedChatTab = client.getVarcIntValue(VARC_INT_CHAT_TAB);

		if (chatbox != null && selectedChatTab != 1337) // 1337 = closed
		{
			Widget[] chatWidgets = chatbox.getDynamicChildren();

			for (int i = 2; i < chatWidgets.length; i = i + 4)
			{
				int messageWidgetIndex = i - 1; // [1]

				Widget messageWidget = chatWidgets[messageWidgetIndex];

				// clean widgets first
				removeChatMenuEntry(messageWidget);

//				if (messageWidgetIndex == 1 && messageWidget.getText().contains("To talk in your clan's channel"))
//				{
//					addChatMenuEntry(messageWidget, "test");
//				}

				if (messageWidget.isHidden())
				{
					return;
				}

				String message = Text.removeTags(messageWidget.getText());

				Matcher collMatcher = COLLECTION_LOG_REGEX.matcher(message);
				if (collMatcher.find())
				{
					String item = collMatcher.group(1);
//					log.info("matched collection log: {}", item);
					addChatMenuEntry(messageWidget, item);
					continue;
				}

				Matcher raidMatcher = RAID_LOOT_REGEX.matcher(message);
				if (raidMatcher.find())
				{
					String item = raidMatcher.group(1);
//					log.info("matched raid drop: {}", item);
					addChatMenuEntry(messageWidget, item);
					continue;
				}

				Matcher dropMatcher = DROP_REGEX.matcher(message);
				if (dropMatcher.find())
				{
					String item = dropMatcher.group(1);
//					log.info("matched loot drop: {}", item);
					addChatMenuEntry(messageWidget, item);
				}

			}

		}
	}

	private void cleanUpWidgets()
	{
		Widget chatbox = client.getWidget(InterfaceID.Chatbox.SCROLLAREA);
		int selectedChatTab = client.getVarcIntValue(VARC_INT_CHAT_TAB);

		if (chatbox != null && selectedChatTab != 1337) // 1337 = closed
		{
			Widget[] chatWidgets = chatbox.getDynamicChildren();

			for (int i = 2; i < chatWidgets.length; i = i + 4)
			{
				int messageWidgetIndex = i - 1; // [1]

				Widget messageWidget = chatWidgets[messageWidgetIndex];

				// clean widgets first
				removeChatMenuEntry(messageWidget);
			}
		}
	}


	private void addChatMenuEntry(Widget messageWidget, String itemName)
	{
		messageWidget.setAction(7, "NER Lookup");
		messageWidget.setHasListener(true);
		messageWidget.setOnOpListener((JavaScriptCallback) (event) -> {
			nerPanel.searchItemNameAndDisplay(itemName);
			SwingUtilities.invokeLater(() -> clientToolbar.openPanel(navButton));
		});
		messageWidget.setName(itemName);
	}

	private void removeChatMenuEntry(Widget messageWidget)
	{
		messageWidget.setName("");
		if (messageWidget.hasListener())
		{
			Object[] listeners = messageWidget.getOnOpListener();

			if (listeners != null)
			{
				listeners = Arrays.stream(listeners)
					.filter(listener -> !String.valueOf(listener).contains("com.notenoughrunes.NotEnoughRunesPlugin"))
					.toArray();
			}

			if (listeners != null && listeners.length > 0)
			{
				messageWidget.setOnOpListener(listeners);
			}
			else
			{
				messageWidget.setOnOpListener((Object[]) null);
				messageWidget.setHasListener(false);
			}
		}
	}


	@Provides
	NotEnoughRunesConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NotEnoughRunesConfig.class);
	}
}
