package dev.abidux.moreautomation.gui.treecutter;

import dev.abidux.moreautomation.MoreAutomationMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TreeCutterScreen extends AbstractContainerScreen<TreeCutterMenu> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MoreAutomationMod.MOD_ID, "textures/gui/tree_cutter.png");
    public TreeCutterScreen(TreeCutterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float tick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        int progress = menu.getScaledProgress();
        graphics.blit(TEXTURE, x + 27, y + 46 + (13 - progress), 176, 13 - progress, 14, progress + 1);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
    }
}