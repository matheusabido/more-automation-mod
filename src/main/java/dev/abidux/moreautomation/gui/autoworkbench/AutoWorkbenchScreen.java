package dev.abidux.moreautomation.gui.autoworkbench;

import dev.abidux.moreautomation.MoreAutomationMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AutoWorkbenchScreen extends AbstractContainerScreen<AutoWorkbenchMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(MoreAutomationMod.MOD_ID, "textures/gui/auto_workbench.png");
    public AutoWorkbenchScreen(AutoWorkbenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float tick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        graphics.renderFakeItem(menu.blockEntity.craftingItem, x+62, y+53);
        graphics.renderItemDecorations(Minecraft.getInstance().font, menu.blockEntity.craftingItem, x+62, y+53);
        renderProgress(graphics, x+63, y+41);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
    }

    private void renderProgress(GuiGraphics graphics, int x, int y) {
        int progress = menu.getScaledProgress();
        graphics.blit(TEXTURE, x, y, 0, 166, progress, 5);
    }
}