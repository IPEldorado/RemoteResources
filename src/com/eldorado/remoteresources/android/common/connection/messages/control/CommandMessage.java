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

import com.eldorado.remoteresources.utils.SubType;

/**
 * Message with informations to run adb command
 * 
 * @author Rafael Dias Santos
 * 
 */
public abstract class CommandMessage extends ControlMessage {
	// TODO
	protected final String serialNumber;
	protected final String[] params;
	protected final SubType subType;

	public CommandMessage(int sequenceNumber, String serialNumber,
			ControlMessageType messageType, SubType subType, String[] params) {
		super(sequenceNumber, messageType);

		this.serialNumber = serialNumber;
		this.params = params;
		this.subType = subType;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public String[] getParams() {
		return params;
	}

	public SubType getSubType() {
		return subType;
	}

}
