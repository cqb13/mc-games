package dev.cqb13.McGames.modules;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import dev.cqb13.McGames.McGames;
import dev.cqb13.McGames.enums.Difficulty;
import dev.cqb13.McGames.utils.McGamesChatUtils;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class GettingStarted extends Module {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private final Setting<Difficulty> difficulty = sgGeneral.add(new EnumSetting.Builder<Difficulty>()
            .name("difficulty")
            .description("Affects the kinds of gear and items you must collect.")
            .defaultValue(Difficulty.Normal)
            .onChanged(d -> optionSwitch())
            .build());

    private static final List<Item> validEasyModeFood = Arrays.asList(Items.COOKED_COD, Items.COOKED_BEEF,
            Items.COOKED_MUTTON, Items.COOKED_RABBIT, Items.COOKED_SALMON, Items.COOKED_CHICKEN, Items.COOKED_PORKCHOP);

    private static final List<Item> validNormalModeFood = Arrays.asList(Items.COOKED_BEEF, Items.GOLDEN_CARROT,
            Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE);

    private static final List<Item> validHardModeFood = Arrays.asList(Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE);

    public GettingStarted() {
        super(McGames.CATEGORY, "getting-started", "Collect esential items and gear as fast as you can.");
    }

    private LocalTime start;

    @Override
    public void onActivate() {

        sendRequiredItems();
        start = LocalTime.now();
    }

    private void optionSwitch() {
        if (!isActive()) {
            return;
        }
        sendRequiredItems();
        start = LocalTime.now();
    }

    /*
     * Easy mode:
     * Full set iron, sword, and pic
     * Stack of any cooked meat
     * Stack of cobblestone
     *
     * Normal Mode:
     * Full set diamond, sword, pic, axe
     * Stack of either steak,egap,gap,gcarret
     * 2 stacks of stone
     * a stack of iron
     *
     * Hard Mode:
     * Full set netherite, sword, pic, axe, hoe, shovel
     * Stack of either egap,gap
     * 2 stacks of quartz blocks
     * a stack of diamonds
     */
    private void sendRequiredItems() {
        MutableText message = Text.empty();
        message.append("\n\n");
        message.append("Please collect the following items:\n");
        McGamesChatUtils.sendGameMsg(title, message);
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        assert mc.player != null;

        PlayerInventory inventory = mc.player.getInventory();

        boolean won = false;

    }

    /*
     * Full set iron, sword, and pic
     * Stack of any cooked meat
     * Stack of cobblestone
     */
    private boolean easyModeChecks(PlayerInventory inventory) {
        if (!wearingArmorType(Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS)) {
            return false;
        }

        if (!hasEnoughBlocks(inventory, Items.COBBLESTONE, 64)) {
            return false;
        }

        boolean hasEnoughFood = hasEnoughFood(inventory, validEasyModeFood);

        if (!hasEnoughFood) {
            return false;
        }

        if (inventory.count(Items.IRON_SWORD) == 0) {
            return false;
        }

        if (inventory.count(Items.IRON_PICKAXE) == 0) {
            return false;
        }

        return true;
    }

    private boolean wearingArmorType(Item head, Item chest, Item legs, Item feet) {
        ItemStack headArmor = mc.player.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chestArmor = mc.player.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack legArmor = mc.player.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack feetArmor = mc.player.getEquippedStack(EquipmentSlot.FEET);

        return headArmor.getItem() == head && chestArmor.getItem() == chest && legArmor.getItem() == legs
                && feetArmor.getItem() == feet;
    }

    private boolean hasEnoughFood(PlayerInventory inventory, List<Item> foodList) {
        boolean hasEnoughFood = false;

        for (Item food : validEasyModeFood) {
            if (inventory.count(food) >= 64) {
                hasEnoughFood = true;
                break;
            }
        }

        return hasEnoughFood;
    }

    private boolean hasEnoughBlocks(PlayerInventory inventory, Item block, int minCount) {
        int amount = inventory.count(block);

        return amount >= minCount;
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        toggle();
    }
}
