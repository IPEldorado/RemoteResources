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

import com.eldorado.remoteresources.android.common.connection.messages.Message;
import com.eldorado.remoteresources.android.common.connection.messages.MessageType;

/**
 * The base control message
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public abstract class ControlMessage extends Message {

	private static final long serialVersionUID = 110689265311286266L;

	/**
	 * Check {@link ControlMessageType}
	 */
	private final ControlMessageType messageType;

	/**
	 * Create a new control message
	 * 
	 * @param sequenceNumber
	 * @param messageType
	 */
	public ControlMessage(int sequenceNumber, ControlMessageType messageType) {
		super(sequenceNumber, MessageType.CONTROL_MESSAGE);
		this.messageType = messageType;
	}

	/**
	 * get the control message subtype.
	 * 
	 * @return the message type
	 * @see {@link ControlMessageType} for available types
	 */
	public ControlMessageType getControlMessageType() {
		return messageType;
	}

}
