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
package p455w0rd.wft.sync;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import p455w0rd.wft.sync.packets.*;

public class WFTPacketHandlerBase {
	private static final Map<Class<? extends WFTPacket>, PacketTypes> REVERSE_LOOKUP = new HashMap<Class<? extends WFTPacket>, WFTPacketHandlerBase.PacketTypes>();

	public enum PacketTypes {

			PACKET_INVENTORY_ACTION(PacketInventoryAction.class),

			PACKET_VALUE_CONFIG(PacketValueConfig.class),

			PACKET_OPENWIRELESSTERM(PacketOpenGui.class),

			PACKET_ME_FLUID_INVENTORY_UPDATE(PacketMEFluidInventoryUpdate.class),

			PACKET_TARGET_FLUID(PacketTargetFluidStack.class);

		private final Class<? extends WFTPacket> packetClass;
		private final Constructor<? extends WFTPacket> packetConstructor;

		PacketTypes(final Class<? extends WFTPacket> c) {
			packetClass = c;

			Constructor<? extends WFTPacket> x = null;
			try {
				x = packetClass.getConstructor(ByteBuf.class);
			}
			catch (final NoSuchMethodException ignored) {
			}
			catch (final SecurityException ignored) {
			}
			catch (final DecoderException ignored) {
			}

			packetConstructor = x;
			REVERSE_LOOKUP.put(packetClass, this);

			if (packetConstructor == null) {
				throw new IllegalStateException("Invalid Packet Class " + c + ", must be constructable on DataInputStream");
			}
		}

		public static PacketTypes getPacket(final int id) {
			return (values())[id];
		}

		static PacketTypes getID(final Class<? extends WFTPacket> c) {
			return REVERSE_LOOKUP.get(c);
		}

		public WFTPacket parsePacket(final ByteBuf in) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			return packetConstructor.newInstance(in);
		}
	}
}