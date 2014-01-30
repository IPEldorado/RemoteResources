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

import com.eldorado.remoteresources.android.common.Status;

/**
 * This control message acknowledges a control message. If there are any need to
 * data to be sent to client, it can be done through data field.
 * 
 * The ack message sequence number is the same of the message that originated
 * this ack
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class AcknowledgmentMessage extends ControlMessage {

	private static final long serialVersionUID = -2892553829246464532L;

	private final Status status;

	private final Object data;

	public AcknowledgmentMessage(int reqMessageSequenceNumber, Status status,
			Object data) {
		super(reqMessageSequenceNumber, ControlMessageType.ACKNOWLEDGMENT);
		this.status = status;
		this.data = data;
	}

	/**
	 * The acknowledgment status
	 * 
	 * @return
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Get this ack message the associated data
	 * 
	 * @return the data, or null if no data
	 */
	public Object getData() {
		return data;
	}
}
