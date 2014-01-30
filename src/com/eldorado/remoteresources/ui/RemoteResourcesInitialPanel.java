/*
 * (C) Copyright 2014 Instituto de Pesquisas Eldorado (http://www.eldorado.org.br/).
 *
 * This file is part of the software Remote Resources
 *
 * All rights reserved. This file and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */

package com.eldorado.remoteresources.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.eldorado.remoteresources.RemoteResourcesConfiguration;
import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization;
import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization.RemoteResourcesLocalization_Languages;

public class RemoteResourcesInitialPanel extends JFrame {

	private static final long serialVersionUID = 1574467190220398826L;

	public RemoteResourcesInitialPanel() {

		// Checks if preferred language is already set in config file
		if ((RemoteResourcesConfiguration.getInstance().get(
				RemoteResourcesConfiguration.PREFERRED_LANG) != null)
				&& !RemoteResourcesConfiguration.getInstance()
						.get(RemoteResourcesConfiguration.PREFERRED_LANG)
						.equals("")) {

			try {
				RemoteResourcesLocalization
						.setLanguage(RemoteResourcesLocalization_Languages
								.valueOf(RemoteResourcesConfiguration
										.getInstance()
										.get(RemoteResourcesConfiguration.PREFERRED_LANG)));
				RemoteResourcesPanel frame = new RemoteResourcesPanel();
				frame.setVisible(true);
				return;
			} catch (IllegalArgumentException e) {
				// if language in config file isn't supported, just continue as
				// if nothing happened
			}
		}

		initComponents();
		setTitle("Remote Resources");
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		Dimension d = new Dimension(250, 250);
		setSize(d);
		setMinimumSize(d);

		// Places initial dialog in the center of screen
		setLocation((Toolkit.getDefaultToolkit().getScreenSize().width / 2)
				- (d.width / 2),
				(Toolkit.getDefaultToolkit().getScreenSize().height / 2)
						- (d.height / 2));
		setVisible(true);
	}

	private void initComponents() {

		JLabel label = new JLabel("Choose a language: ");

		String[] options = { "English", "Português" };
		final JComboBox<String> languages = new JComboBox<String>(options);
		languages.setSelectedIndex(0);

		final JCheckBox checkbox = new JCheckBox("Remember this choice");

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				RemoteResourcesLocalization_Languages l = RemoteResourcesLocalization_Languages.en_US;
				switch (languages.getSelectedIndex()) {
				case 0: // English/Default
					l = RemoteResourcesLocalization_Languages.en_US;
					break;
				case 1: // Portuguese
					l = RemoteResourcesLocalization_Languages.pt_BR;
					break;
				}

				RemoteResourcesLocalization.setLanguage(l);

				if (checkbox.isSelected()) {
					RemoteResourcesConfiguration.getInstance().set(
							RemoteResourcesConfiguration.PREFERRED_LANG,
							l.toString());

				}

				dispose();

				RemoteResourcesPanel frame = new RemoteResourcesPanel();
				frame.setVisible(true);
			}
		});

		JPanel panel = new JPanel(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.gridy = 0;
		panel.add(label, constraints);

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.gridy = 1;
		panel.add(languages, constraints);

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.gridy = 2;
		panel.add(checkbox, constraints);

		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.LINE_END;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.gridy = 3;
		panel.add(okButton, constraints);

		setContentPane(panel);
	}
}
