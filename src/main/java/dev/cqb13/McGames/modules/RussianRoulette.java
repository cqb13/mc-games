package dev.cqb13.McGames.modules;

import dev.cqb13.McGames.McGames;
import dev.cqb13.McGames.utils.McGamesChatUtils;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

public class RussianRoulette extends Module {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private final Setting<Boolean> agreement = sgGeneral.add(new BoolSetting.Builder()
            .name("i-agree")
            .description("I understand that this module can kill me, speak for me, and leak my coordinates to chat.")
            .defaultValue(false)
            .build());

    private final Setting<Boolean> noInstaDeath = sgGeneral.add(new BoolSetting.Builder()
            .name("no-instant-death")
            .description("Removes punishments such as /kill")
            .defaultValue(false)
            .build());

    private final Setting<Boolean> noCoordinateLeaks = sgGeneral.add(new BoolSetting.Builder()
            .name("no-coordinate-leaks")
            .description("Removes punishments that leak your coordinates")
            .defaultValue(false)
            .build());

    public RussianRoulette() {
        super(McGames.CATEGORY, "russian-roulette", "Gamble your life, dignity, and base");
    }

    @Override
    public void onActivate() {
        if (!agreement.get()) {
            McGamesChatUtils.sendGameMsg(title, "You must agree to the terms of this module before you can use it.");
            toggle();
            return;
        }
        toggle();
    }
}
