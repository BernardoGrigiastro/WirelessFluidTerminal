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
package p455w0rd.wft.proxy;

import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.event.*;
import p455w0rd.wft.init.ModIntegration;
import p455w0rd.wft.init.ModNetworking;

/**
 * @author p455w0rd
 *
 */
public class CommonProxy {

	private static LoaderState WFT_STATE = LoaderState.NOINIT;

	public void preInit(FMLPreInitializationEvent e) {
		WFT_STATE = LoaderState.PREINITIALIZATION;
		ModNetworking.preInit();
		ModIntegration.preInit();
	}

	public void init(FMLInitializationEvent e) {
		WFT_STATE = LoaderState.INITIALIZATION;
	}

	public void postInit(FMLPostInitializationEvent e) {
		WFT_STATE = LoaderState.POSTINITIALIZATION;
		ModNetworking.postInit();
	}

	public LoaderState getLoaderState() {
		return WFT_STATE;
	}

}
