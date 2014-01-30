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

package com.eldorado.remoteresources.ui.model;

/**
 * This class is the listener for device model modifications. The device model
 * is responsible to manages devices for UI. It is not related to real
 * connection.
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public interface DeviceModelChangeListener {

	/**
	 * A new device has been added to the model
	 * 
	 * @param device
	 *            the device added
	 */
	public void deviceAdded(Device device);

	/**
	 * A device has been removed from the model
	 * 
	 * @param device
	 *            the device removed
	 */
	public void deviceRemoved(Device device);

	/**
	 * A new host was added. Notice that host additions does NOT triggers device
	 * added
	 * 
	 * @param host
	 *            the host
	 */
	public void hostAdded(Host host);

	/**
	 * A host has been removed. Notice that host removal does NOT triggers
	 * device removed
	 * 
	 * @param host
	 *            the host
	 */
	public void hostRemoved(Host host);

	public void deviceChangedName(Device device, String oldName);

}
