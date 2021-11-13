package com.notenoughrunes.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;

class NERSourcesPanel extends JPanel
{
	NERSourcesPanel()
	{
		setLayout(new BorderLayout());

		JPanel container = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		add(container, BorderLayout.NORTH);
	}
}
