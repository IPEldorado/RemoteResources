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

package com.eldorado.remoteresources.ui;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.eldorado.remoteresources.android.client.AndroidDeviceClient;
import com.eldorado.remoteresources.android.client.connection.Client;
import com.eldorado.remoteresources.android.common.connection.messages.control.TouchCommandMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.TouchCommandMessageType;
import com.eldorado.remoteresources.ui.model.Script;

public class EventsListener extends MouseAdapter {

	private final AndroidDeviceClient device;

	private final Client client;

	public EventsListener(Client client, String device) {
		this.device = client.getDeviceList().get(device);
		this.client = client;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		processEvent(e, TouchCommandMessageType.TOUCH_MOVE);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		processEvent(e, TouchCommandMessageType.TOUCH_DOWN);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		processEvent(e, TouchCommandMessageType.TOUCH_UP);
	}

	private void processEvent(MouseEvent e, TouchCommandMessageType down) {
		Dimension componentDimension = e.getComponent().getSize();

		float xRate = (float) e.getX() / (float) componentDimension.width;
		float yRate = (float) e.getY() / (float) componentDimension.height;

		switch (down) {
		case TOUCH_DOWN:
			client.sendMessage(new TouchCommandMessage(client
					.getNextSequenceNumber(), device.getSerialNumber(),
					TouchCommandMessageType.TOUCH_DOWN, xRate, yRate));
			Script.registerActionTouch(TouchCommandMessageType.TOUCH_DOWN,
					String.valueOf(xRate), String.valueOf(yRate));
			break;
		case TOUCH_MOVE:
			client.sendMessage(new TouchCommandMessage(client
					.getNextSequenceNumber(), device.getSerialNumber(),
					TouchCommandMessageType.TOUCH_MOVE, xRate, yRate));
			Script.registerActionTouch(TouchCommandMessageType.TOUCH_MOVE,
					String.valueOf(xRate), String.valueOf(yRate));
			break;
		case TOUCH_UP:
			client.sendMessage(new TouchCommandMessage(client
					.getNextSequenceNumber(), device.getSerialNumber(),
					TouchCommandMessageType.TOUCH_UP, xRate, yRate));
			Script.registerActionTouch(TouchCommandMessageType.TOUCH_UP,
					String.valueOf(xRate), String.valueOf(yRate));
			break;
		}
	}
}
