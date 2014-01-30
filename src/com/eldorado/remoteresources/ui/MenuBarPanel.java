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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;

import com.eldorado.remoteresources.android.client.AndroidDeviceClient;
import com.eldorado.remoteresources.android.client.ClientChangedListener;
import com.eldorado.remoteresources.android.client.connection.Client;
import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization;
import com.eldorado.remoteresources.i18n.RemoteResourcesMessages;
import com.eldorado.remoteresources.utils.ResourcesLoader;

/**
 * A Button Bar panel with several buttons
 * 
 * @author Michel Silva Fornaciali
 * 
 */
public class MenuBarPanel extends BasePanel implements ClientChangedListener {

	private static final long serialVersionUID = -4248276467079326208L;

	private final Client client = null;

	private final String serialNumber = null;

	private JButton languageButton = null;

	private final LanguageOptionsDialog languageDialog = new LanguageOptionsDialog();

	public MenuBarPanel() {
		createButtons();
		// ClientConnectionManager.getInstance().addClientChangedListener(this);
		languageButton.setEnabled(true);
	}

	private void createButtons() {

		languageButton = addButton(
				ResourcesLoader.getIcon("i18n", 48),
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.MenuBarPanel_Tooltip_MainMenu));
		languageButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (languageDialog.isDisplayed()) {
					languageDialog.dispose();
				} else {
					languageDialog.setLocationRelativeTo(languageButton);
					languageDialog.open();

				}
			}
		});

	}

	private JButton addButton(Icon icon, String tooltip) {
		JButton button = new JButton();
		button.setIcon(icon);
		button.setToolTipText(tooltip);
		add(button);
		return button;
	}

	@Override
	public void clientConnected(Client client) {
		// do nothing
	}

	@Override
	public void clientDisconnected(Client client) {
		// do nothing
	}

	@Override
	public void deviceConnected(Client client, String device) {
		// do nothing
	}

	@Override
	public void deviceDisconnected(Client client, String device) {
		// do nothing
	}

	@Override
	public void deviceAdded(Client client, String device) {
		// do nothing

	}

	@Override
	public void deviceRemoved(Client client, AndroidDeviceClient device) {
		// do nothing
	}
}
