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
package p455w0rd.wft.integration;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import mezz.jei.api.*;
import net.minecraft.item.ItemStack;
import p455w0rd.ae2wtlib.api.WTApi;
import p455w0rd.ae2wtlib.api.WTApi.Integration.Mods;
import p455w0rd.wft.init.ModItems;

/**
 * @author p455w0rd
 *
 */
@JEIPlugin
public class JEI implements IModPlugin {

	@SuppressWarnings("deprecation")
	@Override
	public void register(@Nonnull IModRegistry registry) {
		String wftBaublesDescKey = Mods.BAUBLES.isLoaded() ? WTApi.instance().getConstants().getTooltips().jeiCanBeWorn() : "";
		registry.addIngredientInfo(Lists.newArrayList(new ItemStack(ModItems.WFT)), ItemStack.class, "jei.wft.desc", wftBaublesDescKey);
	}

}
