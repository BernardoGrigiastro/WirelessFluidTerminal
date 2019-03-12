/*
 * This file is part of Wireless Fluid Terminal. Copyright (c) 2017, p455w0rd
 * (aka TheRealp455w0rd), All rights reserved unless otherwise stated.
 *
 * Wireless Fluid Terminal is free software: you can redistribute it and/or
 * modify it under the terms of the MIT License.
 *
 * Wireless Fluid Terminal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the MIT License for
 * more details.
 *
 * You should have received a copy of the MIT License along with Wireless
 * Fluid Terminal. If not, see <https://opensource.org/licenses/MIT>.
 */
package p455w0rd.wft.init;

import p455w0rd.ae2wtlib.api.WTApi;

public class ModGlobals {

	public static final String MODID = "wft";
	public static final String VERSION = "1.0.0";
	public static final String NAME = "Wireless Fluid Terminal";
	public static final String SERVER_PROXY = "p455w0rd.wft.proxy.CommonProxy";
	public static final String CLIENT_PROXY = "p455w0rd.wft.proxy.ClientProxy";
	public static final String DEP_LIST = "required-after:ae2wtlib@[1.0.7,);required-after:appliedenergistics2@[rv6-stable-6,);required-after:p455w0rdslib@[2.0.36,);after:baubles;after:mousetweaks;after:itemscroller";
	public static final String CONFIG_FILE = WTApi.instance().getConfig().getConfigFile();

}
