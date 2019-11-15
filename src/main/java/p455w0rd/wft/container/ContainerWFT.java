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
package p455w0rd.wft.container;

import java.io.IOException;
import java.nio.BufferOverflowException;

import appeng.api.AEApi;
import appeng.api.config.*;
import appeng.api.networking.*;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.storage.*;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.*;
import appeng.api.util.AEPartLocation;
import appeng.api.util.IConfigManager;
import appeng.core.localization.PlayerMessages;
import appeng.fluids.util.AEFluidStack;
import appeng.helpers.InventoryAction;
import appeng.me.helpers.ChannelPowerSrc;
import appeng.util.ConfigManager;
import appeng.util.Platform;
import appeng.util.inv.InvOperation;
import appeng.util.item.AEItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandler;
import p455w0rd.ae2wtlib.api.ICustomWirelessTerminalItem;
import p455w0rd.ae2wtlib.api.WTApi;
import p455w0rd.ae2wtlib.api.container.ContainerWT;
import p455w0rd.wft.api.IWirelessFluidTerminalItem;
import p455w0rd.wft.init.ModNetworking;
import p455w0rd.wft.sync.packets.*;

/**
 * @author p455w0rd
 *
 */
public class ContainerWFT extends ContainerWT implements IMEMonitorHandlerReceiver<IAEFluidStack> {

	private final IMEMonitor<IAEFluidStack> monitor;
	private final IItemList<IAEFluidStack> fluids = AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class).createList();
	private IGridNode networkNode;
	// Holds the fluid the client wishes to extract, or null for insert
	private IAEFluidStack clientRequestedTargetFluid = null;

	public ContainerWFT(final EntityPlayer player, final ITerminalHost hostIn, final int slot, final boolean isBauble) {
		//super(player.inventory, getActionHost(getGuiObject(WCTUtils.getFluidTerm(player.inventory), player, player.getEntityWorld(), (int) player.posX, (int) player.posY, (int) player.posZ)));
		super(player.inventory, getActionHost(getGuiObject(isBauble ? WTApi.instance().getBaublesUtility().getWTBySlot(player, slot, IWirelessFluidTerminalItem.class) : WTApi.instance().getWTBySlot(player, slot), player)), slot, isBauble, true, 152, 110);
		setCustomName("WFTContainer");
		setTerminalHost(hostIn);
		initConfig(setClientConfigManager(new ConfigManager(this)));

		if (Platform.isServer()) {
			setServerConfigManager(getGuiObject().getConfigManager());
			monitor = getGuiObject().getInventory(AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class));
			if (monitor != null) {
				monitor.addListener(this, null);
				if (getGuiObject() instanceof IEnergySource) {
					setPowerSource(getGuiObject());
				}
				else if (getGuiObject() instanceof IGridHost || getGuiObject() instanceof IActionHost) {
					if (getGuiObject() instanceof IGridHost) {
						networkNode = ((IGridHost) getGuiObject()).getGridNode(AEPartLocation.INTERNAL);
					}
					else if (getGuiObject() instanceof IActionHost) {
						networkNode = ((IActionHost) getGuiObject()).getActionableNode();
					}
					else {
						networkNode = null;
					}
					if (networkNode != null) {
						final IGrid g = networkNode.getGrid();
						if (g != null) {
							setPowerSource(new ChannelPowerSrc(networkNode, (IEnergySource) g.getCache(IEnergyGrid.class)));
						}
					}
				}
			}
			else {
				getPlayer().sendMessage(PlayerMessages.CommunicationError.get());
				setValidContainer(false);
			}
		}
		else {
			monitor = null;
		}

		for (int i = 0; i < getPlayerInv().getSizeInventory(); i++) {
			final ItemStack currStack = getPlayerInv().getStackInSlot(i);
			if (!currStack.isEmpty() && currStack == getWirelessTerminal()) {
				lockPlayerInventorySlot(i);
			}
		}
		bindPlayerInventory(player.inventory, 8, 222 - 90);
		readNBT();
	}

	@Override
	protected void initConfig(final IConfigManager cm) {
		cm.registerSetting(Settings.SORT_BY, SortOrder.NAME);
		cm.registerSetting(Settings.SORT_DIRECTION, SortDir.ASCENDING);
		cm.registerSetting(Settings.VIEW_MODE, ViewItems.ALL);
	}

	/*
	@SuppressWarnings("unchecked")
	public static WTGuiObject<IAEFluidStack> getGuiObject(final ItemStack it, final EntityPlayer player, final World w, final int x, final int y, final int z) {
		if (!it.isEmpty()) {
			final IWirelessTermHandler wh = AEApi.instance().registries().wireless().getWirelessTerminalHandler(it);
			if (wh instanceof ICustomWirelessTerminalItem) {
				return (WTGuiObject<IAEFluidStack>) WTApi.instance().getGUIObject((ICustomWirelessTerminalItem) wh, it, player);
			}
		}
		return null;
	}
	*/
	@Override
	public boolean isValid(final Object verificationToken) {
		return true;
	}

	@Override
	public void postChange(final IBaseMonitor<IAEFluidStack> monitor, final Iterable<IAEFluidStack> change, final IActionSource actionSource) {
		for (final IAEFluidStack is : change) {
			fluids.add(is);
		}
	}

	@Override
	public void onListUpdate() {
		for (final IContainerListener c : listeners) {
			queueInventory(c);
		}
	}

	@Override
	public void addListener(final IContainerListener listener) {
		super.addListener(listener);
		queueInventory(listener);
	}

	@Override
	public void onContainerClosed(final EntityPlayer player) {
		writeToNBT();
		super.onContainerClosed(player);
		if (monitor != null) {
			monitor.removeListener(this);
		}
	}

	@Override
	public ItemStack slotClick(final int slot, final int dragType, final ClickType clickTypeIn, final EntityPlayer player) {
		ItemStack returnStack = ItemStack.EMPTY;
		try {
			returnStack = super.slotClick(slot, dragType, clickTypeIn, player);
		}
		catch (final IndexOutOfBoundsException e) {
		}
		writeToNBT();
		detectAndSendChanges();
		return returnStack;
	}

	private void queueInventory(final IContainerListener c) {
		if (Platform.isServer() && c instanceof EntityPlayer && monitor != null) {
			try {
				PacketMEFluidInventoryUpdate piu = new PacketMEFluidInventoryUpdate();
				final IItemList<IAEFluidStack> monitorCache = monitor.getStorageList();

				for (final IAEFluidStack send : monitorCache) {
					try {
						piu.appendFluid(send);
					}
					catch (final BufferOverflowException boe) {
						ModNetworking.instance().sendTo(piu, (EntityPlayerMP) c);

						piu = new PacketMEFluidInventoryUpdate();
						piu.appendFluid(send);
					}
				}

				ModNetworking.instance().sendTo(piu, (EntityPlayerMP) c);
			}
			catch (final IOException e) {
			}
		}
	}

	public void setTargetStack(final IAEFluidStack stack) {
		if (Platform.isClient()) {
			if (stack == null && clientRequestedTargetFluid == null) {
				return;
			}
			if (stack != null && clientRequestedTargetFluid != null && stack.getFluidStack().isFluidEqual(clientRequestedTargetFluid.getFluidStack())) {
				return;
			}
			ModNetworking.instance().sendToServer(new PacketTargetFluidStack((AEFluidStack) stack));
		}

		clientRequestedTargetFluid = stack == null ? null : stack.copy();
	}

	@Override
	public void detectAndSendChanges() {
		if (Platform.isServer()) {

			if (getGuiObject() != null) {
				if (getWirelessTerminal() != getGuiObject().getItemStack()) {
					if (!getWirelessTerminal().isEmpty()) {
						if (ItemStack.areItemsEqual(getGuiObject().getItemStack(), getWirelessTerminal())) {
							getPlayerInv().setInventorySlotContents(getPlayerInv().currentItem, getGuiObject().getItemStack());
						}
						else {
							setValidContainer(false);
						}
					}
					else {
						setValidContainer(false);
					}
				}
			}
			else {
				setValidContainer(false);
			}

			if (monitor != getTerminalHost().getInventory(AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class))) {
				setValidContainer(false);
			}

			for (final Settings set : getServerConfigManager().getSettings()) {
				final Enum<?> sideLocal = getServerConfigManager().getSetting(set);
				final Enum<?> sideRemote = getClientConfigManager().getSetting(set);

				if (sideLocal != sideRemote) {
					getClientConfigManager().putSetting(set, sideLocal);
					for (final IContainerListener crafter : listeners) {
						if (crafter instanceof EntityPlayerMP) {
							try {
								ModNetworking.instance().sendTo(new PacketValueConfig(set.name(), sideLocal.name()), (EntityPlayerMP) crafter);
							}
							catch (final IOException e) {
							}
						}
					}
				}
			}

			if (!fluids.isEmpty()) {
				try {
					final IItemList<IAEFluidStack> monitorCache = monitor.getStorageList();

					final PacketMEFluidInventoryUpdate piu = new PacketMEFluidInventoryUpdate();

					for (final IAEFluidStack is : fluids) {
						final IAEFluidStack send = monitorCache.findPrecise(is);
						if (send == null) {
							is.setStackSize(0);
							piu.appendFluid(is);
						}
						else {
							piu.appendFluid(send);
						}
					}

					if (!piu.isEmpty()) {
						fluids.resetStatus();

						for (final Object c : listeners) {
							if (c instanceof EntityPlayer) {
								ModNetworking.instance().sendTo(piu, (EntityPlayerMP) c);
							}
						}
					}
				}
				catch (final IOException e) {
				}
			}

			super.detectAndSendChanges();

			if (!isInRange()) {
				if (!hasInfiniteRange()) {
					if (isValidContainer()) {
						getPlayer().sendMessage(PlayerMessages.OutOfRange.get());
					}
					setValidContainer(false);
				}
				if (!networkIsPowered()) {
					if (isValidContainer()) {
						getPlayer().sendMessage(new TextComponentString("No Network Power"));
					}
					setValidContainer(false);
				}
			}
			else if (!hasAccess(SecurityPermissions.CRAFT, true) || !hasAccess(SecurityPermissions.EXTRACT, true) || !hasAccess(SecurityPermissions.INJECT, true)) {
				if (isValidContainer()) {
					getPlayer().sendMessage(PlayerMessages.CommunicationError.get());
				}
				setValidContainer(false);
			}
			if (getWirelessTerminal().getItem() instanceof IWirelessFluidTerminalItem && ((IWirelessFluidTerminalItem) getWirelessTerminal().getItem()).getAECurrentPower(getWirelessTerminal()) <= 0) {
				if (isValidContainer()) {
					getPlayer().sendMessage(new TextComponentString("No Power"));
				}
				setValidContainer(false);
			}
		}
	}

	@Override
	public void doAction(final EntityPlayerMP player, final InventoryAction action, final int slot, final long id) {
		if (action != InventoryAction.FILL_ITEM && action != InventoryAction.EMPTY_ITEM && action != InventoryAction.SHIFT_CLICK && action != InventoryAction.ROLL_DOWN && action != InventoryAction.ROLL_UP) {
			super.doAction(player, action, slot, id);
			return;
		}
		ItemStack held = player.inventory.getItemStack();
		if (held.getCount() > 1) {
			return;
		}
		IFluidHandlerItem fh = FluidUtil.getFluidHandler(held);
		if (fh == null && !held.isEmpty()) {
			return;
		}

		if ((action == InventoryAction.FILL_ITEM || action == InventoryAction.ROLL_UP) && clientRequestedTargetFluid != null) {
			final IAEFluidStack stack = clientRequestedTargetFluid.copy();
			stack.setStackSize(Integer.MAX_VALUE);
			int amountAllowed = fh.fill(stack.getFluidStack(), false);
			if (action == InventoryAction.ROLL_UP) {
				final IAEFluidStack tmpStack = stack.copy();
				tmpStack.setStackSize(Fluid.BUCKET_VOLUME);
				amountAllowed = fh.fill(tmpStack.getFluidStack(), false);
			}
			stack.setStackSize(amountAllowed);
			if (held.isEmpty()) {
				final IAEItemStack bucketInSystem = Platform.poweredExtraction(getPowerSource(), getGuiObject().getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class)), AEItemStack.fromItemStack(new ItemStack(Items.BUCKET)), getActionSource(), Actionable.SIMULATE);
				if (bucketInSystem != null) {
					held = bucketInSystem.createItemStack();
					fh = FluidUtil.getFluidHandler(held);
				}
				else {
					return;
				}
			}
			final IAEFluidStack canPull = Platform.poweredExtraction(getPowerSource(), monitor, stack, getActionSource(), Actionable.SIMULATE);
			if (canPull == null || canPull.getStackSize() < 1) {
				return;
			}
			final int canFill = fh.fill(canPull.getFluidStack(), false);
			if (canFill == 0) {
				return;
			}
			stack.setStackSize(canFill);
			final IAEFluidStack pulled = Platform.poweredExtraction(getPowerSource(), monitor, stack, getActionSource());
			if (pulled == null || pulled.getStackSize() < 1) {
				return;
			}
			fh.fill(pulled.getFluidStack(), true);
			player.inventory.setItemStack(fh.getContainer());
			updateHeld(player);
		}
		else if ((action == InventoryAction.EMPTY_ITEM || action == InventoryAction.ROLL_DOWN) && fh != null) {
			final FluidStack extract = fh.drain(action == InventoryAction.ROLL_DOWN ? Fluid.BUCKET_VOLUME : Integer.MAX_VALUE, false);
			if (extract == null || extract.amount < 1) {
				return;
			}
			final IAEFluidStack notStorable = Platform.poweredInsert(getPowerSource(), monitor, AEFluidStack.fromFluidStack(extract), getActionSource(), Actionable.SIMULATE);
			if (notStorable != null && notStorable.getStackSize() > 0) {
				final int toStore = (int) (extract.amount - notStorable.getStackSize());
				final FluidStack storable = fh.drain(toStore, false);
				if (storable == null || storable.amount == 0) {
					return;
				}
				else {
					extract.amount = storable.amount;
				}
			}
			final FluidStack drained = fh.drain(extract, true);
			extract.amount = drained.amount;
			Platform.poweredInsert(getPowerSource(), monitor, AEFluidStack.fromFluidStack(extract), getActionSource());
			player.inventory.setItemStack(fh.getContainer());
			updateHeld(player);
		}
		else if (action == InventoryAction.EMPTY_ITEM && fh == null) {
			if (player.inventory.getItemStack().isEmpty()) {
				final ItemStack stack = inventorySlots.get(slot).getStack();
				fh = FluidUtil.getFluidHandler(stack);
				if (fh != null) {
					final FluidStack extract = fh.drain(Integer.MAX_VALUE, false);
					if (extract == null || extract.amount < 1) {
						return;
					}
					final IAEFluidStack notStorable = Platform.poweredInsert(getPowerSource(), monitor, AEFluidStack.fromFluidStack(extract), getActionSource(), Actionable.SIMULATE);
					if (notStorable != null && notStorable.getStackSize() > 0) {
						final int toStore = (int) (extract.amount - notStorable.getStackSize());
						final FluidStack storable = fh.drain(toStore, false);
						if (storable == null || storable.amount == 0) {
							return;
						}
						else {
							extract.amount = storable.amount;
						}
					}
					final FluidStack drained = fh.drain(extract, true);
					extract.amount = drained.amount;
					Platform.poweredInsert(getPowerSource(), monitor, AEFluidStack.fromFluidStack(extract), getActionSource());
					inventorySlots.get(slot).putStack(fh.getContainer());
				}
			}
		}
		else {
			if (action == InventoryAction.ROLL_DOWN) {

			}
			System.out.println(action);
		}
	}

	@Override
	protected void updateHeld(final EntityPlayerMP p) {
		if (Platform.isServer()) {
			try {
				ModNetworking.instance().sendTo(new PacketInventoryAction(InventoryAction.UPDATE_HAND, 0, AEItemStack.fromItemStack(p.inventory.getItemStack())), p);
			}
			catch (final IOException e) {
			}
		}
	}

	public boolean isPowered() {
		final double pwr = ((ICustomWirelessTerminalItem) getWirelessTerminal().getItem()).getAECurrentPower(getWirelessTerminal());
		return pwr > 0.0;
	}

	@Override
	public void saveChanges() {
	}

	@Override
	public void onChangeInventory(final IItemHandler inv, final int slot, final InvOperation mc, final ItemStack removedStack, final ItemStack newStack) {
	}

	@Override
	public ItemStack transferStackInSlot(final EntityPlayer p, final int idx) {
		return ItemStack.EMPTY;
	}

}
