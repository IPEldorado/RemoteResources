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

package com.eldorado.remoteresources.utils;

public class CommandFactory {
	private static final String ADB_SHELL_COMMAND_PATTERN = "adb shell";
	private static final String ADB_PULL_COMMAND_PATTERN = "adb pull";
	private static final String ADB_PUSH_COMMAND_PATTERN = "adb push";
	private static final String ADB_INSTALL_COMMAND_PATTERN = "adb install";
	private static final String ADB_UNINSTALL_COMMAND_PATTERN = "adb uninstall";
	private static final String ADB_REBOOT_COMMAND_PATTERN = "adb reboot";

	public static Command getCommand(String command) {
		if (command == null) {
			return null;
		}

		if (command.startsWith(ADB_SHELL_COMMAND_PATTERN)) {
			Command c = new ShellCommand(CommandType.SHELL_COMMAND,
					SubType.NONE,
					new String[] { command.substring(ADB_SHELL_COMMAND_PATTERN
							.length()) });

			return c.areParametersValid() ? c : null;
		}

		if (command.startsWith(ADB_PULL_COMMAND_PATTERN)) {
			Command c = new FileHandlingCommand(CommandType.FILE_HANDLING,
					SubType.PULL, getParams(ADB_PULL_COMMAND_PATTERN, command));

			return c.areParametersValid() ? c : null;
		}

		if (command.startsWith(ADB_PUSH_COMMAND_PATTERN)) {
			Command c = new FileHandlingCommand(CommandType.FILE_HANDLING,
					SubType.PUSH, getParams(ADB_PUSH_COMMAND_PATTERN, command));

			return c.areParametersValid() ? c : null;
		}

		if (command.startsWith(ADB_INSTALL_COMMAND_PATTERN)) {
			Command c = new PackageHandlingCommand(
					CommandType.PACKAGE_HANDLING, SubType.INSTALL, getParams(
							ADB_INSTALL_COMMAND_PATTERN, command));

			return c.areParametersValid() ? c : null;
		}

		if (command.startsWith(ADB_UNINSTALL_COMMAND_PATTERN)) {
			Command c = new PackageHandlingCommand(
					CommandType.PACKAGE_HANDLING, SubType.UNINSTALL, getParams(
							ADB_UNINSTALL_COMMAND_PATTERN, command));

			return c.areParametersValid() ? c : null;
		}

		if (command.startsWith(ADB_REBOOT_COMMAND_PATTERN)) {
			Command c = new RebootCommand(CommandType.REBOOT, getParams(
					ADB_REBOOT_COMMAND_PATTERN, command));

			return c.areParametersValid() ? c : null;
		}

		return null;
	}

	private static String[] getParams(String commandPattern, String command) {
		String params = command.substring(commandPattern.length());

		return (null == params) ? null : params.trim().split(" ");
	}
}
