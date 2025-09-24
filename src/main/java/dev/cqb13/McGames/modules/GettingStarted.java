package dev.cqb13.McGames.modules;

import java.time.LocalTime;

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
import net.minecraft.entity.player.PlayerInventory;
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
   * Stack of any food
   * Stack of cobblestone
   *
   * Normal Mode:
   * Full set diamond, sword, pic, axe
   * Stack of either steak,porkchop,salmon,mutton,egap,gap,gcarret
   * 2 stacks of stone
   * a stack of iron
   *
   * Hard Mode:
   * Full set netherite, sword, pic, axe, hoe, shovel
   * Stack of either egap,gap,gcarret
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

    boolean hasAllItems = true;

  }

  @EventHandler
  private void onGameLeft(GameLeftEvent event) {
    toggle();
  }
}
