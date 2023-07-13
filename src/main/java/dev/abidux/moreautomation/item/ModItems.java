package dev.abidux.moreautomation.item;

import dev.abidux.moreautomation.MoreAutomationMod;
import dev.abidux.moreautomation.block.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MoreAutomationMod.MOD_ID);

    public static final RegistryObject<Item> AUTO_WORKBENCH = register("auto_workbench", ModBlocks.AUTO_WORKBENCH, "messages.auto_workbench_guide");
    public static final RegistryObject<Item> PLACER = register("placer", ModBlocks.PLACER, "messages.placer_guide");
    public static final RegistryObject<Item> HARVESTER = register("harvester", ModBlocks.HARVESTER, "messages.harvester_guide");
    public static final RegistryObject<Item> TRANSPORTER = register("transporter", ModBlocks.TRANSPORTER, "messages.transporter_guide");
    public static final RegistryObject<Item> FILTER = register("filter", ModBlocks.FILTER, "messages.filter_guide");

    private static RegistryObject<Item> register(String name, RegistryObject<Block> block, String translatable) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()) {
            @Override
            public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pFlag) {
                if (Screen.hasShiftDown()) {
                    String text = Component.translatable(translatable).getString();
                    Arrays.stream(text.split("\n")).map(Component::literal).forEach(tooltip::add);
                } else tooltip.add(Component.translatable("messages.press_shift_for_more").withStyle(ChatFormatting.YELLOW));
            }
        });
    }

}