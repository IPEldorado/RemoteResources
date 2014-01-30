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

package com.eldorado.remoteresources.android.client.screencapture;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import com.eldorado.remoteresources.android.client.AndroidDeviceClient;
import com.eldorado.remoteresources.android.client.ClientChangedListener;
import com.eldorado.remoteresources.android.client.ClientLogger;
import com.eldorado.remoteresources.android.client.connection.Client;
import com.eldorado.remoteresources.android.client.connection.ClientConnectionManager;
import com.eldorado.remoteresources.android.client.connection.DataReceivedListener;
import com.eldorado.remoteresources.ui.EventsListener;
import com.eldorado.remoteresources.ui.ScreenPanel;
import com.eldorado.remoteresources.ui.model.Device;
import com.eldorado.remoteresources.utils.ImageUtils;
import com.eldorado.remoteresources.utils.ResourcesLoader;

/**
 * 
 *
 * 
 */
public class CaptureManager implements ClientChangedListener {

	private static CaptureManager instance;

	private final ScreenPanel canvas;

	private Client client;

	private String deviceSerialNumber;

	private Device userDevice;

	private boolean isRunning;

	private EventsListener mouseListener;

	private final Set<CaptureStateListener> listeners;

	private CaptureManager() {
		canvas = new ScreenPanel();
		listeners = new HashSet<CaptureStateListener>();
		ClientConnectionManager.getInstance().addClientChangedListener(this);
	}

	public static CaptureManager getInstance() {
		if (instance == null) {
			instance = new CaptureManager();
		}

		return instance;
	}

	/**
	 * Adds a new capture listener to this manager. Does nothing if already
	 * added.
	 * 
	 * @param listener
	 *            the listener
	 */

	public void addCaptureStateListener(CaptureStateListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove the listener from this manager listeners
	 * 
	 * @param listener
	 *            the listener to be removed
	 */
	public void removeCaptureStateListener(CaptureStateListener listener) {
		listeners.remove(listener);
	}

	public ScreenPanel getCanvas() {
		return canvas;
	}

	/**
	 * 
	 * @param device
	 */
	public void startCapture(Client client, Device userDevice) {
		canvas.createBufferStrategy(2);

		final Image image = ResourcesLoader.getAnimatedGif("loader");
		try {
			canvas.drawScreen(image, true);
		} catch (IllegalStateException e) {
			// this exception happens very rarely and freezes the application,
			// so ignore it, because it doesn't affect the screen capture
			e.printStackTrace();
		}
		if (!isRunning && (userDevice != null)) {
			isRunning = true;
			deviceSerialNumber = userDevice.getSerialNumber();
			this.client = client;
			this.userDevice = userDevice;
			mouseListener = new EventsListener(client, deviceSerialNumber);
			canvas.addMouseListener(mouseListener);
			canvas.addMouseMotionListener(mouseListener);
			this.client.addDataReceivedListener(deviceSerialNumber,
					new DataReceivedListener() {
						private BufferedImage prevImage = null;

						@Override
						public void newFrameReceived(String serialNumber,
								byte[] frameContent) {
							if (isRunning) {
								BufferedImage currentImage;
								if (prevImage == null) {
									canvas.stopAnimation();
									canvas.setLocation(0, 0);
									canvas.setSize(new Dimension(canvas
											.getParent().getWidth() - 20,
											canvas.getParent().getHeight() - 20));
								}
								try {
									if (frameContent == null) {
										currentImage = prevImage;
									} else {
										currentImage = ImageIO.read(ImageUtils
												.decompress(new ByteArrayInputStream(
														frameContent)));
										prevImage = currentImage;
									}
									canvas.drawScreen(currentImage);
								} catch (IOException e) {
									ClientLogger
											.error(CaptureManager.class,
													"Failed to decompress received frame. Using previous one if available",
													e);
									currentImage = prevImage;
								}
							}
						}
					});
			ClientConnectionManager.getInstance().startDataConnection(client,
					deviceSerialNumber);
			while (!client.isDataConnectionActive(deviceSerialNumber)) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// do nothing
				}
			}

			if (userDevice.getProperty(Device.POLLING_RATE) != null) {
				client.setPollingRate(deviceSerialNumber,
						userDevice.getProperty(Device.POLLING_RATE));
			}
			if (userDevice.getProperty(Device.IMAGE_QUALITY) != null) {
				client.setImageQuality(deviceSerialNumber,
						userDevice.getProperty(Device.IMAGE_QUALITY));
			}
			for (CaptureStateListener listener : listeners) {
				listener.captureStarted(client, deviceSerialNumber);
			}
		}
	}

	public void stopCapture() {
		if (mouseListener != null) {
			canvas.removeMouseListener(mouseListener);
			canvas.removeMouseMotionListener(mouseListener);
		}
		if (isRunning) {
			Client c = client;
			String device = deviceSerialNumber;
			isRunning = false;
			deviceSerialNumber = null;
			client = null;
			userDevice = null;
			canvas.drawScreen(null);
			ClientConnectionManager.getInstance().stopDataConnection(c, device);
			for (CaptureStateListener listener : listeners) {
				listener.captureFinished(c, device);
			}

		}

	}

	/**
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return isRunning;
	}

	@Override
	public void clientConnected(Client client) {
		// do nothing

	}

	@Override
	public void clientDisconnected(Client client) {
		// do nothing

	}

	@Override
	public void deviceConnected(Client client, String device) {
		// do nothing

	}

	@Override
	public void deviceDisconnected(Client client, String device) {
		if (isRunning && device.equals(deviceSerialNumber)) {
			Device dev = userDevice;
			stopCapture();
			JOptionPane
					.showConfirmDialog(
							null,
							"Lost connection with device "
									+ dev.getName()
									+ " ("
									+ dev.getHost()
									+ "). Make sure that both device and host are connected and try again.",
							"Device disconnected", JOptionPane.DEFAULT_OPTION,
							JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void deviceAdded(Client client, String device) {
		// do nothing

	}

	@Override
	public void deviceRemoved(Client client, AndroidDeviceClient device) {
		// do nothing

	}

}
