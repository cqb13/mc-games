package dev.cqb13.McGames.modules;

import java.util.List;

import dev.cqb13.McGames.McGames;
import dev.cqb13.McGames.enums.Difficulty;
import dev.cqb13.McGames.utils.GameUtils;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ScavengerHunt extends Module {
  private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
  private final SettingGroup sgDimensions = settings.createGroup("Dimensions");

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
      .build());

  public ScavengerHunt() {
    super(McGames.CATEGORY, "scavenger-hunt", "Find and collect items.");
  }

  private HashMap<Item, Integer> requirements;
  private List<Item> allItems = new ArrayList<>();

  @Override
  public void onActivate() {
    for (Item item : Registries.ITEM) {
      allItems.add(item);
    }

    selectItems();
  }

  private void optionSwitch() {
    selectItems();
  }

  // easy, 3 kinds of items, limited to max of 10 of each
  // medium, 6 kinds of items, limited to max of 32 of each
  // hard, 12 kinds of items, limited to max of 128 of each
  private void selectItems() {
    Random random = new Random();

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

    while (items != 0) {
      Item chosen = allItems.get(random.nextInt(allItems.size()));

      if (requirements.containsKey(chosen)) {
        continue;
      }

      if (blackListedItems.get().contains(chosen)) {
        continue;
      }

      int amount = random.nextInt(1, itemLimit + 1);

      if (chosen.getMaxCount() > amount) {
        amount = chosen.getMaxCount();
      }

      requirements.put(chosen, amount);

      items -= 1;
    }
  }

  @EventHandler
  private void onGameLeft(GameLeftEvent event) {
    System.out.println(blackListedItems.get());
    toggle();
  }
}
