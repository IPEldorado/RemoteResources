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
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import com.eldorado.remoteresources.android.common.connection.IConnectionConstants;
import com.eldorado.remoteresources.android.server.ServerLogger;
import com.eldorado.remoteresources.android.server.connection.Server;
import com.eldorado.remoteresources.android.server.connection.ServerConnectionManager;
import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization;
import com.eldorado.remoteresources.i18n.RemoteResourcesMessages;
import com.eldorado.remoteresources.utils.SocketUtils;

/**
 * This class controls the initialization of a Remote Resources Server instance.
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class RemoteResourcesServer extends Thread {

	// TODO: verify the use of weupnp to create port mapping through NAT.
	private ServerSocket controlServerSocket = null;

	private boolean cancel = false;

	private boolean started = false;

	private RemoteResourcesServer(int port, boolean local) {

		try {
			controlServerSocket = SocketUtils.newServerSocket(port, local);
			setName(RemoteResourcesLocalization
					.getMessage(
							RemoteResourcesMessages.RemoteResourcesServer_UI_ThreadName,
							controlServerSocket.getInetAddress()));
		} catch (IOException e) {
			ServerLogger
					.error(RemoteResourcesServer.class,
							RemoteResourcesLocalization
									.getMessage(
											RemoteResourcesMessages.RemoteResourcesServer_Error_UnableToBind,
											port), e);
		}

	}

	@Override
	public void run() {
		ServerLogger
				.info(RemoteResourcesServer.class,
						RemoteResourcesLocalization
								.getMessage(
										RemoteResourcesMessages.RemoteResourcesServer_Info_StartingServer,
										controlServerSocket.getInetAddress()
												+ ":"
												+ controlServerSocket
														.getLocalPort()), null);
		while (!cancel) {
			try {
				started = true;
				new Server(controlServerSocket.accept()).start();
			} catch (IOException e) {
				if (!cancel) {
					ServerLogger
							.error(RemoteResourcesServer.class,
									RemoteResourcesLocalization
											.getMessage(
													RemoteResourcesMessages.RemoteResourcesServer_Error_WaitingForConnection,
													controlServerSocket
															.getInetAddress()),
									e);
				}
			}

		}
	}

	public void cancel() {
		cancel = true;
		ServerLogger
				.info(RemoteResourcesServer.class,
						RemoteResourcesLocalization
								.getMessage(
										RemoteResourcesMessages.RemoteResourcesServer_Info_StoppingServer,
										controlServerSocket.getInetAddress()),
						null);
		if (controlServerSocket != null) {
			try {
				controlServerSocket.close();
			} catch (IOException e) {
				// do nothing
			}
		}
	}

	public static RemoteResourcesServer newInstance(String[] args, boolean local) {
		// Disable chimpchat log
		java.util.logging.Logger.getLogger("com.android.chimpchat").setLevel( //$NON-NLS-1$
				java.util.logging.Level.OFF);
		// load configuration file
		loadConfigurationFile();
		// process command line arguments - command line defined arguments will
		// overwrite configuration file
		if (args != null) {
			processCommandLine(args);
		}

		// get basic needed arguments
		String adbPath = RemoteResourcesConfiguration.getInstance().get(
				RemoteResourcesConfiguration.ADB_PATH, "adb"); //$NON-NLS-1$
		int port = IConnectionConstants.CONTROL_SERVER_DEFAULT_PORT;
		try {
			String portStr = RemoteResourcesConfiguration.getInstance().get(
					RemoteResourcesConfiguration.SERVER_PORT);
			if (portStr != null) {
				port = Integer.parseInt(portStr);
			}
		} catch (NumberFormatException e) {
			// do nothing
		}

		// initialize jvm debug bridge instance
		ServerConnectionManager.getInstance().initializeDebugBridge(adbPath);

		return new RemoteResourcesServer(port, local);
	}

	public boolean isStarted() {
		return started;
	}

	public static void main(String[] args) {

		RemoteResourcesServer server = newInstance(args, false);
		server.start();
		try {
			server.join();
		} catch (InterruptedException e) {
			// do nothing
		}
	}

	static void loadConfigurationFile() {

		String rrparent = RemoteResourcesConfiguration.getRemoteResourcesDir();
		if (rrparent != null) {
			File configFile = new File(rrparent, "remote.cfg"); //$NON-NLS-1$
			if (configFile.canRead()) {
				RemoteResourcesConfiguration.getInstance().load(configFile);
			} else {
				System.err
						.println(RemoteResourcesLocalization
								.getMessage(RemoteResourcesMessages.RemoteResourcesServer_Warning_NoConfigFile));
			}
		} else {
			System.err
					.println(RemoteResourcesLocalization
							.getMessage(RemoteResourcesMessages.RemoteResourcesServer_Warning_NoConfigFile));
		}
	}

	static void processCommandLine(String[] arguments) {

		if (arguments.length > 0) {
			ArrayList<String> args = new ArrayList<String>();
			for (String arg : arguments) {
				args.add(arg);
			}

			while (args.size() > 0) {
				if ("-a".equalsIgnoreCase(args.get(0))) { //$NON-NLS-1$
					args.remove(0);
					try {
						File adbFile = new File(args.get(0));
						if (adbFile.exists()) {
							args.remove(0);
							RemoteResourcesConfiguration.getInstance().set(
									RemoteResourcesConfiguration.ADB_PATH,
									adbFile.getPath());
						} else {
							System.err
									.println(RemoteResourcesLocalization
											.getMessage(RemoteResourcesMessages.RemoteResourcesServer_Error_AdbNotFound));
						}
					} catch (IndexOutOfBoundsException e) {
						System.err
								.println(RemoteResourcesLocalization
										.getMessage(RemoteResourcesMessages.RemoteResourcesServer_Error_AdbNoPath));
					}
				} else if ("-p".equalsIgnoreCase(args.get(0))) {
					args.remove(0);
					try {
						String port = args.get(0);
						args.remove(0);

						Integer.valueOf(port, 10);
						RemoteResourcesConfiguration.getInstance().set(
								RemoteResourcesConfiguration.SERVER_PORT, port);

					} catch (NumberFormatException e) {
						System.err
								.println(RemoteResourcesLocalization
										.getMessage(RemoteResourcesMessages.RemoteResourcesServer_Error_InvalidPortNumber));
					} catch (IndexOutOfBoundsException e) {
						System.err
								.println(RemoteResourcesLocalization
										.getMessage(RemoteResourcesMessages.RemoteResourcesServer_Error_NoPortNumber));
					}
				} else {
					args.remove(0);
					System.err
							.println("Usage: java com.eldorado.remoteresources.RemoteResourcesServer [-a path/to/adb] [-p <PORT_NUMBER>]"); //$NON-NLS-1$
				}
			}
		}
	}
}
