package dev.cqb13.McGames.modules;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import dev.cqb13.McGames.McGames;
import dev.cqb13.McGames.enums.Difficulty;
import dev.cqb13.McGames.utils.GameUtils;
import dev.cqb13.McGames.utils.McGamesChatUtils;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ScavengerHunt extends Module {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private final Setting<Difficulty> difficulty = sgGeneral.add(new EnumSetting.Builder<Difficulty>()
            .name("difficulty")
            .description("Affects the amount of items you must collect.")
            .defaultValue(Difficulty.Normal)
            .onChanged(d -> optionSwitch())
            .build());

    private final Setting<List<Item>> blackListedItems = sgGeneral.add(new ItemListSetting.Builder()
            .name("block-blacklist")
            .description("Blocks that will not be used in the scavenger hunt.")
            .defaultValue(GameUtils.defualtBlackList)
            .onChanged(b -> optionSwitch())
            .build());

    public ScavengerHunt() {
        super(McGames.CATEGORY, "scavenger-hunt", "Find and collect items.");
    }

    private HashMap<Item, Integer> requirements;
    private List<Item> allItems = new ArrayList<>();
    private LocalTime start;

    @Override
    public void onActivate() {
        for (Item item : Registries.ITEM) {
            allItems.add(item);
        }

        selectItems();
        sendRequiredItems();
        start = LocalTime.now();
    }

    private void optionSwitch() {
        if (!isActive()) {
            return;
        }
        selectItems();
        sendRequiredItems();
        start = LocalTime.now();
    }

    private void sendRequiredItems() {
        MutableText message = Text.empty();
        message.append("\n\n");
        message.append("Please collect the following items:\n");
        for (Map.Entry<Item, Integer> items : requirements.entrySet()) {
            message.append("  - " + items.getKey().toString().trim().replace("minecraft:", "").replace("_", " ") + " x"
                    + items.getValue() + "\n");
        }
        McGamesChatUtils.sendGameMsg(title, message);
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        assert mc.player != null;

        PlayerInventory inventory = mc.player.getInventory();

        boolean hasAllItems = true;

        for (Map.Entry<Item, Integer> items : requirements.entrySet()) {
            if (inventory.count(items.getKey()) < items.getValue()) {
                hasAllItems = false;
                break;
            }
        }

        if (!hasAllItems) {
            return;
        }

        sendWinMessage();
        toggle();
    }

    private void sendWinMessage() {
        String time = GameUtils.calculateDuration(start);
        MutableText message = Text.empty();
        message.append("\n\n");
        message.append("You collected all the items!\n");
        message.append("It took you: " + time + "\n");
        McGamesChatUtils.sendGameMsg(title, message);
    }

    // easy, 3 kinds of items, limited to max of 10 of each
    // medium, 6 kinds of items, limited to max of 32 of each
    // hard, 12 kinds of items, limited to max of 128 of each
    private void selectItems() {
        Random random = new Random();
        requirements = new HashMap<>();

        int items = 0;
        int itemLimit = 0;

        switch (difficulty.get()) {
            case Difficulty.Easy:
                items = 3;
                itemLimit = 10;
                break;
            case Difficulty.Normal:
                items = 6;
                itemLimit = 32;
                break;
            case Difficulty.Hard:
                items = 12;
                itemLimit = 128;
                break;
        }

        while (items > 0) {
            if (allItems.isEmpty() || itemLimit <= 0)
                break;

            Item chosen = allItems.get(random.nextInt(0, allItems.size()));

            if (requirements.containsKey(chosen))
                continue;
            if (blackListedItems.get().contains(chosen))
                continue;

            int amount = random.nextInt(1, itemLimit + 1);

            if (amount > chosen.getMaxCount()) {
                amount = chosen.getMaxCount();
            }

            requirements.put(chosen, amount);
            items--;
        }

    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        toggle();
    }
}
