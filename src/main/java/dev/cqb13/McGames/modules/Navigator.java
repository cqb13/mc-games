
package dev.cqb13.McGames.modules;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

import dev.cqb13.McGames.McGames;
import dev.cqb13.McGames.utils.McGamesChatUtils;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class Navigator extends Module {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private final Setting<Integer> maxDistance = sgGeneral.add(new IntSetting.Builder()
            .name("maximum-distance")
            .description("The maximum distance to generate the destination.")
            .min(1000)
            .max(50000)
            .sliderMin(1000)
            .sliderMax(50000)
            .defaultValue(10000)
            .build());

    public Navigator() {
        super(McGames.CATEGORY, "navigator", "Make your way to a random location near you");
    }

    private BlockPos destination;
    private LocalDateTime startTime;

    @Override
    public void onActivate() {
        if (mc.world == null || mc.player == null) {
            return;
        }

        Vec3d pos = mc.player.getEntityPos();
        double px = pos.x;
        double py = pos.y;
        double pz = pos.z;

        double nx, ny, nz;

        while (true) {
            double dx = (mc.world.getRandom().nextDouble() * 2 - 1) * maxDistance.get();
            double dz = (mc.world.getRandom().nextDouble() * 2 - 1) * maxDistance.get();

            nx = px + dx;
            nz = pz + dz;

            nx = Math.max(-29999999, Math.min(29999999, nx));
            Random random = new Random();
            ny = random.nextInt(321 + 64) - 64;
            nz = Math.max(-29999999, Math.min(29999999, nz));

            if (distanceTo(px, py, pz, nx, ny, nz) <= maxDistance.get()) {
                break;
            }
        }

        destination = new BlockPos(new Vec3i((int) nx, (int) ny, (int) nz));

        sendGoalMessage();
        startTime = LocalDateTime.now();
    }

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WVerticalList list = theme.verticalList();

        WHorizontalList l1 = list.add(theme.horizontalList()).expandX().widget();

        WButton start = l1.add(theme.button("Remind Me")).expandX().widget();
        start.action = () -> {
            if (this.isActive()) {
                sendGoalMessage();
            }
        };

        return list;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.world == null || mc.player == null) {
            return;
        }

        BlockPos playerPos = mc.player.getBlockPos();

        if (playerPos.equals(destination)) {
            McGamesChatUtils.sendGameMsg(title, "You made it!");

            LocalDateTime stopTime = LocalDateTime.now();
            Duration duration = Duration.between(startTime, stopTime);

            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();
            long seconds = duration.toSecondsPart();

            String timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);

            McGamesChatUtils.sendGameMsg(title, timeFormatted);
            toggle();
        }
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        toggle();
    }

    public void sendGoalMessage() {
        McGamesChatUtils.sendGameMsg(title,
                String.format("Your destination is (%d, %d, %d), you must reach it as fast as possible.",
                        destination.getX(), destination.getY(), destination.getZ()));

    }

    private static double distanceTo(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dz = z1 - z2;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
