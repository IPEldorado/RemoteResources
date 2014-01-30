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

package com.eldorado.remoteresources.android.client;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.eldorado.remoteresources.RemoteResourcesConfiguration;
import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization;
import com.eldorado.remoteresources.i18n.RemoteResourcesMessages;

public class ClientLogger {
	private static final Logger logger = Logger
			.getLogger("RemoteResourcesClient"); //$NON-NLS-1$

	private static final String DEFAULT_CLIENT_LOGGER_DIR = RemoteResourcesConfiguration
			.getRemoteResourcesDir() != null ? RemoteResourcesConfiguration
			.getRemoteResourcesDir() + File.separator + "logs" : System //$NON-NLS-1$
			.getProperty("user.home");

	private static final String CLIENT_LOGGER_FILENAME = "remote_resources_client.log"; //$NON-NLS-1$

	private ClientLogger() {
	};

	static {
		logger.setLevel(Level.FINE);
		String outputDir = RemoteResourcesConfiguration.getInstance()
				.get(RemoteResourcesConfiguration.LOG_DIR,
						DEFAULT_CLIENT_LOGGER_DIR);
		File fOutputDir = new File(outputDir);

		if (!fOutputDir.exists()) {
			fOutputDir.mkdirs();
		}

		if (fOutputDir.exists()) {
			String outputFile = outputDir + File.separator
					+ CLIENT_LOGGER_FILENAME;

			try {
				Handler fileHandler = new FileHandler(outputFile);
				fileHandler.setFormatter(new SimpleFormatter());
				logger.addHandler(fileHandler);
			} catch (Exception e) {
				System.err
						.println(RemoteResourcesLocalization
								.getMessage(RemoteResourcesMessages.ClientLogger_Error_UnableToConfigure));
				e.printStackTrace(System.err);
			}
		} else {
			System.err
					.println(RemoteResourcesLocalization
							.getMessage(
									RemoteResourcesMessages.ClientLogger_Error_InsufficientPermissions,
									fOutputDir.getAbsolutePath()));
		}
	}

	public static void error(Class<?> clazz, String message, Throwable exception) {
		logger.logp(Level.SEVERE, clazz.getName(), null, message, exception);
	}

	public static void warning(Class<?> clazz, String message,
			Throwable exception) {
		logger.logp(Level.WARNING, clazz.getName(), null, message, exception);
	}

	public static void info(Class<?> clazz, String message, Throwable exception) {
		logger.logp(Level.INFO, clazz.getName(), null, message, exception);
	}

	public static void debug(Class<?> clazz, String message, Throwable exception) {
		logger.logp(Level.FINE, clazz.getName(), null, message, exception);
	}

}
