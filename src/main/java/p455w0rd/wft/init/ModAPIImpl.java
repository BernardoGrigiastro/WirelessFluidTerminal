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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.relauncher.Side;
import p455w0rd.wft.WFT;
import p455w0rd.wft.api.IWirelessFluidTerminalItem;
import p455w0rd.wft.api.WFTApi;
import p455w0rd.wft.sync.packets.PacketOpenGui;
import p455w0rd.wft.util.WFTUtils;

/**
 * @author p455w0rd
 *
 */
public class ModAPIImpl extends WFTApi {

	private static ModAPIImpl INSTANCE = null;

	public static ModAPIImpl instance() {
		if (ModAPIImpl.INSTANCE == null) {
			if (!ModAPIImpl.hasFinishedPreInit()) {
				return null;
			}
			ModAPIImpl.INSTANCE = new ModAPIImpl();
		}
		return INSTANCE;
	}

	protected static boolean hasFinishedPreInit() {
		if (WFT.PROXY.getLoaderState() == LoaderState.NOINIT) {
			ModLogger.warn("API is not available until WFT finishes the PreInit phase.");
			return false;
		}

		return true;
	}

	@Override
	public void openWFTGui(final EntityPlayer player, final boolean isBauble, final int wftSlot) {
		if ((player == null) || (player instanceof FakePlayer) || (player instanceof EntityPlayerMP) || FMLCommonHandler.instance().getSide() == Side.SERVER) {
			return;
		}
		ItemStack is = WFTUtils.getFluidTerm(player.inventory);
		if (!is.isEmpty() && isTerminalLinked(is)) {
			ModNetworking.instance().sendToServer(new PacketOpenGui(ModGuiHandler.GUI_WFT, isBauble, wftSlot));
		}
	}

	@Override
	public boolean isTerminalLinked(final ItemStack wirelessTerminalItemstack) {
		String sourceKey = "";
		if (wirelessTerminalItemstack.getItem() instanceof IWirelessFluidTerminalItem && wirelessTerminalItemstack.hasTagCompound()) {
			sourceKey = ((IWirelessFluidTerminalItem) wirelessTerminalItemstack.getItem()).getEncryptionKey(wirelessTerminalItemstack);
			return (sourceKey != null) && (!sourceKey.isEmpty());
		}
		return false;
	}

}
