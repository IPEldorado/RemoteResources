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

import java.util.Vector;

import com.eldorado.remoteresources.android.common.Keys;
import com.eldorado.remoteresources.android.common.connection.messages.control.DeviceCommandMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.KeyCommandMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.KeyCommandMessageType;
import com.eldorado.remoteresources.android.common.connection.messages.control.ScriptCommandMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.ScriptCommandMessageType;
import com.eldorado.remoteresources.android.common.connection.messages.control.TouchCommandMessage;
import com.eldorado.remoteresources.android.common.connection.messages.control.TouchCommandMessageType;

/**
 * Generate / Read a script file with the actions performed on RR
 * 
 * @author Luiz Rafael de Souza
 * 
 */
public class Script {

	private static boolean generationScriptActive = false;

	private final static String TAG_TOUCH = "[TOUCH]";

	private final static String TYPE_OF_TOUCH = "[TYPE_OF_TOUCH]";

	private final static String COORDINATE_X = "[COR_X]";

	private final static String COORDINATE_Y = "[COR_Y]";

	private final static String TAG_KEYCOMMAND = "[KEYCOMMAND]";

	private final static String TYPE_KEY_PRESSED = "[TYPE_KEY_PRESSED]";

	private final static String TYPE_KEY_ORIENTATION = "[ORIENTATION]";

	private final static String PARAM = "[PARAM]";

	public final static String TAG_SLEEP = "[SLEEP]";

	private final static String TIME_TO_SLEEP = "[TTS]";

	public final static String TAG_PAUSE = "[PAUSE]";

	private final static StringBuilder key_model_builder = new StringBuilder();

	private final static StringBuilder touch_model_builder = new StringBuilder();

	private final static StringBuilder sleep_model_builder = new StringBuilder();

	private static String touch_model = null;

	private static String sleep_model = null;

	private static String pause_model = TAG_PAUSE;

	private static String key_model = null;

	private final static Vector<String> actions = new Vector<String>();

	private static Vector<DeviceCommandMessage> messages = new Vector<DeviceCommandMessage>();

	static {

		key_model_builder.append(TAG_KEYCOMMAND);
		key_model_builder.append(":");
		key_model_builder.append(TYPE_KEY_PRESSED);
		key_model_builder.append("=");
		key_model_builder.append(TYPE_KEY_ORIENTATION);
		key_model_builder.append("&");
		key_model_builder.append(PARAM);
		key_model = new String(key_model_builder.toString());

		touch_model_builder.append(TAG_TOUCH);
		touch_model_builder.append(":");
		touch_model_builder.append(TYPE_OF_TOUCH);
		touch_model_builder.append("-");
		touch_model_builder.append(COORDINATE_X);
		touch_model_builder.append("*");
		touch_model_builder.append(COORDINATE_Y);
		touch_model = new String(touch_model_builder.toString());

		sleep_model_builder.append(TAG_SLEEP);
		sleep_model_builder.append(":");
		sleep_model_builder.append(TIME_TO_SLEEP);
		sleep_model = new String(sleep_model_builder);

	}

	public static void registerActionTouch(TouchCommandMessageType touch,
			String cX, String cY) {

		actions.add(touch_model.replace(TYPE_OF_TOUCH, touch.toString())
				.replace(COORDINATE_X, cX).replace(COORDINATE_Y, cY));
	}

	public static void registerActionKey(String name, String param,
			KeyCommandMessageType orin) {
		actions.add(key_model.replace(TYPE_KEY_PRESSED, name)
				.replace(PARAM, param)
				.replace(TYPE_KEY_ORIENTATION, orin.name().toString()));
	}

	public static void clean() {
		messages.clear();
		actions.clear();
	}

	public static Vector<String> getActions() {
		return actions;
	}

	public static boolean getScriptGeneration() {
		return generationScriptActive;
	}

	public static void setScriptGeneration(boolean b) {
		generationScriptActive = b;
	}

	public static void loadScript(Vector<String> actions) {

		int sequenceNumber = 0;
		String serialNumber = "";

		for (String action : actions) {
			String[] a = action.split(":");

			// TODO: improve me!!! please! and what do we do with the PARAM?

			if (TAG_PAUSE.equals(a[0])) {
				ScriptCommandMessage scriptPauseMessage = new ScriptCommandMessage(
						sequenceNumber, serialNumber, null,
						ScriptCommandMessageType.PAUSE);
				messages.add(scriptPauseMessage);
			} else if (TAG_KEYCOMMAND.equals(a[0])) {

				String type = a[1].substring(0, a[1].indexOf("="));
				String orientation = a[1].substring(a[1].indexOf("=") + 1,
						a[1].indexOf("&"));
				String param = a[1].substring(a[1].indexOf("&") + 1,
						a[1].length());

				Keys k;
				if ("BACK".equals(type)) {
					k = Keys.BACK;
				} else if ("CAMERA".toString().equals(type)) {
					k = Keys.CAMERA;
				} else if ("CLEAR".toString().equals(type)) {
					k = Keys.CLEAR;
				} else if ("DEL".toString().equals(type)) {
					k = Keys.DEL;
				} else if ("FOCUS".toString().equals(type)) {
					k = Keys.FOCUS;
				} else if ("HOME".toString().equals(type)) {
					k = Keys.HOME;
				} else if ("MENU".toString().equals(type)) {
					k = Keys.MENU;
				} else if ("POWER".toString().equals(type)) {
					k = Keys.POWER;
				} else if ("SEARCH".toString().equals(type)) {
					k = Keys.SEARCH;
				} else if ("VOLUME_DOWN".toString().equals(type)) {
					k = Keys.VOLUME_DOWN;
				} else {
					k = Keys.VOLUME_UP;
				}

				KeyCommandMessageType kt;
				if (KeyCommandMessageType.KEY_UP.toString().equals(orientation)) {
					kt = KeyCommandMessageType.KEY_UP;
				} else {
					kt = KeyCommandMessageType.KEY_DOWN;
				}

				KeyCommandMessage keyMessage = new KeyCommandMessage(
						sequenceNumber, serialNumber, k, kt);

				messages.add(keyMessage);
			} else if (TAG_TOUCH.equals(a[0])) {

				String type = a[1].substring(0, a[1].indexOf("-"));
				String cor_X = a[1].substring(a[1].indexOf("-") + 1,
						a[1].indexOf("*"));
				String cor_Y = a[1].substring(a[1].indexOf("*") + 1,
						a[1].length());

				TouchCommandMessageType tt;
				if (TouchCommandMessageType.TOUCH_DOWN.toString().equals(type)) {
					tt = TouchCommandMessageType.TOUCH_DOWN;
				} else if (TouchCommandMessageType.TOUCH_UP.toString().equals(
						type)) {
					tt = TouchCommandMessageType.TOUCH_UP;
				} else {
					tt = TouchCommandMessageType.TOUCH_MOVE;
				}

				TouchCommandMessage touchMessage = new TouchCommandMessage(
						sequenceNumber, serialNumber, tt,
						Float.parseFloat(cor_X), Float.parseFloat(cor_Y));

				messages.add(touchMessage);

			} else if (TAG_SLEEP.equals(a[0])) {
				ScriptCommandMessage scriptSleepMessage = new ScriptCommandMessage(
						sequenceNumber, serialNumber, a[1],
						ScriptCommandMessageType.SLEEP);
				messages.add(scriptSleepMessage);
			} else {
				serialNumber = a[0];
			}

			sequenceNumber++;
		}

	}

	public static Vector<DeviceCommandMessage> getMessages() {
		return messages;
	}
}
