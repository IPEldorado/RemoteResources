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

package com.eldorado.remoteresources.android.server;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.eldorado.remoteresources.RemoteResourcesConfiguration;

public class ServerLogger {
	private static final Logger logger = Logger
			.getLogger("RemoteResourcesServer");

	private static final String DEFAULT_SERVER_LOGGER_DIR = RemoteResourcesConfiguration
			.getRemoteResourcesDir() != null ? RemoteResourcesConfiguration
			.getRemoteResourcesDir() + File.separator + "logs" : System
			.getProperty("user.home");

	private static final String SERVER_LOGGER_FILENAME = "remote_resources_server.log";

	private ServerLogger() {
	};

	static {
		logger.setLevel(Level.FINE);
		String outputDir = RemoteResourcesConfiguration.getInstance()
				.get(RemoteResourcesConfiguration.LOG_DIR,
						DEFAULT_SERVER_LOGGER_DIR);
		File fOutputDir = new File(outputDir);

		if (!fOutputDir.exists()) {
			fOutputDir.mkdirs();
		}

		if (fOutputDir.exists()) {
			String outputFile = outputDir + File.separator
					+ SERVER_LOGGER_FILENAME;

			try {
				Handler fileHandler = new FileHandler(outputFile);
				fileHandler.setFormatter(new SimpleFormatter());
				logger.addHandler(fileHandler);
			} catch (Exception e) {
				System.err
						.println("Unable to configure client logger instance");
				e.printStackTrace(System.err);
			}
		} else {
			System.err
					.println("Unable to configure client logger instance. Make sure you have enough permissions to create the directory: "
							+ fOutputDir.getAbsolutePath());
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
