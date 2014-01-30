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

package com.eldorado.remoteresources.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import com.eldorado.remoteresources.RemoteResourcesConfiguration;
import com.eldorado.remoteresources.android.common.connection.IConnectionConstants;

public class SocketUtils {

	/**
	 * Get a new server socket in the desired port.
	 * 
	 * @param port
	 *            the port number, or 0 to get an ephemeral port
	 * @param local
	 * @return the server socket
	 * @throws IOException
	 *             if the binding fails
	 * @throws UnknownHostException
	 *             if getting binding host fails (mostly problematic when using
	 *             upnp)
	 */
	public static ServerSocket newServerSocket(int port, boolean local)
			throws UnknownHostException, IOException {
		return new ServerSocket(port, 1,
				local ? InetAddress.getByName(IConnectionConstants.LOCALHOST)
						: getBindAddress());
	}

	/**
	 * Create a new server socket
	 * 
	 * @param bindAddr
	 *            the bind address
	 * @param port
	 *            the port
	 * @return the new instance of server socket already bound
	 * @throws UnknownHostException
	 *             if the host name cannot be resolved
	 * @throws IOException
	 *             if binding fails
	 */
	public static ServerSocket newServerSocket(String bindAddr, int port)
			throws UnknownHostException, IOException {
		return new ServerSocket(port, 1, InetAddress.getByName(bindAddr));
	}

	/**
	 * Get the bind address. This method is also responsible for //TODO: setup
	 * automatic port forwarding using upnp
	 * 
	 * @return the bind address
	 * @throws UnknownHostException
	 */
	public static InetAddress getBindAddress() throws UnknownHostException {
		return RemoteResourcesConfiguration.getInstance().get(
				RemoteResourcesConfiguration.BIND_ADDR) != null ? InetAddress
				.getByName(RemoteResourcesConfiguration.getInstance().get(
						RemoteResourcesConfiguration.BIND_ADDR)) : InetAddress
				.getLocalHost();
	}

}
