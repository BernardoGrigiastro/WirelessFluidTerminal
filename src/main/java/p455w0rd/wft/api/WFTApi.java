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
package p455w0rd.wft.api;

import java.lang.reflect.Method;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;

public abstract class WFTApi {

	protected static WFTApi api = null;

	@Nullable
	public static WFTApi instance() {
		if (WFTApi.api == null) {
			try {
				final Class<?> clazz = Class.forName("p455w0rd.wft.init.ModAPIImpl");
				final Method instanceAccessor = clazz.getMethod("instance");
				WFTApi.api = (WFTApi) instanceAccessor.invoke(null);
			}
			catch (final Throwable e) {
				return null;
			}
		}

		return WFTApi.api;
	}

	public abstract void openWFTGui(EntityPlayer player, boolean isBauble, int wftSlot);

}
