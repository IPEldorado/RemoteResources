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

package com.eldorado.remoteresources.android.common.connection.messages.data;

/**
 * New Frame Message This message describes a new frame data message. When the
 * frame changes, the frame content is also sent within this message
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
public class NewFrameMessage extends DataMessage {

	private static final long serialVersionUID = 8725294156550039300L;

	private final byte[] frameContent;

	/**
	 * Create a new frame message
	 * 
	 * @param sequenceNumber
	 *            the sequence number
	 * @param frameContent
	 *            the frame content
	 */
	public NewFrameMessage(int sequenceNumber, byte[] frameContent) {
		super(sequenceNumber, DataMessageType.NEW_FRAME);
		this.frameContent = frameContent;
	}

	/**
	 * Get the changed frame content (or null if the frame has not changed) By
	 * changed assume this frame has differences regarding the latest one sent
	 * 
	 * @return the frame content
	 */
	public byte[] getFrameContent() {
		return frameContent;
	}

	/**
	 * Check if this frame has changed
	 * 
	 * @return true if this frame is a new one, false otherwise
	 */
	public boolean hasChanged() {
		return frameContent != null;
	}

}
