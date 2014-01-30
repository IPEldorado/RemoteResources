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
public class TouchCommandMessage extends DeviceCommandMessage {

	private static final long serialVersionUID = 6485247348661667223L;

	private final TouchCommandMessageType command;

	private final float x;

	private final float y;

	/**
	 * Create a new touch command
	 * 
	 * @param sequenceNumber
	 *            the message sequence number
	 * @param device
	 *            the device serial number
	 * @param command
	 *            the command to be executed (
	 *            {@link TouchCommandMessage#TOUCH_DOWN},
	 *            {@link TouchCommandMessage#TOUCH_MOVE} or
	 *            {@link TouchCommandMessage#TOUCH_UP})
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 */
	public TouchCommandMessage(int sequenceNumber, String device,
			TouchCommandMessageType command, float x, float y) {
		super(sequenceNumber, device, DeviceCommandMessageType.TOUCH_COMMAND);
		this.command = command;
		this.x = x;
		this.y = y;
	}

	/**
	 * Get the x coordinate to be touched
	 * 
	 * @return the x coordinate
	 */
	public float getX() {
		return x;
	}

	/**
	 * Get the y coordinate to be touched
	 * 
	 * @return the y coordinate
	 */
	public float getY() {
		return y;
	}

	/**
	 * 
	 * @return The command to be executed
	 */
	public TouchCommandMessageType getTouchCommandType() {
		return command;
	}

}
