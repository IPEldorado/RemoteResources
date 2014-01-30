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

public class FileHandlingCommand extends Command {

	public FileHandlingCommand(CommandType type, SubType subType,
			String[] params) {
		this.params = params;
		this.subType = subType;
		this.type = type;
	}

	@Override
	public boolean areParametersValid() {
		if (null == params) {
			return false;
		}

		return (params.length >= 2); // TODO add support for flags
	}

}
