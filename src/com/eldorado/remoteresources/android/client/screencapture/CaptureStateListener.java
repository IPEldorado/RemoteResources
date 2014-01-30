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

package com.eldorado.remoteresources.android.client.screencapture;

import com.eldorado.remoteresources.android.client.connection.Client;

/**
 * Capture listener intends to notify about changes in capture thread state.
 * 
 * @see CaptureManager#addCaptureStateListener(CaptureStateListener)
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public interface CaptureStateListener {

	/**
	 * Notifies about capture started on a certain client for a specific device
	 * 
	 * @param client
	 *            the client
	 * @param device
	 *            the device
	 */
	public void captureStarted(Client client, String device);

	/**
	 * Notifies about capture finished on a certain client for a specific device
	 * 
	 * @param client
	 *            the client
	 * @param device
	 *            the device
	 */
	public void captureFinished(Client client, String device);

}
