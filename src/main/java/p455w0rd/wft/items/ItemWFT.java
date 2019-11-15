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
package p455w0rd.wft.items;

import appeng.api.config.*;
import appeng.api.util.IConfigManager;
import appeng.util.Platform;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import p455w0rd.ae2wtlib.api.client.IBaubleItem;
import p455w0rd.ae2wtlib.api.item.ItemWT;
import p455w0rd.wft.api.IWirelessFluidTerminalItem;
import p455w0rd.wft.api.WFTApi;
import p455w0rd.wft.init.ModGlobals;

/**
 * @author p455w0rd
 *
 */
public class ItemWFT extends ItemWT implements IWirelessFluidTerminalItem, IBaubleItem {

	public ItemWFT() {
		this(new ResourceLocation(ModGlobals.MODID, "wft"));
	}

	public ItemWFT(final ResourceLocation registryName) {
		super(registryName);
	}

	@Override
	public IConfigManager getConfigManager(final ItemStack target) {
		final IConfigManager out = super.getConfigManager(target);
		out.registerSetting(Settings.SORT_BY, SortOrder.NAME);
		out.registerSetting(Settings.VIEW_MODE, ViewItems.ALL);
		out.registerSetting(Settings.SORT_DIRECTION, SortDir.ASCENDING);
		out.readFromNBT(Platform.openNbtData(target).copy());
		return out;
	}

	@Override
	public void openGui(final EntityPlayer player, final boolean isBauble, final int playerSlot) {
		WFTApi.instance().openWFTGui(player, isBauble, playerSlot);
	}

	@Override
	public ResourceLocation getMenuIcon() {
		return new ResourceLocation(ModGlobals.MODID, "textures/items/wft.png");
	}

	@Override
	public int getColor() {
		return 0xFF54AFFF;
	}

}
