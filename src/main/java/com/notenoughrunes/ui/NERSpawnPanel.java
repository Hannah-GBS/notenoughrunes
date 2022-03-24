package com.notenoughrunes.ui;

import com.notenoughrunes.types.NERSpawnItem;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

public class NERSpawnPanel extends JPanel
{
	
	private static final BufferedImage MEMBERS_ICON = ImageUtil.loadImageResource(NERSpawnPanel.class, "members.png");
	private static final BufferedImage F2P_ICON = ImageUtil.loadImageResource(NERSpawnPanel.class, "free_to_play.png");
	
	public NERSpawnPanel(NERSpawnItem spawn)
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
		// maybe this panel could have a link to show on the world map?
	}
	
}
