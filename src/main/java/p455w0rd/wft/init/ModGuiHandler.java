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

import org.apache.commons.lang3.tuple.Pair;

import appeng.api.AEApi;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.data.IAEFluidStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import p455w0rd.ae2wtlib.api.*;
import p455w0rd.wft.WFT;
import p455w0rd.wft.api.IWirelessFluidTerminalItem;
import p455w0rd.wft.client.gui.GuiWFT;
import p455w0rd.wft.container.ContainerWFT;
import p455w0rd.wft.util.WFTUtils;

/**
 * @author p455w0rd
 *
 */
public class ModGuiHandler implements IGuiHandler {

	public static final int GUI_WFT = 0;
	private static int slot = -1;
	private static boolean isBauble = false;

	public static boolean isBauble() {
		return isBauble;
	}

	public static void setIsBauble(final boolean value) {
		isBauble = value;
	}

	public static int getSlot() {
		return slot;
	}

	public static void setSlot(final int value) {
		slot = value;
	}

	@Override
	public Object getServerGuiElement(final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		if (ID == GUI_WFT) {
			final ITerminalHost fluidTerminal = getFluidTerminal(player, world, new BlockPos(x, y, z), isBauble(), getSlot());
			if (fluidTerminal != null) {
				return new ContainerWFT(player, fluidTerminal, getSlot(), isBauble());
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		if (ID == GUI_WFT) {
			final ITerminalHost fluidTerminal = getFluidTerminal(player, world, new BlockPos(x, y, z), isBauble(), getSlot());
			if (fluidTerminal != null) {
				return new GuiWFT(new ContainerWFT(player, fluidTerminal, getSlot(), isBauble()));
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private ITerminalHost getFluidTerminal(final EntityPlayer player, final World world, final BlockPos pos, final boolean isBauble, final int slot) {
		ItemStack wirelessTerminal = ItemStack.EMPTY;
		if (slot >= 0) {
			wirelessTerminal = isBauble ? WTApi.instance().getBaublesUtility().getWTBySlot(player, slot, IWirelessFluidTerminalItem.class) : WTApi.instance().getWTBySlot(player, slot);
		}
		else {
			final Pair<Boolean, Pair<Integer, ItemStack>> firstTerm = WFTUtils.getFirstWirelessFluidTerminal(player.inventory);
			wirelessTerminal = firstTerm.getRight().getRight();
			setSlot(firstTerm.getRight().getLeft());
			setIsBauble(firstTerm.getLeft());
		}
		final ICustomWirelessTerminalItem wh = (ICustomWirelessTerminalItem) AEApi.instance().registries().wireless().getWirelessTerminalHandler(wirelessTerminal);
		final WTGuiObject<IAEFluidStack> terminal = wh == null ? null : (WTGuiObject<IAEFluidStack>) WTApi.instance().getGUIObject(wh, wirelessTerminal, player);
		return terminal;
	}

	public static void open(final int ID, final EntityPlayer player, final World world, final BlockPos pos, final boolean isBauble, final int slot) {
		final int x = pos.getX();
		final int y = pos.getY();
		final int z = pos.getZ();
		setIsBauble(isBauble);
		setSlot(slot);
		player.openGui(WFT.INSTANCE, ID, world, x, y, z);
	}

}
