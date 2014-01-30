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

package com.eldorado.remoteresources.ui;

import javax.swing.JButton;

import com.eldorado.remoteresources.android.common.Keys;

/**
 * 
 * 
 *
 */
public class KeyButton extends JButton {

	private static final long serialVersionUID = 8637146572121840078L;

	private final Keys key;

	/**
	 * 
	 * @param key
	 */
	public KeyButton(Keys key) {
		this.key = key;
	}

	/**
	 * 
	 * @return
	 */
	public Keys getKey() {
		return key;
	}
}
