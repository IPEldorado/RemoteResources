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

package com.eldorado.remoteresources.android.common.connection;

import java.util.LinkedHashMap;
import java.util.Map;

import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization;
import com.eldorado.remoteresources.i18n.RemoteResourcesMessages;

public abstract class IConnectionConstants {

	public static final int CONTROL_SERVER_DEFAULT_PORT = 54321;

	public static final long DEFAULT_FRAME_DELAY = 500;

	public static final int DEFAULT_IMAGE_QUALITY = 50;

	public static final Map<String, String> pollingRates = new LinkedHashMap<String, String>();

	public static final Map<String, String> qualities = new LinkedHashMap<String, String>();

	public static final String LOCALHOST = "localhost";

	static {
		pollingRates
				.put("0",
						RemoteResourcesLocalization
								.getMessage(RemoteResourcesMessages.IConnnectionConstants_PollingRates1));
		pollingRates
				.put("100",
						RemoteResourcesLocalization
								.getMessage(RemoteResourcesMessages.IConnnectionConstants_PollingRates2));
		pollingRates
				.put("200",
						RemoteResourcesLocalization
								.getMessage(RemoteResourcesMessages.IConnnectionConstants_PollingRates3));
		pollingRates
				.put("500",
						RemoteResourcesLocalization
								.getMessage(RemoteResourcesMessages.IConnnectionConstants_PollingRates4));
		pollingRates
				.put("1000",
						RemoteResourcesLocalization
								.getMessage(RemoteResourcesMessages.IConnnectionConstants_PollingRates5));
		pollingRates
				.put("2000",
						RemoteResourcesLocalization
								.getMessage(RemoteResourcesMessages.IConnnectionConstants_PollingRates6));

		qualities
				.put("15",
						RemoteResourcesLocalization
								.getMessage(RemoteResourcesMessages.IConnnectionConstants_Qualities1));
		qualities
				.put("20",
						RemoteResourcesLocalization
								.getMessage(RemoteResourcesMessages.IConnnectionConstants_Qualities2));
		qualities
				.put("30",
						RemoteResourcesLocalization
								.getMessage(RemoteResourcesMessages.IConnnectionConstants_Qualities3));
		qualities
				.put("50",
						RemoteResourcesLocalization
								.getMessage(RemoteResourcesMessages.IConnnectionConstants_Qualities4));
		qualities
				.put("70",
						RemoteResourcesLocalization
								.getMessage(RemoteResourcesMessages.IConnnectionConstants_Qualities5));
		qualities
				.put("90",
						RemoteResourcesLocalization
								.getMessage(RemoteResourcesMessages.IConnnectionConstants_Qualities6));
	}
}
