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
package p455w0rd.wft.util;

import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.ae2wtlib.api.WTApi;
import p455w0rd.ae2wtlib.api.WTApi.Integration.Mods;
import p455w0rd.wft.api.IWirelessFluidTerminalItem;
import p455w0rd.wft.api.WFTApi;
import p455w0rd.wft.container.ContainerWFT;
import p455w0rd.wft.init.ModItems;
import p455w0rd.wft.init.ModKeybindings;

public class WFTUtils {

	public static final String INFINITY_ENERGY_NBT = "InfinityEnergy";
	public static final String BOOSTER_SLOT_NBT = "BoosterSlot";
	public static final String IN_RANGE_NBT = "IsInRange";
	public static final String AUTOCONSUME_BOOSTER_NBT = "AutoConsumeBoosters";

	public static NonNullList<ItemStack> getFluidTerminals(EntityPlayer player) {
		NonNullList<ItemStack> terminalList = NonNullList.<ItemStack>create();
		InventoryPlayer playerInventory = player.inventory;
		for (ItemStack fluidTerm : playerInventory.mainInventory) {
			if (isAnyWFT(fluidTerm)) {
				terminalList.add(fluidTerm);
			}
		}
		if (Mods.BAUBLES.isLoaded()) {
			Set<Pair<Integer, ItemStack>> pairSet = WTApi.instance().getBaublesUtility().getAllWTBaublesByType(player, IWirelessFluidTerminalItem.class);
			for (Pair<Integer, ItemStack> pair : pairSet) {
				terminalList.add(pair.getRight());
			}
		}
		return terminalList;
	}

	@Nonnull
	public static ItemStack getFluidTerm(InventoryPlayer playerInv) {
		if (!playerInv.player.getHeldItemMainhand().isEmpty() && playerInv.player.getHeldItemMainhand().getItem() instanceof IWirelessFluidTerminalItem) {
			return playerInv.player.getHeldItemMainhand();
		}
		ItemStack fluidTerm = ItemStack.EMPTY;
		if (Mods.BAUBLES.isLoaded()) {
			fluidTerm = WTApi.instance().getBaublesUtility().getAllWTBaublesByType(playerInv.player, IWirelessFluidTerminalItem.class).stream().findFirst().get().getRight();
		}
		if (fluidTerm.isEmpty()) {
			int invSize = playerInv.getSizeInventory();
			if (invSize <= 0) {
				return ItemStack.EMPTY;
			}
			for (int i = 0; i < invSize; ++i) {
				ItemStack item = playerInv.getStackInSlot(i);
				if (item.isEmpty()) {
					continue;
				}
				if (item.getItem() instanceof IWirelessFluidTerminalItem) {
					fluidTerm = item;
					break;
				}
			}
		}
		return fluidTerm;
	}

	/**
	 * gets the first available Wireless Crafting Terminal
	 * the Integer of the Pair tells the slotNumber
	 * the boolean tells whether or not the Integer is a Baubles slot
	 */
	@Nonnull
	public static Pair<Boolean, Pair<Integer, ItemStack>> getFirstWirelessFluidTerminal(InventoryPlayer playerInv) {
		boolean isBauble = false;
		int slotID = -1;
		ItemStack wirelessTerm = ItemStack.EMPTY;
		if (!playerInv.player.getHeldItemMainhand().isEmpty() && playerInv.player.getHeldItemMainhand().getItem() instanceof IWirelessFluidTerminalItem) {
			slotID = playerInv.currentItem;
			wirelessTerm = playerInv.player.getHeldItemMainhand();
		}
		else {
			if (WTApi.Integration.Mods.BAUBLES.isLoaded()) {
				wirelessTerm = WTApi.instance().getBaublesUtility().getFirstWTBaubleByType(playerInv.player, IWirelessFluidTerminalItem.class).getRight();
				slotID = WTApi.instance().getBaublesUtility().getFirstWTBauble(playerInv.player).getLeft();
				if (!wirelessTerm.isEmpty()) {
					isBauble = true;
				}
			}
			if (wirelessTerm.isEmpty()) {
				int invSize = playerInv.getSizeInventory();
				if (invSize > 0) {
					for (int i = 0; i < invSize; ++i) {
						ItemStack item = playerInv.getStackInSlot(i);
						if (item.isEmpty()) {
							continue;
						}
						if (item.getItem() instanceof IWirelessFluidTerminalItem) {
							wirelessTerm = item;
							slotID = i;
							break;
						}
					}
				}
			}
		}
		return Pair.of(isBauble, Pair.of(slotID, wirelessTerm));
	}

	public static boolean isAnyWFT(@Nonnull ItemStack wirelessTerm) {
		return wirelessTerm.getItem() == ModItems.WFT || wirelessTerm.getItem() == ModItems.CREATIVE_WFT || wirelessTerm.getItem() instanceof IWirelessFluidTerminalItem;
	}

	public static boolean isWFTCreative(ItemStack fluidTerm) {
		return !fluidTerm.isEmpty() && fluidTerm.getItem() == ModItems.CREATIVE_WFT;
	}

	@SideOnly(Side.CLIENT)
	public static String color(String color) {
		switch (color) {
		case "white":
			return TextFormatting.WHITE.toString();
		case "black":
			return TextFormatting.BLACK.toString();
		case "green":
			return TextFormatting.GREEN.toString();
		case "red":
			return TextFormatting.RED.toString();
		case "yellow":
			return TextFormatting.YELLOW.toString();
		case "aqua":
			return TextFormatting.AQUA.toString();
		case "blue":
			return TextFormatting.BLUE.toString();
		case "italics":
			return TextFormatting.ITALIC.toString();
		case "bold":
			return TextFormatting.BOLD.toString();
		default:
		case "gray":
			return TextFormatting.GRAY.toString();
		}
	}

	@SideOnly(Side.CLIENT)
	public static EntityPlayer player() {
		return Minecraft.getMinecraft().player;
	}

	public static EntityPlayer player(InventoryPlayer playerInv) {
		return playerInv.player;
	}

	@SideOnly(Side.CLIENT)
	public static World world() {
		return Minecraft.getMinecraft().world;
	}

	public static World world(EntityPlayer player) {
		return player.getEntityWorld();
	}

	public static void chatMessage(EntityPlayer player, ITextComponent message) {
		player.sendMessage(message);
	}

	@SideOnly(Side.CLIENT)
	public static void handleKeybind() {
		EntityPlayer p = WFTUtils.player();
		if (p.openContainer == null) {
			return;
		}
		if (ModKeybindings.openFluidTerminal.getKeyCode() != Keyboard.CHAR_NONE && ModKeybindings.openFluidTerminal.isPressed()) {
			ItemStack is = WFTUtils.getFluidTerm(p.inventory);
			if (is.isEmpty()) {
				return;
			}
			IWirelessFluidTerminalItem fluidTerm = (IWirelessFluidTerminalItem) is.getItem();
			if (fluidTerm != null) {
				if (!(p.openContainer instanceof ContainerWFT)) {
					Pair<Boolean, Pair<Integer, ItemStack>> wftPair = WFTUtils.getFirstWirelessFluidTerminal(p.inventory);
					WFTApi.instance().openWFTGui(p, wftPair.getLeft(), wftPair.getRight().getLeft());
					//ModNetworking.instance().sendToServer(new PacketOpenGui(ModGuiHandler.GUI_WFT));
				}
				else {
					p.closeScreen();
				}
			}
		}
	}

}
