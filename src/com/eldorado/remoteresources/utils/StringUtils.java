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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZInputStream;
import org.tukaani.xz.XZOutputStream;

/**
 * encrypts / decrypts strings before to transfer information
 * 
 * @author Luiz Rafael de Souza
 * 
 */
public class StringUtils {
	/**
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str == null) {
			return true;
		}

		return str.isEmpty();
	}

	public static byte[] compress(String log) throws IOException {
		ByteArrayOutputStream xzOutput = new ByteArrayOutputStream();
		XZOutputStream xzStream = new XZOutputStream(xzOutput,
				new LZMA2Options(LZMA2Options.PRESET_MAX));
		xzStream.write(log.getBytes());
		xzStream.close();
		return xzOutput.toByteArray();
	}

	public static String decompress(byte[] log) throws IOException {
		XZInputStream xzInputStream = new XZInputStream(
				new ByteArrayInputStream(log));
		byte firstByte = (byte) xzInputStream.read();
		byte[] buffer = new byte[xzInputStream.available()];
		buffer[0] = firstByte;
		xzInputStream.read(buffer, 1, buffer.length - 2);
		xzInputStream.close();
		return new String(buffer);
	}

}
