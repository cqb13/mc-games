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

public class ScavengerHunt extends Module {
  private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
  private final SettingGroup sgDimensions = settings.createGroup("Dimensions");

  // easy, 3 kinds of items, limited to max of 10 of each
  // medium, 6 kinds of items, limited to max of 32 of each
  // hard, 12 kinds of items, limited to max of 128 of each
  private final Setting<Difficulty> difficulty = sgGeneral.add(new EnumSetting.Builder<Difficulty>()
      .name("difficulty")
      .description("Affects the amount of items you must collect.")
      .defaultValue(Difficulty.Normal)
      .onChanged(d -> optionSwitch())
      .build());

  private final Setting<List<Item>> items = sgGeneral.add(new ItemListSetting.Builder()
      .name("block-blacklist")
      .description("Blocks that will not be used in the scavenger hunt.")
      .defaultValue(GameUtils.defualtBlackList)
      .build());

  public ScavengerHunt() {
    super(McGames.CATEGORY, "scavenger-hunt", "Find and collect items.");
  }

  @Override
  public void onActivate() {
  }

  private void optionSwitch() {
  }

  @EventHandler
  private void onGameLeft(GameLeftEvent event) {
    System.out.println(items.get());
    toggle();
  }
}
