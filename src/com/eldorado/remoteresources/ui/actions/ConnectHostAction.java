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

package com.eldorado.remoteresources.ui.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.eldorado.remoteresources.android.client.AndroidDeviceClient;
import com.eldorado.remoteresources.android.client.ClientChangedListener;
import com.eldorado.remoteresources.android.client.connection.Client;
import com.eldorado.remoteresources.android.client.connection.ClientConnectionManager;
import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization;
import com.eldorado.remoteresources.i18n.RemoteResourcesMessages;
import com.eldorado.remoteresources.utils.ResourcesLoader;

/**
 * Connect a host. Currently not using this action, but it will remains here for
 * a while
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class ConnectHostAction extends AbstractAction implements
		ClientChangedListener {

	private static final long serialVersionUID = 6579975985501501417L;

	private final String hostname;

	private final int port;

	private final String connectTooltip;

	private final String disconnectTooltip;

	public ConnectHostAction(String hostname, int port) {

		connectTooltip = RemoteResourcesLocalization
				.getMessage(RemoteResourcesMessages.ConnectHostAction_Tooltip_StartHost);

		disconnectTooltip = RemoteResourcesLocalization
				.getMessage(RemoteResourcesMessages.ConnectHostAction_Tooltip_StopHost);

		this.hostname = hostname;
		this.port = port;
		putValue(Action.SHORT_DESCRIPTION, connectTooltip);
		putValue(LARGE_ICON_KEY, ResourcesLoader.getIcon("plug", 32));
		ClientConnectionManager.getInstance().addClientChangedListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ClientConnectionManager.getInstance().getClient(hostname, port) == null) {
			try {
				ClientConnectionManager.getInstance().connect(hostname, port);
				setEnabled(false);
			} catch (UnknownHostException ex) {
				// TODO: report exception visually (a dialog for example)
				setEnabled(true);
			} catch (IOException ex) {
				setEnabled(true);
			}
		} else {
			ClientConnectionManager.getInstance().disconnect(hostname, port);
			setEnabled(true);
		}

	}

	@Override
	public void clientConnected(Client client) {
		if (client.getHostname().equals(hostname) && (client.getPort() == port)) {
			putValue(Action.SHORT_DESCRIPTION, disconnectTooltip);
			putValue(LARGE_ICON_KEY, ResourcesLoader.getIcon("button-stop", 32));
			setEnabled(true);
		}
	}

	@Override
	public void clientDisconnected(Client client) {
		if (client.getHostname().equals(hostname) && (client.getPort() == port)) {
			putValue(Action.SHORT_DESCRIPTION, connectTooltip);
			putValue(LARGE_ICON_KEY, ResourcesLoader.getIcon("plug", 32));
			setEnabled(true);
		}
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
