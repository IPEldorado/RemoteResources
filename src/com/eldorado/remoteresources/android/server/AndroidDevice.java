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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.chimpchat.adb.AdbChimpDevice;
import com.android.chimpchat.core.TouchPressType;
import com.android.ddmlib.IDevice;
import com.eldorado.remoteresources.android.common.Keys;
import com.eldorado.remoteresources.utils.StringUtils;

/**
 * 
 * 
 *
 */
public class AndroidDevice {
	/**
	 * 
	 */
	private IDevice device;

	/**
	 * 
	 */
	private AdbChimpDevice chimpDevice;

	/**
	 * 
	 */
	private final String serialNumber;

	/**
	 * 
	 */
	private String deviceModel;

	/**
	 * 
	 */
	private Dimension screenSize;

	/**
	 * 
	 */
	private static final String KEYCODE_SPACE = String.valueOf(62);

	/**
	 * 
	 */
	private static final Pattern[] DISPLAY_SIZE_PATTERNS = {
			Pattern.compile("\\scur=(?<width>\\d+)x(?<height>\\d+)\\s",
					Pattern.CASE_INSENSITIVE),
			Pattern.compile(
					"displaywidth\\s*\\=\\s*(?<width>\\d+).*displayheight\\s*\\=\\s*(?<height>\\d+)",
					Pattern.CASE_INSENSITIVE) };

	/**
	 * 
	 * @param device
	 */
	public AndroidDevice(IDevice device) {
		this.device = device;
		serialNumber = device.getSerialNumber();
		chimpDevice = new AdbChimpDevice(device);
		getScreenSize();
	}

	public IDevice getDevice() {
		return device;
	}

	public void setDevice(IDevice device) {
		this.device = device;
	}

	/**
	 * 
	 * @return
	 */
	public String getDeviceModel() {
		if (StringUtils.isEmpty(deviceModel)) {
			deviceModel = device != null ? device
					.getProperty("ro.product.model") : "";
		}

		return deviceModel;
	}

	/**
	 * 
	 * @return
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

	@Override
	public String toString() {
		return getDeviceModel();// + "\n\r" + getSerialNumber();
	}

	/**
	 * 
	 * @return
	 */
	public BufferedImage getScreenshot() {
		return isOnline() ? chimpDevice.takeSnapshot().getBufferedImage()
				: null;
	}

	public String getShellCommand(String command) {
		return chimpDevice.shell(command);
	}

	/**
	 * 
	 * @return
	 */
	public boolean isOnline() {
		return (device != null) && device.isOnline();
	}

	/**
	 * 
	 */
	public void dispose() {
		try {
			chimpDevice.getManager().quit();
			device = null;
			chimpDevice = null;
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof AndroidDevice ? ((AndroidDevice) obj)
				.getSerialNumber().equalsIgnoreCase(serialNumber) : false;
	}

	/**
	 * 
	 * @param key
	 * @param pressType
	 */
	private void pressKey(String key, TouchPressType pressType) {
		if (isOnline()) {
			chimpDevice.press(key, pressType);
		}
	}

	/**
	 * 
	 * @param key
	 */
	public void keyDown(Keys key) {
		pressKey(key.toString(), TouchPressType.DOWN);
	}

	/**
	 * 
	 * @param key
	 */
	public void keyUp(Keys key) {
		if (key == Keys.POWER) {
			chimpDevice.wake();
		}

		pressKey(key.toString(), TouchPressType.UP);
	}

	/**
	 * 
	 * @param xRate
	 * @param yRate
	 * @param pressType
	 */
	private void touch(float xRate, float yRate, TouchPressType pressType) {
		Dimension screenSize = getScreenSize();

		int x = (int) (screenSize.width * xRate);
		int y = (int) (screenSize.height * yRate);

		if (pressType != null) {
			chimpDevice.touch(x, y, pressType);
		} else {
			try {
				chimpDevice.getManager().touchMove(x, y);
			} catch (IOException e) {
				// TODO: improve exception handling
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param xRate
	 * @param yRate
	 */
	public void touchDown(float xRate, float yRate) {
		touch(xRate, yRate, TouchPressType.DOWN);
	}

	/**
	 * 
	 * @param xRate
	 * @param yRate
	 */
	public void touchMove(float xRate, float yRate) {
		touch(xRate, yRate, null);
	}

	/**
	 * 
	 * @param xRate
	 * @param yRate
	 */
	public void touchUp(float xRate, float yRate) {
		touch(xRate, yRate, TouchPressType.UP);
	}

	/**
	 * 
	 * @return
	 */
	public Dimension getScreenSize() {
		if (screenSize == null) {
			String dumpsysResult = chimpDevice.shell("dumpsys window");

			for (Pattern pattern : DISPLAY_SIZE_PATTERNS) {
				Matcher matcher = pattern.matcher(dumpsysResult);

				if (matcher.find()) {
					int width = Integer.parseInt(matcher.group("width"));
					int height = Integer.parseInt(matcher.group("height"));

					screenSize = new Dimension(width, height);
				}
			}
		}

		return screenSize;
	}

	public void type(String text) {
		StringTokenizer textAux = new StringTokenizer(text, " ");
		while (textAux.hasMoreTokens()) {
			// chimpDevice.type(textAux.nextToken());
			chimpDevice.shell("input text " + textAux.nextToken());
			chimpDevice.shell("input keyevent ".concat(KEYCODE_SPACE));
		}
	}
}
