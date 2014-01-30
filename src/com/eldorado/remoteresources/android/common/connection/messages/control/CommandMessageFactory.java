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

import com.eldorado.remoteresources.utils.Command;

/**
 * Message with informations to run shell command
 * 
 * @author Rafael Dias Santos
 * 
 */
public class CommandMessageFactory {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1467637828016992406L;

	public static CommandMessage getCommandMessage(Command command,
			int sequenceNumber, String serialNumber) {

		switch (command.getCommandType()) {
		case SHELL_COMMAND: {
			return new ControlRunShellCommandMessage(sequenceNumber,
					serialNumber, command.getSubType(), command.getParams());
		}
		case FILE_HANDLING: {
			return new ControlFileHandlingMessage(sequenceNumber, serialNumber,
					command.getSubType(), command.getParams());
		}
		case PACKAGE_HANDLING: {
			return new ControlPackageHandlingMessage(sequenceNumber,
					serialNumber, command.getSubType(), command.getParams());
		}
		case REBOOT: {
			return new ControlRebootMessage(sequenceNumber, serialNumber,
					command.getSubType(), command.getParams());
		}
		}

		return null;
	}
}
