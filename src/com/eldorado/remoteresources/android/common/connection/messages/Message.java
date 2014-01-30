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

package com.eldorado.remoteresources.android.common.connection.messages;

import java.io.Serializable;

/**
 * This class defines the base message that should be exchanged between client
 * and server
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public abstract class Message implements Serializable {

	private static final long serialVersionUID = -1051009758389055769L;

	public static final int MAX_SEQUENCE_NUMBER = 1 << 16;

	/**
	 * The message type: check {@link MessageType}
	 */
	private final MessageType type;

	/**
	 * The message sequence number for the client/server connection
	 */
	private final int sequenceNumber;

	/**
	 * Create a new message with a sequence number and type
	 * 
	 * @param sequenceNumber
	 *            the sequence number
	 * @param type
	 *            the message type
	 */
	public Message(int sequenceNumber, MessageType type) {
		this.type = type;
		this.sequenceNumber = sequenceNumber;
	}

	/**
	 * Get this message type.
	 * 
	 * @return the type of this message
	 * @see {@link MessageType} for available types
	 */
	public MessageType getMessageType() {
		return type;
	}

	/**
	 * Get this message sequence number
	 * 
	 * @return this message sequence number
	 */
	public int getSequenceNumber() {
		return sequenceNumber;
	}

}
