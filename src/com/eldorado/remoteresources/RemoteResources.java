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

package com.eldorado.remoteresources;

import java.io.File;

import javax.swing.JOptionPane;

import com.eldorado.remoteresources.android.client.ClientLogger;
import com.eldorado.remoteresources.android.client.connection.ClientConnectionManager;
import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization;
import com.eldorado.remoteresources.i18n.RemoteResourcesMessages;
import com.eldorado.remoteresources.ui.RemoteResourcesInitialPanel;
import com.eldorado.remoteresources.ui.actions.InstanceChecker;

public class RemoteResources {
	public static void main(String[] args) {

		if (InstanceChecker.lockInstance("./lock")) {

			final RemoteResourcesServer localServer = RemoteResourcesServer
					.newInstance(args, true);
			localServer.start();

			java.awt.EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {

					RemoteResourcesConfiguration.getInstance().load(
							new File(RemoteResourcesConfiguration
									.getRemoteResourcesDir()
									+ File.separator
									+ "remote.cfg"));

					RemoteResourcesInitialPanel l = new RemoteResourcesInitialPanel();

					while (!localServer.isStarted()) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// do nothing
						}
					}
					try {

						ClientConnectionManager.getInstance().connect(
								ClientConnectionManager.LOCALHOST,
								RemoteResourcesConfiguration.getInstance()
										.getServerPort());
					} catch (Exception e) {
						ClientLogger
								.error(RemoteResources.class,
										RemoteResourcesLocalization
												.getMessage(RemoteResourcesMessages.RemoteResources_Error_ConnectionError),
										e);
					}
				}
			});
		} else {
			JOptionPane
					.showConfirmDialog(
							null,
							RemoteResourcesLocalization
									.getMessage(RemoteResourcesMessages.RemoteResources_OneInstance),
							"Remote Resources", JOptionPane.DEFAULT_OPTION,
							JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
