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

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.ae2wtlib.api.WTApi;
import p455w0rd.wft.api.IWirelessFluidTerminalItem;
import p455w0rd.wft.util.WFTUtils;

/**
 * @author p455w0rd
 *
 */
@EventBusSubscriber(modid = ModGlobals.MODID)
public class ModEvents {

	@SubscribeEvent
	public static void onItemRegistryReady(RegistryEvent.Register<Item> event) {
		ModItems.register(event.getRegistry());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onKeyInput(KeyInputEvent e) {
		WFTUtils.handleKeybind();
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onMouseEvent(MouseEvent event) {
		WFTUtils.handleKeybind();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onModelBake(ModelBakeEvent event) {
		ModItems.initModels(event);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onModelRegister(ModelRegistryEvent event) {
		ModItems.registerTEISRs(event);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onPickup(EntityItemPickupEvent e) {
		if (e.getEntityPlayer() != null && e.getEntityPlayer() instanceof EntityPlayerMP) {
			if (!WTApi.instance().getConfig().isOldInfinityMechanicEnabled() && e.getItem().getItem().getItem() == WTApi.instance().getBoosterCard()) {
				if (WTApi.Integration.Mods.BAUBLES.isLoaded()) {
					for (Pair<Integer, ItemStack> termPair : WTApi.instance().getBaublesUtility().getAllWTBaubles(e.getEntityPlayer())) {
						ItemStack wirelessTerminal = termPair.getRight();
						if (!wirelessTerminal.isEmpty() && WTApi.instance().shouldConsumeBoosters(wirelessTerminal)) {
							e.setCanceled(true);
							ItemStack boosters = e.getItem().getItem().copy();
							WTApi.instance().addInfinityBoosters(wirelessTerminal, boosters);
							WTApi.instance().getNetHandler().sendTo(WTApi.instance().getNetHandler().createInfinityEnergySyncPacket(WTApi.instance().getInfinityEnergy(wirelessTerminal), e.getEntityPlayer().getUniqueID(), true, termPair.getLeft()), (EntityPlayerMP) e.getEntityPlayer());
							e.getItem().setDead();
							return;
						}
					}
				}
				for (Pair<Boolean, Pair<Integer, ItemStack>> termPair : WTApi.instance().getAllWirelessTerminalsByType(e.getEntityPlayer(), IWirelessFluidTerminalItem.class)) {
					ItemStack wirelessTerminal = termPair.getRight().getRight();
					boolean shouldConsume = WTApi.instance().shouldConsumeBoosters(wirelessTerminal);
					if (!wirelessTerminal.isEmpty() && shouldConsume) {
						e.setCanceled(true);
						ItemStack boosters = e.getItem().getItem().copy();
						WTApi.instance().addInfinityBoosters(wirelessTerminal, boosters);
						WTApi.instance().getNetHandler().sendTo(WTApi.instance().getNetHandler().createInfinityEnergySyncPacket(WTApi.instance().getInfinityEnergy(wirelessTerminal), e.getEntityPlayer().getUniqueID(), true, termPair.getRight().getLeft()), (EntityPlayerMP) e.getEntityPlayer());
						e.getItem().setDead();
						return;
					}
				}
			}
		}
	}
}
