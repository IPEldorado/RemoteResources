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
 * The touch command message
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class TypeCommandMessage extends DeviceCommandMessage {

	private static final long serialVersionUID = 4661705511854391742L;

	private final String text;

	/**
	 * Create a new touch command
	 * 
	 * @param sequenceNumber
	 *            the message sequence number
	 * @param device
	 *            the device serial number
	 * @param text
	 *            the text to type
	 */
	public TypeCommandMessage(int sequenceNumber, String device, String text) {
		super(sequenceNumber, device, DeviceCommandMessageType.TYPE_COMMAND);
		this.text = text;
	}

	/**
	 * get the text to type
	 * 
	 * @return the text to type
	 */
	public String getText() {
		return text;
	}
}
