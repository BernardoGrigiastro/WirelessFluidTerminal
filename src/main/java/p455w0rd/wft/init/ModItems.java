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

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import p455w0rd.ae2wtlib.api.client.IModelHolder;
import p455w0rd.ae2wtlib.client.render.ItemLayerWrapper;
import p455w0rd.ae2wtlib.client.render.WTItemRenderer;
import p455w0rd.wft.items.ItemWFT;
import p455w0rd.wft.items.ItemWFTCreative;

/**
 * @author p455w0rd
 *
 */
public class ModItems {

	public static final ItemWFT WFT = new ItemWFT();
	public static final ItemWFTCreative CREATIVE_WFT = new ItemWFTCreative();
	private static final Item[] ITEM_ARRAY = new Item[] {
			WFT, CREATIVE_WFT
	};
	private static final List<Item> ITEM_LIST = Lists.newArrayList(ITEM_ARRAY);

	public static final List<Item> getList() {
		return ITEM_LIST;
	}

	public static final void register(IForgeRegistry<Item> registry) {
		registry.registerAll(ITEM_ARRAY);
	}

	@SideOnly(Side.CLIENT)
	public static final void initModels(ModelBakeEvent event) {
		for (Item item : getList()) {
			if (item instanceof IModelHolder) {
				IModelHolder holder = (IModelHolder) item;
				holder.initModel();
				if (holder.shouldUseInternalTEISR()) {
					IBakedModel wtModel = event.getModelRegistry().getObject(holder.getModelResource());
					holder.setWrappedModel(new ItemLayerWrapper(wtModel));
					if (item.getTileEntityItemStackRenderer() instanceof WTItemRenderer) {
						WTItemRenderer renderer = (WTItemRenderer) item.getTileEntityItemStackRenderer();
						renderer.setModel(holder.getWrappedModel());
					}
					event.getModelRegistry().putObject(holder.getModelResource(), holder.getWrappedModel());
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static final void registerTEISRs(ModelRegistryEvent event) {
		for (Item item : getList()) {
			if (item instanceof IModelHolder) {
				IModelHolder holder = (IModelHolder) item;
				if (holder.shouldUseInternalTEISR()) {
					item.setTileEntityItemStackRenderer(new WTItemRenderer());
				}
			}
		}
	}

}
