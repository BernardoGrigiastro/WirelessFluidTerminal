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

	public static void setIsBauble(boolean value) {
		isBauble = value;
	}

	public static int getSlot() {
		return slot;
	}

	public static void setSlot(int value) {
		slot = value;
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == GUI_WFT) {
			ITerminalHost fluidTerminal = getFluidTerminal(player, world, new BlockPos(x, y, z), isBauble(), getSlot());
			if (fluidTerminal != null) {
				return new ContainerWFT(player, fluidTerminal, getSlot(), isBauble());
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == GUI_WFT) {
			ITerminalHost fluidTerminal = getFluidTerminal(player, world, new BlockPos(x, y, z), isBauble(), getSlot());
			if (fluidTerminal != null) {
				return new GuiWFT(new ContainerWFT(player, fluidTerminal, getSlot(), isBauble()));
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private ITerminalHost getFluidTerminal(EntityPlayer player, World world, BlockPos pos, boolean isBauble, int slot) {
		ItemStack wirelessTerminal = ItemStack.EMPTY;
		if (slot >= 0) {
			wirelessTerminal = isBauble ? WTApi.instance().getBaublesUtility().getWTBySlot(player, slot, IWirelessFluidTerminalItem.class) : WTApi.instance().getWTBySlot(player, slot);
		}
		else {
			Pair<Boolean, Pair<Integer, ItemStack>> firstTerm = WFTUtils.getFirstWirelessFluidTerminal(player.inventory);
			wirelessTerminal = firstTerm.getRight().getRight();
			setSlot(firstTerm.getRight().getLeft());
			setIsBauble(firstTerm.getLeft());
		}
		final ICustomWirelessTermHandler wh = (ICustomWirelessTermHandler) AEApi.instance().registries().wireless().getWirelessTerminalHandler(WFTUtils.getFluidTerm(player.inventory));
		final WTGuiObject<IAEFluidStack> terminal = wh == null ? null : (WTGuiObject<IAEFluidStack>) WTApi.instance().getGUIObject(wh, wirelessTerminal, player);
		return terminal;
	}

	public static void open(int ID, EntityPlayer player, World world, BlockPos pos, boolean isBauble, int slot) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		setIsBauble(isBauble);
		setSlot(slot);
		player.openGui(WFT.INSTANCE, ID, world, x, y, z);
	}

}
