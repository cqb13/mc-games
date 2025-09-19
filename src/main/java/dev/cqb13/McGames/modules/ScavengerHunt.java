package dev.cqb13.McGames.modules;

import dev.cqb13.McGames.McGames;
import dev.cqb13.McGames.enums.Difficulty;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

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

  private Setting<Boolean> overworld = sgDimensions.add(new BoolSetting.Builder()
      .name("overworld")
      .description("Include blocks from the overworld.")
      .defaultValue(true)
      .onChanged(d -> optionSwitch())
      .build());

  private Setting<Boolean> nether = sgDimensions.add(new BoolSetting.Builder()
      .name("nether")
      .description("Include blocks from the nether.")
      .defaultValue(true)
      .onChanged(d -> optionSwitch())
      .build());
  private Setting<Boolean> end = sgDimensions.add(new BoolSetting.Builder()
      .name("overworld")
      .description("Include blocks from the end.")
      .defaultValue(false)
      .onChanged(d -> optionSwitch())
      .build());

  public ScavengerHunt() {
    super(McGames.CATEGORY, "wordle", "Play Wordle forever.");
  }

  @Override
  public void onActivate() {
  }

  private void optionSwitch() {

  }

  @EventHandler
  private void onGameLeft(GameLeftEvent event) {
    toggle();
  }
}
