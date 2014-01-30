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

package com.eldorado.remoteresources.android.common.connection.messages.data;

import com.eldorado.remoteresources.android.common.connection.messages.Message;
import com.eldorado.remoteresources.android.common.connection.messages.MessageType;

/**
 * A data message
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public abstract class DataMessage extends Message {

	private static final long serialVersionUID = 2991317175997003007L;

	private final DataMessageType messageType;

	/**
	 * Create a new data message
	 * 
	 * @param sequenceNumber
	 *            the message sequence number
	 * @param messageType
	 *            the message type
	 * @see {@link DataMessageType} for available types
	 */
	public DataMessage(int sequenceNumber, DataMessageType messageType) {
		super(sequenceNumber, MessageType.DATA_MESSAGE);
		this.messageType = messageType;
	}

	/**
	 * Get this data message subtype.
	 * 
	 * @return the message type
	 * @see {@link DataMessageType} for available types
	 */
	public DataMessageType getDataMessageType() {
		return messageType;
	}
}
