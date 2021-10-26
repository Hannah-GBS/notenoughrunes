package com.notenoughrunes;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.notenoughrunes.ui.NERPanel;
import java.awt.Cursor;
import java.awt.Insets;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import net.runelite.client.ui.ContainableFrame;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.skin.SubstanceRuneLiteLookAndFeel;
import net.runelite.client.util.SwingUtil;

public class NotEnoughRunesUITest
{

	public static void main(String[] args) throws InterruptedException, InvocationTargetException
	{
		Injector testInjector = Guice.createInjector(i ->
		{
		});

		SwingUtilities.invokeAndWait(() ->
		{
			// roughly copied from RuneLite's ClientUI.java init()
			SwingUtil.setupDefaults();
			SwingUtil.setTheme(new SubstanceRuneLiteLookAndFeel());
			SwingUtil.setFont(FontManager.getRunescapeFont());

			ContainableFrame frame = new ContainableFrame();
			frame.getLayeredPane().setCursor(Cursor.getDefaultCursor());

			NERPanel pluginPanel = testInjector.getInstance(NERPanel.class);
			frame.add(pluginPanel);

			Insets insets = frame.getInsets(); // non-frame border (os elements)
			frame.setSize(242 + insets.left + insets.right, 800 + insets.top + insets.bottom);
			
			frame.setResizable(true);
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.setVisible(true);
		});
	}
	
}
