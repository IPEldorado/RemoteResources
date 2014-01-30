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

package com.eldorado.remoteresources;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.eldorado.remoteresources.android.common.connection.IConnectionConstants;
import com.eldorado.remoteresources.i18n.RemoteResourcesLocalization;
import com.eldorado.remoteresources.i18n.RemoteResourcesMessages;
import com.eldorado.remoteresources.utils.ResourcesLoader;

/**
 * This class represents a remote resources configuration file. A configuration
 * file avoids the use of infinite number of command line parameters.
 * 
 * @author Marcelo Marzola Bossoni.
 * 
 */
public class RemoteResourcesConfiguration {

	private static RemoteResourcesConfiguration instance;

	private final Properties configurations;

	private File configFile;

	/**
	 * The ADB binary path in filesystem
	 */
	public static final String ADB_PATH = "ADB_PATH"; //$NON-NLS-1$

	/**
	 * The port where server will run
	 */
	public static final String SERVER_PORT = "SERVER_PORT"; //$NON-NLS-1$

	/**
	 * The directory where log will be written
	 */
	public static final String LOG_DIR = "LOG_DIR"; //$NON-NLS-1$ 

	public static final String SCRIPT_DIR = "SCRIPT_DIR"; //$NON-NLS-1$ 

	/**
	 * The address to bind to. use this only if you are having troubles
	 * determining external host address
	 */
	public static final String BIND_ADDR = "BIND_ADDR"; //$NON-NLS-1$ 

	public static final String LOCK_FILE = "LOCK_FILE";

	public static final String PREFERRED_LANG = "PREFERRED_LANG";

	private RemoteResourcesConfiguration() {
		configurations = new Properties();
	}

	void load(File configFile) {
		this.configFile = configFile;
		FileInputStream inputStream = null;
		try {
			configurations.load(inputStream = new FileInputStream(configFile));
		} catch (Exception e) {
			// do nothing
			System.err
					.println(RemoteResourcesLocalization
							.getMessage(RemoteResourcesMessages.RemoteResourcesConfiguration_Error_UnableToLoad));
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}

	public static RemoteResourcesConfiguration getInstance() {
		if (instance == null) {
			instance = new RemoteResourcesConfiguration();
		}
		return instance;
	}

	public String get(String property, String defaultValue) {
		return configurations.getProperty(property) != null ? configurations
				.getProperty(property) : defaultValue;
	}

	public String get(String property) {
		return configurations.getProperty(property);
	}

	public void set(String property, String value) {
		configurations.setProperty(property, value);
	}

	public int getServerPort() {
		int port = IConnectionConstants.CONTROL_SERVER_DEFAULT_PORT;
		try {
			if (get(SERVER_PORT) != null) {
				port = Integer.parseInt(get(SERVER_PORT));
			}
		} catch (NumberFormatException e) {
			// do nothing
		}
		return port;
	}

	public static String getRemoteResourcesDir() {
		String parent = null;
		try {
			parent = new File(
					URLDecoder.decode(ClassLoader.getSystemClassLoader()
							.getResource(".").getPath(), "UTF-8")).getAbsolutePath();//$NON-NLS-1$//$NON-NLS-2$
		} catch (UnsupportedEncodingException e) {
			// do nothing
		}
		return parent;
	}

	public void store() {
		// needed to create a store() method different from the one in
		// Properties class to store configurations, in order to not lose
		// commented out lines from config file

		BufferedReader br = null;
		StringBuilder confString = new StringBuilder();
		try {
			br = new BufferedReader(new FileReader(configFile));
			String line;
			for (; (line = br.readLine()) != null;) {
				confString.append(line + "\n");
			}
		} catch (FileNotFoundException e) {
			// remote.cfg doesn't exist yet: create this file using template
			// from resources.files package
			try {
				configFile.createNewFile();
			} catch (IOException e1) {
				System.err
						.println(RemoteResourcesLocalization
								.getMessage(RemoteResourcesMessages.RemoteResourcesConfiguration_Error_ConfigFileError));
				return;
			}
			confString = ResourcesLoader
					.getFileResourceContent("template_remote.cfg");
		} catch (IOException e) {
			System.err
					.println(RemoteResourcesLocalization
							.getMessage(RemoteResourcesMessages.RemoteResourcesConfiguration_Error_StoreGenericError));
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					System.err
							.println(RemoteResourcesLocalization
									.getMessage(RemoteResourcesMessages.RemoteResourcesConfiguration_Error_StoreGenericError));
				}
			}
		}

		for (String configuration : configurations.stringPropertyNames()) {
			String newConfigLine = configuration + " = "
					+ configurations.getProperty(configuration);
			Matcher m = Pattern.compile(
					"(#?)" + configuration + "\\s*=\\s*(\\w*)").matcher(
					confString);

			if (m.find()) {
				confString.replace(0, confString.length(),
						m.replaceAll(newConfigLine));

			} else { // if configuration was not present in config file
				confString.append("\n" + newConfigLine);

			}
		}

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(configFile));
			bw.write(confString.toString());
			bw.close();
		} catch (IOException e) {
			System.err
					.println(RemoteResourcesLocalization
							.getMessage(RemoteResourcesMessages.RemoteResourcesConfiguration_Error_UnableToStore));
		}

	}
}
