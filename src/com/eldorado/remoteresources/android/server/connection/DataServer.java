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

package com.eldorado.remoteresources.android.server.connection;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import com.eldorado.remoteresources.android.common.Status;
import com.eldorado.remoteresources.android.common.connection.IConnectionConstants;
import com.eldorado.remoteresources.android.common.connection.messages.data.NewFrameMessage;
import com.eldorado.remoteresources.android.server.AndroidDevice;
import com.eldorado.remoteresources.android.server.ServerLogger;
import com.eldorado.remoteresources.utils.ImageUtils;
import com.eldorado.remoteresources.utils.SocketUtils;

/**
 * A device server intended to serve data through a dedicated socket. This
 * server is device bound.
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class DataServer extends Thread {

	/**
	 * The device serving data
	 */
	private final AndroidDevice device;

	/**
	 * The socket to serve data (notice that we always use the passive data
	 * serving: socket is always opened from client to server)
	 */
	private ServerSocket dataServer;

	/**
	 * The status of this data server
	 */
	private final Status status = Status.OK;

	/**
	 * Readiness of this server
	 */
	private boolean ready = false;

	/**
	 * Control whether this thread should be canceled or not
	 */
	private boolean cancel = false;

	/**
	 * The capture threads actually running. Notice that currently we have just
	 * one thread at a time
	 */
	private final List<CaptureThread> captureThreads = new ArrayList<CaptureThread>();

	/**
	 * The address to bind to
	 */
	private final String bindAddr;

	/**
	 * Time to wait between data
	 */
	private long frameDelay = IConnectionConstants.DEFAULT_FRAME_DELAY;

	private int imageQuality = IConnectionConstants.DEFAULT_IMAGE_QUALITY;

	public DataServer(AndroidDevice device, String bindAddr) {
		super("DeviceDataServer - " + device.getSerialNumber());
		this.device = device;
		if (bindAddr == null) {
			this.bindAddr = IConnectionConstants.LOCALHOST;
		} else {
			this.bindAddr = bindAddr;
		}
	}

	@Override
	public void run() {
		try {
			// bind to address and say we are ready to go
			dataServer = SocketUtils.newServerSocket(bindAddr, 0);
			ServerLogger.info(
					DataServer.class,
					"Setting up data server for device ["
							+ device.getSerialNumber() + "]", null);
			ServerLogger.debug(DataServer.class, "Data server address is "
					+ dataServer.getLocalSocketAddress(), null);
			setName("DeviceDataServer - " + device.getSerialNumber() + "@"
					+ dataServer.getInetAddress());
			ready = true;
			while (!cancel) {
				Socket dataSocket = dataServer.accept();
				CaptureThread captureThread = new CaptureThread(dataSocket);
				captureThreads.add(captureThread);
				captureThread.start();
			}
		} catch (IOException e) {
			ServerLogger.error(
					DataServer.class,
					"Error creating data server for device ["
							+ device.getSerialNumber()
							+ "]. Shutting down data server.", e);
		} finally {
			close();
		}
	}

	public void cancel() throws IOException {
		for (CaptureThread thread : captureThreads) {
			thread.cancel();
		}
		cancel = true;
		dataServer.close();
	}

	/**
	 * Get the reserved address to this server. This allow us to notify client
	 * in which port it should connect
	 * 
	 * @return
	 */
	public SocketAddress getAddress() {
		return dataServer.getLocalSocketAddress();
	}

	/**
	 * Set time between data chunks
	 * 
	 * @param timeBetweenData
	 */
	public void setFrameDelay(long frameDelay) {
		this.frameDelay = frameDelay;
	}

	/**
	 * Get time between data chunks
	 * 
	 * @return the current time between data chunks
	 */
	public long getFrameDelay() {
		return frameDelay;
	}

	/**
	 * Set the image quality
	 * 
	 * @param imageQuality
	 *            the quality of image between 0 and 100
	 */
	public void setImageQuality(int imageQuality) {
		this.imageQuality = imageQuality;
	}

	/**
	 * Get the current image quality being used
	 * 
	 * @return the image quality
	 */
	public int getImageQuality() {
		return imageQuality;
	}

	/**
	 * Get this server status
	 * 
	 * @return the server status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Check if this server is ready to send data. By ready means that it is
	 * bound to some port.
	 * 
	 * @return
	 */
	public boolean isReady() {
		return ready;
	}

	/**
	 * The inner capture thread responsible to send the data
	 * 
	 * @author Marcelo Marzola Bossoni
	 * 
	 */
	class CaptureThread extends Thread {

		private final Socket dataSocket;

		private boolean cancel = false;

		public CaptureThread(Socket dataSocket) {
			super("Capture Thread - " + device.getSerialNumber());
			this.dataSocket = dataSocket;
		}

		@Override
		public void run() {
			ObjectOutputStream outputStream = null;
			BufferedImage previousImage = null;
			try {
				outputStream = new ObjectOutputStream(
						dataSocket.getOutputStream());
				while (!cancel) {
					long startTime = System.currentTimeMillis();
					BufferedImage newImage = device.getScreenshot();
					byte[] buffer = null;
					// only send bytes when images differ
					if (!ImageUtils.isEqual(newImage, previousImage)) {
						buffer = ImageUtils.compress(newImage, imageQuality)
								.toByteArray();
						long endTime = System.currentTimeMillis();
						ServerLogger.debug(DataServer.class, "Screenshot took "
								+ (endTime - startTime), null);
						previousImage = newImage;
					}
					outputStream.writeObject(new NewFrameMessage(0, buffer));
					try {
						Thread.sleep(frameDelay);
					} catch (InterruptedException e) {
						// do nothing
					}
				}
			} catch (IOException e) {
				ServerLogger.error(
						Server.class,
						"Error sending data package to client ["
								+ dataSocket.getRemoteSocketAddress()
								+ "]. Shutting capture thread down.", e);
			} finally {
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
						// do nothing
					}
				}
				try {
					dataSocket.close();
				} catch (IOException e) {
					// do nothing
				}
			}

		}

		public void cancel() {
			cancel = true;
		}

	}

	/**
	 * 
	 * close this data server
	 */
	public void close() {
		ServerLogger.info(DataServer.class, "Closing data server for device ["
				+ device.getSerialNumber() + "]", null);
		for (CaptureThread thread : captureThreads) {
			thread.cancel();
		}
		for (CaptureThread thread : captureThreads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// do nothing
			}
		}
		try {
			dataServer.close();
		} catch (IOException e) {
			ServerLogger.error(
					Server.class,
					"Error shutting down data server for device ["
							+ device.getSerialNumber() + "]", e);
		}

	}
}
