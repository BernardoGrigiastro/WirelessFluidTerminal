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

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import p455w0rd.ae2wtlib.api.WTApi;

/**
 * @author p455w0rd
 *
 */
public class ModKeybindings {

	public static KeyBinding openFluidTerminal;

	public static void preInit() {
		openFluidTerminal = new KeyBinding("key.open_wft.desc", Keyboard.CHAR_NONE, WTApi.instance().getConstants().getItemGroup());
		ClientRegistry.registerKeyBinding(openFluidTerminal);
	}

}
