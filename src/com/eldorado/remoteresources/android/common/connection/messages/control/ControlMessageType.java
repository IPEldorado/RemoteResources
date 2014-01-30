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
 * This class defines the available control message types
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public enum ControlMessageType {
	GET_DEVICE_LIST, EXECUTE_DEVICE_COMMAND, ACKNOWLEDGMENT, SETUP_DEVICE, FORCE_GET_NEW_FRAME, GET_LOGS, MAKE_STOP_RUNNER, RUN_SHELL_COMMAND, HANDLE_FILE, HANDLE_PACKAGE, REBOOT_DEVICE;
}
