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

package com.eldorado.remoteresources.android.common.connection.messages.control;

/**
 * This class request a new image frame asynchronously. Useful for getting a
 * screenshot with full resolution.
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class GetNewFrameMessage extends ControlMessage {

	private static final long serialVersionUID = 4765869610561738390L;

	private final String deviceSerialNumber;

	private final int quality;

	/**
	 * Create a message requesting new data
	 * 
	 * @param sequenceNumber
	 *            this message sequence number
	 * @param deviceSerialNumber
	 *            the device serial number
	 * @param quality
	 *            the image quality (0 to 100)
	 */
	public GetNewFrameMessage(int sequenceNumber, String deviceSerialNumber,
			int quality) {
		super(sequenceNumber, ControlMessageType.FORCE_GET_NEW_FRAME);
		this.deviceSerialNumber = deviceSerialNumber;
		this.quality = quality;
	}

	/**
	 * Get the requested device serial number
	 * 
	 * @return the serial number
	 */
	public String getDeviceSerialNumber() {
		return deviceSerialNumber;
	}

	/**
	 * Get the requested image quality
	 * 
	 * @return the image quality
	 */
	public int getQuality() {
		return quality;
	}

}
