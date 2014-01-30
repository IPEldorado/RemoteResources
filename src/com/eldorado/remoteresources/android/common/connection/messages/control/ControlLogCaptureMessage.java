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
 * Message with informations to capture logcat file
 * 
 * @author Luiz Rafael de Souza
 * 
 */
public class ControlLogCaptureMessage extends ControlMessage {

	private final String serialNumber;
	private final ControlLogCaptureTypeMessage level;

	public ControlLogCaptureMessage(int sequenceNumber, String serialNumber,
			ControlLogCaptureTypeMessage level) {
		super(sequenceNumber, ControlMessageType.GET_LOGS);
		this.serialNumber = serialNumber;
		this.level = level;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public String getLogLevel() {
		return level.toString();
	}

}
