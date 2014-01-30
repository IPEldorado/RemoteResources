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

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * 
 *
 */
public class ResourcesLoader {
	public static final String RESOURCES_PATH = "com/eldorado/remoteresources/resources/";

	private static final Map<String, Image> cachedImages = new HashMap<String, Image>();

	private static final Map<String, Icon> cachedIcons = new HashMap<String, Icon>();

	/**
	 * 
	 * @param resourceName
	 * @return
	 */
	public static Image getImageResource(String resourceName, String extension,
			int size) {
		Image image = cachedImages.get(getImageKey(resourceName, size));

		if (image == null) {
			try {
				InputStream imageInputStream = loadResourceInputStream("images/"
						+ resourceName + extension);
				image = ImageIO.read(imageInputStream);
				if (size > 0) {
					if (image.getWidth(null) > image.getHeight(null)) {
						image = image.getScaledInstance(size, -1,
								Image.SCALE_SMOOTH);
					} else {
						image = image.getScaledInstance(-1, size,
								Image.SCALE_SMOOTH);
					}
				}
				cachedImages.put(getImageKey(resourceName, size), image);
			} catch (IOException e) {
				// TODO: log the information
			}
		}
		return image;
	}

	public static Image getImageResource(String resourceName, int size) {
		return getImageResource(resourceName, ".png", size);

	}

	public static Icon getIcon(String resourceName, int size) {
		return getIcon(resourceName, ".png", size);
	}

	public static Icon getIcon(String resourceName, String extension, int size) {
		Icon icon = cachedIcons.get(getImageKey(resourceName, size));
		if (icon == null) {
			icon = new ImageIcon(
					getImageResource(resourceName, extension, size));
			cachedIcons.put(getImageKey(resourceName, size), icon);
		}
		return icon;
	}

	public static Image getAnimatedGif(String resourceName) {
		URL url = ClassLoader.getSystemClassLoader().getResource(
				RESOURCES_PATH + "images/" + resourceName + ".gif");
		ImageIcon icon = new ImageIcon(url);
		return icon.getImage();
	}

	private static String getImageKey(String resourceName, int size) {
		return resourceName + "_" + size;
	}

	/**
	 * 
	 * @param resourceName
	 * @return
	 */
	public static Font getFontResource(String resourceName) {
		Font font;

		try {
			InputStream fontInputStream = loadResourceInputStream("fonts/"
					+ resourceName + ".ttf");
			font = Font.createFont(Font.PLAIN, fontInputStream);
		} catch (FontFormatException | IOException | NullPointerException e) {
			font = null;
		}

		return font;

	}

	/**
	 * 
	 * @param resourceName
	 * @return
	 */
	public static StringBuilder getFileResourceContent(String resourceName) {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				loadResourceInputStream("files/" + resourceName)));
		try {
			String line;
			for (; (line = br.readLine()) != null;) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			return null;
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return sb;

	}

	private static InputStream loadResourceInputStream(String resourceName) {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();

		InputStream resourceStream = classLoader
				.getResourceAsStream(RESOURCES_PATH + resourceName);

		return resourceStream;
	}
}
