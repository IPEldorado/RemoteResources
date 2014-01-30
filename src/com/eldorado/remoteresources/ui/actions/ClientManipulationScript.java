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

package com.eldorado.remoteresources.ui.actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Vector;

import com.eldorado.remoteresources.RemoteResourcesConfiguration;
import com.eldorado.remoteresources.ui.model.Script;

/**
 * Class responsible for start / pause the creation of the script and read the
 * contents recorded
 * 
 * @author Luiz Rafael de Souza
 * 
 */
public class ClientManipulationScript {

	private static final String DEFAULT_CLIENT_SCRIPT_DIR = RemoteResourcesConfiguration
			.getRemoteResourcesDir() != null ? RemoteResourcesConfiguration
			.getRemoteResourcesDir() + File.separator + "scripts" : System //$NON-NLS-1$
			.getProperty("user.home");

	private static final String CLIENT_SCRIPT_FILENAME_EXTENSION = ".txt"; //$NON-NLS-1$

	private static String outputFile = null;
	private BufferedWriter out;

	private static String serialNumber;
	private static String host;
	private static String port;

	public boolean playGenerateScriptFile(String device, String host,
			String port, String path, String name) {

		serialNumber = device;
		ClientManipulationScript.host = host;
		ClientManipulationScript.port = port;

		File fOutputDir = new File(path);

		if (!fOutputDir.exists()) {
			fOutputDir.mkdirs();
		}

		outputFile = path + File.separator + name;

		File f = new File(outputFile);
		if (!f.exists()) {
			try {
				out = new BufferedWriter(new FileWriter(outputFile));
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public void stopGenerateScriptFile(Vector<String> actions) {
		Iterator<String> it = actions.iterator();

		try {
			out.write(serialNumber);
			out.newLine();
			while (it.hasNext()) {
				System.out.println();
				out.write(it.next().toString());
				out.newLine();
			}
			out.close();
			Script.clean();
		} catch (IOException e) {

		}
	}

	public void createScriptFile(String device, String host, String port,
			String path, String name, Vector<String> actions) {
		if (playGenerateScriptFile(device, host, port, path, name)) {
			stopGenerateScriptFile(actions);
		}
		Script.clean();
	}

	// ================== READ A SCRIPT ==================
	public Vector<String> readScriptFile(File f) {
		Vector<String> actions = null;
		BufferedReader reader = null;
		try {
			if (f.canRead()) {
				actions = new Vector<String>();
				reader = new BufferedReader(new FileReader(f));
				String line = reader.readLine();

				while (line != null) {
					actions.add(line);
					line = reader.readLine();
				}
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}

		return actions;
	}

	// ================== UPDATE SCRIPT ==================
	public void updateScriptFile(File f, Vector<String> actions) {

		BufferedWriter writer = null;
		Iterator it = actions.iterator();

		try {
			if (f.canRead() && !it.equals(null)) {
				writer = new BufferedWriter(new PrintWriter(new FileWriter(f)));
				while (it.hasNext()) {
					writer.write(it.next().toString());
					writer.newLine();
				}
			}

			writer.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}

	}
}
