package com.notenoughrunes.ui;

import com.notenoughrunes.types.NERSpawnItem;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.events.PluginMessage;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

public class NERSpawnPanel extends JPanel
{
	
	private static final BufferedImage MEMBERS_ICON = ImageUtil.loadImageResource(NERSpawnPanel.class, "members.png");
	private static final BufferedImage F2P_ICON = ImageUtil.loadImageResource(NERSpawnPanel.class, "free_to_play.png");
	
	public NERSpawnPanel(NERSpawnItem spawn, ClientThread clientThread, Client client, EventBus eventBus, PluginManager pluginManager)
	{
		setMaximumSize(new Dimension(PluginPanel.PANEL_WIDTH, 200));
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setBorder(new EmptyBorder(7, 7, 6, 7));
		setLayout(new BorderLayout());

		JLabel membersLabel = new JLabel(new ImageIcon(spawn.isMembers() ? MEMBERS_ICON : F2P_ICON));
		membersLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));
		add(membersLabel, BorderLayout.WEST);
		JLabel location = new JLabel(spawn.getLocation());
		location.setPreferredSize(new Dimension(0, 20));
		location.setToolTipText(spawn.getLocation());
		add(location, BorderLayout.CENTER);

		clientThread.invokeLater(() ->
		{
			WorldPoint spawnWP;
			WorldPoint playerWP;

			if (client.getGameState() == GameState.LOGGED_IN) {
				playerWP = client.getLocalPlayer().getWorldLocation();
			}
			else {
				playerWP = null;
			}

			if (!Objects.equals(spawn.getCoords(), "") && !Objects.equals(spawn.getPlane(), "")) {
				String[] shopCoords = spawn.getCoords().split(",");
				int shopX = Integer.parseInt(shopCoords[0]);
				int shopY = Integer.parseInt(shopCoords[1]);
				int shopPlane = Integer.parseInt(spawn.getPlane());
				spawnWP = new WorldPoint(shopX, shopY, shopPlane);

			}
			else
			{
				spawnWP = null;
			}

			Plugin spPluginFound = null;
			for (Plugin p : pluginManager.getPlugins())
			{
				if (p.getName().equals("Shortest Path"))
				{
					spPluginFound = p;
					break;
				}
			}

			JPopupMenu menu = new JPopupMenu("Menu");
			if (spawnWP != null && playerWP != null && spPluginFound != null && pluginManager.isPluginActive(spPluginFound)) {

				JMenuItem shortestPathMenu = new JMenuItem(new AbstractAction("Shortest Path")
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						Map<String, Object> data = new HashMap<>();
						data.put("start", playerWP);
						data.put("target", spawnWP);
						eventBus.post(new PluginMessage("shortestpath", "path", data));
					}
				});

				menu.add(shortestPathMenu);
			}

			var menuHasEntries = menu.getSubElements().length > 0;

			if (menuHasEntries)
			{
				location.addMouseListener(new MouseAdapter()
				{
					@Override
					public void mouseClicked(MouseEvent e)
					{
						if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1)
						{
							menu.show(location, e.getX(), e.getY());
						}
					}
				});

				membersLabel.addMouseListener(new MouseAdapter()
				{
					@Override
					public void mouseClicked(MouseEvent e)
					{
						if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1)
						{
							menu.show(membersLabel, e.getX(), e.getY());
						}
					}
				});

				addMouseListener(new MouseAdapter()
				{
					@Override
					public void mouseClicked(MouseEvent e)
					{
						if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1)
						{
							menu.show(getParent(), e.getX(), e.getY());
						}
					}
				});
			}
		});
	}
	
}
