package dev.abidux.moreautomation.item.custom;

import dev.abidux.moreautomation.util.LocationData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TransporterRemoteItem extends Item {
    public TransporterRemoteItem() {
        super(new Item.Properties().stacksTo(1).durability(8).setNoRepair());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pIsAdvanced) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("x")) {
            tooltip.add(Screen.hasShiftDown() ? Component.translatable("tooltip.transpositioner_remote_guide") : Component.translatable("tooltip.press_shift_for_more").withStyle(ChatFormatting.YELLOW));
            return;
        }
        LocationData data = LocationData.from(tag);
        tooltip.add(Component.translatable("tooltip.transpositioner_remote", data.x, data.y, data.z, data.dimension).withStyle(ChatFormatting.YELLOW));
    }
}