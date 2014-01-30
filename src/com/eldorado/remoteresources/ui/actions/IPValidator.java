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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checks if the IP address is valid
 * 
 * @author Luiz Rafael de Souza
 * 
 */
public class IPValidator {

	private static final String IPV4_REGEX = "\\A(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z";
	private static final String IPV6_HEX4DECCOMPRESSED_REGEX = "\\A((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?) ::((?:[0-9A-Fa-f]{1,4}:)*)(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z";
	private static final String IPV6_6HEX4DEC_REGEX = "\\A((?:[0-9A-Fa-f]{1,4}:){6,6})(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z";
	private static final String IPV6_HEXCOMPRESSED_REGEX = "\\A((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)\\z";
	private static final String IPV6_REGEX = "\\A(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}\\z";
	private static final String IP_FORMAT_REGEX = "\\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b";

	public static boolean isIPV6(String ip) {

		Pattern ipv6_hex4 = Pattern.compile(IPV6_HEX4DECCOMPRESSED_REGEX);
		Pattern ipv6_6hex4 = Pattern.compile(IPV6_6HEX4DEC_REGEX);
		Pattern ipv6_hexcom = Pattern.compile(IPV6_HEXCOMPRESSED_REGEX);
		Pattern ipv6 = Pattern.compile(IPV6_REGEX);

		Matcher matcher_ipv6_hex4 = ipv6_hex4.matcher(ip);
		Matcher matcher_ipv6_6hex4 = ipv6_6hex4.matcher(ip);
		Matcher matcher_ipv6_hexcom = ipv6_hexcom.matcher(ip);
		Matcher matcher_ipv6 = ipv6.matcher(ip);

		return (matcher_ipv6_hex4.find() || matcher_ipv6_6hex4.find()
				|| matcher_ipv6_hexcom.find() || matcher_ipv6.find());

	}

	public static boolean isIPV4(String ip) {

		Pattern ipv4 = Pattern.compile(IPV4_REGEX);
		Matcher matcher_ipv4 = ipv4.matcher(ip);
		return matcher_ipv4.find();

	}

	public static boolean isIPFormat(String ip) {

		Pattern ip_format = Pattern.compile(IP_FORMAT_REGEX);

		Matcher matcher_ip_format = ip_format.matcher(ip);
		return matcher_ip_format.find();
	}
}
