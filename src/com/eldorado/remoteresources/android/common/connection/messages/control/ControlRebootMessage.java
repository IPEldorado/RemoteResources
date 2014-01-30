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
 * Message with informations to handle files (pull/push/delete)
 * 
 * @author Rafael Dias Santos
 * 
 */
public class ControlRebootMessage extends CommandMessage {
	private static final long serialVersionUID = 5481207318846024671L;

	public ControlRebootMessage(int sequenceNumber, String serialNumber,
			SubType subType, String[] params) {
		super(sequenceNumber, serialNumber, ControlMessageType.REBOOT_DEVICE,
				subType, params);
	}

	@Override
	public String getSerialNumber() {
		return serialNumber;
	}

	public String getRebootParameter() {
		return params[0];
	}
}
