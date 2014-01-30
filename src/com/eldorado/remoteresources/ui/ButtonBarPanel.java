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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JButton;

import com.eldorado.remoteresources.android.client.AndroidDeviceClient;
import com.eldorado.remoteresources.android.client.ClientChangedListener;
import com.eldorado.remoteresources.android.client.connection.Client;
import com.eldorado.remoteresources.android.client.connection.ClientConnectionManager;
import com.eldorado.remoteresources.android.common.Keys;
import com.eldorado.remoteresources.android.common.connection.messages.control.KeyCommandMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.KeyCommandMessageType;
import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization;
import com.eldorado.remoteresources.i18n.RemoteResourcesMessages;
import com.eldorado.remoteresources.ui.model.Script;
import com.eldorado.remoteresources.utils.ResourcesLoader;

/**
 * A Button Bar panel with several buttons
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class ButtonBarPanel extends BasePanel implements ClientChangedListener {

	private static final long serialVersionUID = -4248276467079326208L;

	private JButton actionsMenuButton;

	private final MenuBarOptionsDialog options = new MenuBarOptionsDialog();

	private JButton menuButton;

	private JButton homeButton;

	private JButton backButton;

	private JButton searchButton;

	private JButton powerButton;

	private JButton uploadFileButton;

	private JButton downloadFileButton;

	private Client client = null;

	private String serialNumber = null;

	private final MouseListener mouseListener = new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e) {
			if ((client != null) && (serialNumber != null)) {
				Keys k = ((KeyButton) e.getSource()).getKey();
				client.sendMessage(new KeyCommandMessage(client
						.getNextSequenceNumber(), serialNumber, k,
						KeyCommandMessageType.KEY_DOWN));

				Script.registerActionKey(k.name().toString(), "",
						KeyCommandMessageType.KEY_DOWN);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			Keys k = ((KeyButton) e.getSource()).getKey();
			if ((client != null) && (serialNumber != null)) {
				client.sendMessage(new KeyCommandMessage(client
						.getNextSequenceNumber(), serialNumber, k,
						KeyCommandMessageType.KEY_UP));

				Script.registerActionKey(k.name().toString(), "",
						KeyCommandMessageType.KEY_UP);
			}
		}

	};

	public ButtonBarPanel() {
		createButtons();
		setButtonsEdition(false);
		ClientConnectionManager.getInstance().addClientChangedListener(this);
	}

	private void setButtonsEdition(boolean b) {
		actionsMenuButton.setEnabled(b);
		menuButton.setEnabled(b);
		homeButton.setEnabled(b);
		backButton.setEnabled(b);
		searchButton.setEnabled(b);
		powerButton.setEnabled(b);
		// runnerButton.setEnabled(b);

		// uploadFileButton.setEnabled(b);
		// downloadFileButton.setEnabled(b);
	}

	private void createButtons() {

		menuButton = addButton(Keys.MENU,
				ResourcesLoader.getIcon("btnMenu", 48), "Menu");
		homeButton = addButton(Keys.HOME,
				ResourcesLoader.getIcon("btnHome", 48), "Home");
		backButton = addButton(Keys.BACK,
				ResourcesLoader.getIcon("btnBack", 48), "Back");
		searchButton = addButton(Keys.SEARCH,
				ResourcesLoader.getIcon("btnSearch", 48), "Search");
		powerButton = addButton(Keys.POWER,
				ResourcesLoader.getIcon("btnPower", 48), "Power");

		// Bringing Actions Menu button to this panel
		actionsMenuButton = addButton(
				ResourcesLoader.getIcon("menu", 48),
				RemoteResourcesLocalization
						.getMessage(RemoteResourcesMessages.ButtonBarPanel_ActionsMenu));
		actionsMenuButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (options.isDisplayed()) {
					options.dispose();
				} else {
					options.setConection(client, serialNumber);
					options.open(actionsMenuButton);
				}
			}
		});
		// uploadFileButton = addButton(ResourcesLoader.getIcon("uploadFile",
		// 48),
		// "Upload file");
		// downloadFileButton = addButton(
		// ResourcesLoader.getIcon("downloadFile", 48), "Download file");
		//
		// uploadFileButton.addActionListener(new ActionListener() {
		//
		// @Override
		// public void actionPerformed(ActionEvent arg0) {
		//
		// System.out.println("subindo!");
		//
		// }
		// });
		//
		// downloadFileButton.addActionListener(new ActionListener() {
		//
		// @Override
		// public void actionPerformed(ActionEvent arg0) {
		//
		// System.out.println("baixando!");
		//
		// }
		// });
	}

	private KeyButton addButton(Keys key, Icon icon, String tooltip) {
		KeyButton button = new KeyButton(key);
		button.setIcon(icon);
		button.setToolTipText(tooltip);
		button.addMouseListener(mouseListener);
		add(button);
		return button;
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
		this.client = client;
		serialNumber = device;
		setButtonsEdition(true);
	}

	@Override
	public void deviceDisconnected(Client client, String device) {
		this.client = null;
		serialNumber = null;
		setButtonsEdition(false);
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
