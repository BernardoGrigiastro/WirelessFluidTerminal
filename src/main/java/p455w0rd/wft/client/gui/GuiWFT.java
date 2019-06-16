package p455w0rd.wft.client.gui;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;

import org.lwjgl.input.Mouse;

import appeng.api.config.SearchBoxMode;
import appeng.api.config.Settings;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.client.gui.widgets.GuiImgButton;
import appeng.client.gui.widgets.ISortSource;
import appeng.client.me.*;
import appeng.core.AEConfig;
import appeng.core.localization.GuiText;
import appeng.fluids.container.slots.IMEFluidSlot;
import appeng.helpers.InventoryAction;
import appeng.util.IConfigManagerHost;
import appeng.util.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.Loader;
import p455w0rd.ae2wtlib.api.WTApi;
import p455w0rd.ae2wtlib.api.client.FluidStackSizeRenderer;
import p455w0rd.ae2wtlib.api.client.ReadableNumberConverter;
import p455w0rd.ae2wtlib.api.client.gui.GuiWT;
import p455w0rd.ae2wtlib.api.client.gui.widgets.*;
import p455w0rd.wft.container.ContainerWFT;
import p455w0rd.wft.init.ModGlobals;
import p455w0rd.wft.init.ModNetworking;
import p455w0rd.wft.sync.packets.PacketInventoryAction;
import p455w0rd.wft.sync.packets.PacketValueConfig;
import p455w0rd.wft.util.WFTUtils;
import yalter.mousetweaks.api.MouseTweaksIgnore;

@MouseTweaksIgnore
public class GuiWFT extends GuiWT implements ISortSource, IConfigManagerHost {

	private final List<SlotFluidME> meFluidSlots = new LinkedList<>();
	private final FluidRepo repo;
	private final IConfigManager configSrc;
	private final ContainerWFT container;
	private final int rows = 5;

	protected ITerminalHost terminal;

	private GuiMETextField searchField;
	private GuiImgButton sortByBox;
	private GuiImgButton sortDirBox;
	private GuiImgButtonBooster autoConsumeBoostersBox;

	public GuiWFT(final Container container) {
		super(container);
		xSize = 185;
		ySize = 222;
		terminal = ((ContainerWFT) container).getGuiObject();
		final GuiScrollbar scrollbar = new GuiScrollbar();
		this.setScrollBar(scrollbar);
		repo = new FluidRepo(scrollbar, this);
		configSrc = ((IConfigurableObject) inventorySlots).getConfigManager();
		this.container = (ContainerWFT) inventorySlots;
		this.container.setGui(this);
	}

	@Override
	public void initGui() {
		mc.player.openContainer = inventorySlots;
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;

		searchField = new GuiMETextField(fontRenderer, guiLeft + Math.max(79, 8), guiTop + 4, 90, 12);
		searchField.setEnableBackgroundDrawing(false);
		searchField.setMaxStringLength(25);
		searchField.setTextColor(0xFFFFFF);
		searchField.setSelectionColor(0xFF99FF99);
		searchField.setVisible(true);

		//int offset = guiTop + 5;

		super.initGui();

		getButtonPanel().addButton(sortByBox = new GuiImgButton(getButtonPanelXOffset(), getButtonPanelYOffset(), Settings.SORT_BY, configSrc.getSetting(Settings.SORT_BY)));
		//offset += 20;

		getButtonPanel().addButton(sortDirBox = new GuiImgButton(getButtonPanelXOffset(), getButtonPanelYOffset(), Settings.SORT_DIRECTION, configSrc.getSetting(Settings.SORT_DIRECTION)));
		if (!WTApi.instance().getConfig().isOldInfinityMechanicEnabled() && !WTApi.instance().isWTCreative(getWirelessTerminal())) {
			//offset += 20;
			getButtonPanel().addButton(autoConsumeBoostersBox = new GuiImgButtonBooster(getButtonPanelXOffset(), getButtonPanelYOffset(), container.getWirelessTerminal()));
		}
		getButtonPanel().init(this);
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < 9; x++) {
				final SlotFluidME slot = new SlotFluidME(new InternalFluidSlotME(repo, x + y * 9, 8 + x * 18, 18 + y * 18));
				getMeFluidSlots().add(slot);
				inventorySlots.inventorySlots.add(slot);
			}
		}
		this.setScrollBar();
	}

	@Override
	public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
		fontRenderer.drawString(getGuiDisplayName("Fluid Terminal"), 8, 6, 4210752);
		fontRenderer.drawString(GuiText.inventory.getLocal(), 8, ySize - 100, 4210752);

		String warning = "";
		if (WTApi.instance().getConfig().isInfinityBoosterCardEnabled() && !WTApi.instance().getConfig().isOldInfinityMechanicEnabled()) {
			final int infinityEnergyAmount = WTApi.instance().getInfinityEnergy(container.getWirelessTerminal());
			if (WTApi.instance().hasInfiniteRange(getWirelessTerminal())) {
				if (!WTApi.instance().isInRangeOfWAP(getWirelessTerminal(), WFTUtils.player())) {
					if (infinityEnergyAmount < WTApi.instance().getConfig().getLowInfinityEnergyWarningAmount()) {
						warning = TextFormatting.RED + "" + I18n.format(WTApi.instance().getConstants().getTooltips().infinityEnergyLow());
					}
				}
			}
			if (!WTApi.instance().isWTCreative(getWirelessTerminal()) && isPointInRegion(container.getBoosterSlot().xPos, container.getBoosterSlot().yPos, 16, 16, mouseX, mouseY) && mc.player.inventory.getItemStack().isEmpty()) {
				final String amountColor = infinityEnergyAmount < WTApi.instance().getConfig().getLowInfinityEnergyWarningAmount() ? TextFormatting.RED.toString() : TextFormatting.GREEN.toString();
				final String infinityEnergy = I18n.format(WTApi.instance().getConstants().getTooltips().infinityEnergy()) + ": " + amountColor + "" + (isShiftKeyDown() ? infinityEnergyAmount : ReadableNumberConverter.INSTANCE.toSlimReadableForm(infinityEnergyAmount)) + "" + TextFormatting.GRAY + " " + I18n.format(WTApi.instance().getConstants().getTooltips().units());
				drawTooltip(mouseX - offsetX, mouseY - offsetY, infinityEnergy);
			}
		}
		//if (!warning.isEmpty()) {
		//	GlStateManager.enableBlend();
		//	GlStateManager.color(1, 1, 1, 1);
		mc.fontRenderer.drawString(warning, 8, ySize - 111, 4210752);
		//}
	}

	@Override
	public void drawBG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
		final ResourceLocation loc = new ResourceLocation(ModGlobals.MODID, "textures/" + getBackground());
		mc.getTextureManager().bindTexture(loc);

		final int x_width = 197;
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, x_width, 18);

		for (int x = 0; x < 5; x++) {
			this.drawTexturedModalRect(offsetX, offsetY + 18 + x * 18, 0, 18, x_width, 18);
		}

		this.drawTexturedModalRect(offsetX, offsetY + 16 + 6 * 18 - 18, 0, 106 - 18 - 18, x_width, 8);
		this.drawTexturedModalRect(offsetX, offsetY + 16 + 6 * 18 - 10, 0, 106 - 18 - 18 + 8, x_width, 7);
		this.drawTexturedModalRect(offsetX, offsetY + 16 + 6 * 18 - 3, 0, 106 - 18 - 18 + 8, x_width, 7);
		//this.drawTexturedModalRect(offsetX, offsetY + 16 + 6 * 18 + 4, 0, 106 - 18 - 18 + 8, x_width, 3);

		this.drawTexturedModalRect(offsetX, offsetY + 16 + 6 * 18, 0, 106 - 18 - 18 + 8, x_width, 99 + 77);
		if (WTApi.instance().getConfig().isInfinityBoosterCardEnabled() && !WTApi.instance().isWTCreative(getWirelessTerminal())) {
			drawTexturedModalRect(guiLeft + 150, guiTop + rows * 18 + 18, 237, 237, 19, 19);
		}
		if (searchField != null) {
			searchField.drawTextBox();
		}
	}

	@Override
	public void drawSlot(final Slot s) {
		if (s instanceof IMEFluidSlot && ((IMEFluidSlot) s).shouldRenderAsFluid()) {
			final IMEFluidSlot slot = (IMEFluidSlot) s;
			final IAEFluidStack fs = slot.getAEFluidStack();

			if (fs != null && isPowered()) {
				GlStateManager.disableLighting();
				GlStateManager.disableBlend();
				final Fluid fluid = fs.getFluid();
				Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				final TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getStill().toString());

				// Set color for dynamic fluids
				// Convert int color to RGB
				final float red = (fluid.getColor() >> 16 & 255) / 255.0F;
				final float green = (fluid.getColor() >> 8 & 255) / 255.0F;
				final float blue = (fluid.getColor() & 255) / 255.0F;
				GlStateManager.color(red, green, blue);

				this.drawTexturedModalRect(s.xPos, s.yPos, sprite, 16, 16);
				GlStateManager.enableLighting();
				GlStateManager.enableBlend();

				//if (s instanceof IMEFluidSlot) {
				//final IMEFluidSlot meFluidSlot = (IMEFluidSlot) s;
				FluidStackSizeRenderer.getInstance().renderStackSize(fontRenderer, fs, s.xPos, s.yPos);
				//}
			}
			return;
		}
		super.drawSlot(s);
	}

	@Override
	public void updateScreen() {
		repo.setPower(container.isPowered());
		super.updateScreen();
	}

	@Override
	protected void renderHoveredToolTip(final int mouseX, final int mouseY) {
		final Slot slot = getSlot(mouseX, mouseY);

		if (slot != null && slot instanceof IMEFluidSlot && slot.isEnabled()) {
			final IMEFluidSlot fluidSlot = (IMEFluidSlot) slot;

			if (fluidSlot.getAEFluidStack() != null && fluidSlot.shouldRenderAsFluid()) {
				final IAEFluidStack fluidStack = fluidSlot.getAEFluidStack();
				final String formattedAmount = NumberFormat.getNumberInstance(Locale.US).format(fluidStack.getStackSize() / 1000.0) + " B";

				final String modName = "" + TextFormatting.BLUE + TextFormatting.ITALIC + Loader.instance().getIndexedModList().get(Platform.getModId(fluidStack)).getName();

				final List<String> list = new ArrayList<>();

				list.add(fluidStack.getFluidStack().getLocalizedName());
				list.add(formattedAmount);
				list.add(modName);

				this.drawHoveringText(list, mouseX, mouseY);

				return;
			}
		}
		super.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void actionPerformed(final GuiButton btn) throws IOException {
		if (btn instanceof GuiImgButton) {
			final boolean backwards = Mouse.isButtonDown(1);
			final GuiImgButton iBtn = (GuiImgButton) btn;

			if (iBtn.getSetting() != Settings.ACTIONS) {
				final Enum<?> cv = iBtn.getCurrentValue();
				final Enum<?> next = Platform.rotateEnum(cv, backwards, iBtn.getSetting().getPossibleValues());
				ModNetworking.instance().sendToServer(new PacketValueConfig(iBtn.getSetting().name(), next.name()));
				iBtn.set(next);
			}
		}
		if (btn == autoConsumeBoostersBox) {
			autoConsumeBoostersBox.cycleValue();
		}
		super.actionPerformed(btn);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		final int i = Mouse.getEventDWheel();
		if (i != 0 && isShiftKeyDown()) {
			final int x = Mouse.getEventX() * width / mc.displayWidth;
			final int y = height - Mouse.getEventY() * height / mc.displayHeight - 1;
			mouseWheelEvent(x, y, i / Math.abs(i));
		}
	}

	private void mouseWheelEvent(final int x, final int y, final int wheel) {
		final Slot slot = getSlot(x, y);
		if (slot instanceof SlotFluidME) {
			final IAEFluidStack stack = ((SlotFluidME) slot).getAEFluidStack();

			if (inventorySlots instanceof ContainerWFT) {
				//if (stack != null) {
				((ContainerWFT) inventorySlots).setTargetStack(stack);
				//}
				final InventoryAction direction = wheel > 0 ? InventoryAction.ROLL_DOWN : InventoryAction.ROLL_UP;
				final int times = Math.abs(wheel);
				final int inventorySize = getInventorySlots().size();
				for (int h = 0; h < times; h++) {
					final PacketInventoryAction p = new PacketInventoryAction(direction, inventorySize, 0);
					ModNetworking.instance().sendToServer(p);
				}
			}
		}
	}

	@Override
	protected void handleMouseClick(final Slot slot, final int slotIdx, final int mouseButton, final ClickType clickType) {
		if (slot instanceof SlotFluidME) {
			final SlotFluidME meSlot = (SlotFluidME) slot;

			if (clickType == ClickType.PICKUP) {
				// TODO: Allow more options
				if (mouseButton == 0 && meSlot.getHasStack()) {
					container.setTargetStack(meSlot.getAEFluidStack());
					ModNetworking.instance().sendToServer(new PacketInventoryAction(InventoryAction.FILL_ITEM, slot.slotNumber, 0));
				}
				else {
					if (!mc.player.inventory.getItemStack().isEmpty()) {
						container.setTargetStack(meSlot.getAEFluidStack());
						ModNetworking.instance().sendToServer(new PacketInventoryAction(InventoryAction.EMPTY_ITEM, slot.slotNumber, 0));
					}
				}
			}
			return;
		}
		else {
			if (clickType == ClickType.QUICK_MOVE) {
				//EntityPlayer player = Minecraft.getMinecraft().player;
				if (slot.getHasStack()) {
					final ItemStack stack = slot.getStack();
					//boolean isBucket = stack.getItem() == Items.BUCKET || stack.getItem() == Items.WATER_BUCKET || stack.getItem() == Items.LAVA_BUCKET || stack.getItem() == Items.MILK_BUCKET || stack.getItem() == ForgeModContainer.getInstance().universalBucket;
					final IFluidHandlerItem fh = FluidUtil.getFluidHandler(stack);
					if (!stack.isEmpty() && fh != null) {
						if (fh != null) {
							final IFluidTankProperties props = fh.getTankProperties()[0];
							if (props != null) {
								final FluidStack fStack = props.getContents();
								if (fStack != null && fStack.amount > 0) {
									ModNetworking.instance().sendToServer(new PacketInventoryAction(InventoryAction.SHIFT_CLICK, slot.slotNumber, 0));
								}
							}
						}
					}
				}
			}
			//System.out.println(clickType);
		}
		super.handleMouseClick(slot, slotIdx, mouseButton, clickType);
	}

	@Override
	protected void keyTyped(final char character, final int key) throws IOException {
		if (!checkHotbarKeys(key)) {
			if (character == ' ' && searchField.getText().isEmpty()) {
				return;
			}

			if (searchField.textboxKeyTyped(character, key)) {
				repo.setSearchString(searchField.getText());
				repo.updateView();
				this.setScrollBar();
			}
			else {
				super.keyTyped(character, key);
			}
		}
	}

	@Override
	protected void mouseClicked(final int xCoord, final int yCoord, final int btn) throws IOException {
		final Enum<?> searchMode = AEConfig.instance().getConfigManager().getSetting(Settings.SEARCH_MODE);

		if (searchMode != SearchBoxMode.AUTOSEARCH && searchMode != SearchBoxMode.JEI_AUTOSEARCH) {
			searchField.mouseClicked(xCoord, yCoord, btn);
		}

		if (btn == 1 && searchField.isMouseIn(xCoord, yCoord)) {
			searchField.setText("");
			repo.setSearchString("");
			repo.updateView();
			this.setScrollBar();
		}

		super.mouseClicked(xCoord, yCoord, btn);
	}

	public void postUpdate(final List<IAEFluidStack> list) {
		for (final IAEFluidStack is : list) {
			repo.postUpdate(is);
		}

		repo.updateView();
		this.setScrollBar();
	}

	private void setScrollBar() {
		getScrollBar().setTop(18).setLeft(175).setHeight(rows * 18 - 2);
		getScrollBar().setRange(0, (repo.size() + 9 - 1) / 9 - rows, Math.max(1, rows / 6));
	}

	@Override
	public Enum getSortBy() {
		return configSrc.getSetting(Settings.SORT_BY);
	}

	@Override
	public Enum getSortDir() {
		return configSrc.getSetting(Settings.SORT_DIRECTION);
	}

	@Override
	public Enum getSortDisplay() {
		return configSrc.getSetting(Settings.VIEW_MODE);
	}

	@Override
	public void updateSetting(final IConfigManager manager, final Enum settingName, final Enum newValue) {
		if (sortByBox != null) {
			sortByBox.set(configSrc.getSetting(Settings.SORT_BY));
		}
		if (sortDirBox != null) {
			sortDirBox.set(configSrc.getSetting(Settings.SORT_DIRECTION));
		}
		repo.updateView();
	}

	protected List<SlotFluidME> getMeFluidSlots() {
		return meFluidSlots;
	}

	@Override
	protected boolean isPowered() {
		return repo.hasPower();
	}

	protected String getBackground() {
		return "gui/fluid.png";
	}

}
