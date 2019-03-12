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

import net.minecraftforge.fml.common.Loader;
import p455w0rd.ae2wtlib.api.WTApi;
import p455w0rd.wft.integration.ItemScroller;

/**
 * @author p455w0rd
 *
 */
public class ModIntegration {

	public static void preInit() {
		WTApi.instance().getRegistry().registerWirelessTerminal(ModItems.WFT);
		WTApi.instance().getRegistry().registerWirelessTerminal(ModItems.CREATIVE_WFT);
	}

	public static void postInit() {
		if (Mods.ITEMSCROLLER.isLoaded()) {
			ItemScroller.blackListSlots();
		}
	}

	public static enum Mods {
			ITEMSCROLLER("itemscroller", "Item Scroller");

		private String modid, name;

		Mods(String modidIn, String nameIn) {
			modid = modidIn;
			name = nameIn;
		}

		public String getId() {
			return modid;
		}

		public String getName() {
			return name;
		}

		public boolean isLoaded() {
			return Loader.isModLoaded(getId());
		}
	}

}
