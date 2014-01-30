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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.eldorado.remoteresources.android.client.AndroidDeviceClient;
import com.eldorado.remoteresources.android.client.ClientChangedListener;
import com.eldorado.remoteresources.android.client.connection.Client;
import com.eldorado.remoteresources.android.client.connection.ClientConnectionManager;
import com.eldorado.remoteresources.android.client.screencapture.CaptureManager;
import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization;
import com.eldorado.remoteresources.i18n.RemoteResourcesMessages;
import com.eldorado.remoteresources.ui.model.Device;
import com.eldorado.remoteresources.ui.model.Host;
import com.eldorado.remoteresources.ui.model.PersistentDeviceModel;
import com.eldorado.remoteresources.ui.model.Script;
import com.eldorado.remoteresources.utils.ResourcesLoader;

/**
 * Action to connect a device. It also connects the host if it is not connected
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class ConnectDeviceAction extends AbstractAction implements
		ClientChangedListener {

	private static final long serialVersionUID = 6579975985501501417L;

	private final Device device;

	private final PersistentDeviceModel model;

	private final JPanel panel;

	private final String startCaptureTooltip;

	private final String stopCaptureTooltip;

	public ConnectDeviceAction(PersistentDeviceModel model, Device device,
			JPanel panel) {

		startCaptureTooltip = RemoteResourcesLocalization
				.getMessage(RemoteResourcesMessages.ConnectDeviceAction_Tooltip_StartCapture);
		stopCaptureTooltip = RemoteResourcesLocalization
				.getMessage(RemoteResourcesMessages.ConnectDeviceAction_Tooltip_StopCapture);

		putValue(Action.SHORT_DESCRIPTION, startCaptureTooltip);
		putValue(LARGE_ICON_KEY, ResourcesLoader.getIcon("button-play", 32));
		ClientConnectionManager.getInstance().addClientChangedListener(this);
		this.device = device;
		this.model = model;
		this.panel = panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Host h = model.getHost(device.getHost());

		if (h != null) {
			Client c = ClientConnectionManager.getInstance().getClient(
					h.getHostname(), h.getPort());
			if (c == null) {

				try {
					c = ClientConnectionManager.getInstance().connect(
							h.getHostname(), h.getPort());
					while (!c.isConnected()) {
						Thread.sleep(100);
					}
				} catch (UnknownHostException e1) {
					// do nothing

					e1.printStackTrace();
				} catch (IOException e1) {
					// do nothing

					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// do nothing

					e1.printStackTrace();
				}
			}

			if ((c != null)
					&& !c.isDataConnectionActive(device.getSerialNumber())) {

				boolean connectNewDevice = true;

				if (c.isAnyDataConnectionActive()) {
					int option = JOptionPane
							.showConfirmDialog(
									null,
									RemoteResourcesLocalization
											.getMessage(
													RemoteResourcesMessages.ConnectDeviceAction_ChangeDevice,
													h.getName()),
									"Change device",
									JOptionPane.OK_CANCEL_OPTION,
									JOptionPane.QUESTION_MESSAGE);

					connectNewDevice = option == JOptionPane.YES_OPTION ? true
							: false;
				}

				if (connectNewDevice) {
					/* Stop the capture of the current device. */
					CaptureManager.getInstance().stopCapture();

					CaptureManager.getInstance().startCapture(c, device);
					putValue(Action.SHORT_DESCRIPTION, stopCaptureTooltip);
					putValue(LARGE_ICON_KEY,
							ResourcesLoader.getIcon("button-stop", 32));
					panel.setBackground(Color.LIGHT_GRAY);
				}
			} else if (c != null) {
				CaptureManager.getInstance().stopCapture();
				putValue(Action.SHORT_DESCRIPTION, startCaptureTooltip);
				putValue(LARGE_ICON_KEY,
						ResourcesLoader.getIcon("button-play", 32));
				panel.setBackground(null);
			} else {
				JOptionPane
						.showConfirmDialog(
								null,
								RemoteResourcesLocalization
										.getMessage(
												RemoteResourcesMessages.ConnectDeviceAction_CaptureErrorMessage,
												h.getName()), "Host offline",
								JOptionPane.DEFAULT_OPTION,
								JOptionPane.ERROR_MESSAGE);
			}
		}
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
		if (this.device.getSerialNumber().equals(device)) {
			putValue(Action.SHORT_DESCRIPTION, stopCaptureTooltip);
			putValue(LARGE_ICON_KEY, ResourcesLoader.getIcon("button-stop", 32));

			Script.clean();
		}
	}

	@Override
	public void deviceDisconnected(Client client, String device) {
		if (this.device.getSerialNumber().equals(device)) {
			putValue(Action.SHORT_DESCRIPTION, startCaptureTooltip);
			putValue(LARGE_ICON_KEY, ResourcesLoader.getIcon("button-play", 32));

			if (this.device.getScriptGeneration()) {
				ClientManipulationScript cScript = new ClientManipulationScript();
				cScript.createScriptFile(this.device.getSerialNumber(),
						client.getHostname(), String.valueOf(client.getPort()),
						this.device.getProperty(Device.SCRIPT_PATH),
						this.device.getProperty(Device.SCRIPT_NAME),
						Script.getActions());
			}
		}
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
