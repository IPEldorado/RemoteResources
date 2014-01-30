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

package com.eldorado.remoteresources.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class RemoteResourcesLocalization {

	public enum RemoteResourcesLocalization_Languages {
		en_US, pt_BR
	};

	private static ResourceBundle bundle = ResourceBundle.getBundle(
			"com.eldorado.remoteresources.i18n.RemoteResourcesMessages",
			Locale.getDefault());

	/**
	 * Get a message with a following id
	 * 
	 * @param message
	 *            the message id
	 * @return the message
	 */
	public static String getMessage(String message) {
		return bundle.getString(message);
	}

	/**
	 * Get a message with the parameters in place
	 * 
	 * @param message
	 *            the message id
	 * @param parameters
	 *            the parameters
	 * @return the compound message
	 */
	public static String getMessage(String message, Object... parameters) {
		return MessageFormat.format(bundle.getString(message), parameters);
	}

	public static void setLanguage(
			RemoteResourcesLocalization_Languages language) {
		switch (language) {
		case en_US:
			bundle = ResourceBundle
					.getBundle(
							"com.eldorado.remoteresources.i18n.RemoteResourcesMessages",
							Locale.getDefault());
			break;
		case pt_BR:
			bundle = ResourceBundle
					.getBundle(
							"com.eldorado.remoteresources.i18n.RemoteResourcesMessages",
							new Locale("pt", "BR"));
			break;
		default:
			bundle = ResourceBundle
					.getBundle(
							"com.eldorado.remoteresources.i18n.RemoteResourcesMessages",
							Locale.getDefault());
		}
	}
}
