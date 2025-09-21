package dev.cqb13.McGames.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class GameUtils {
  public static final List<Item> defualtBlackList = Arrays.asList(Items.AIR, Items.VAULT, Items.TRIAL_SPAWNER,
      Items.DEBUG_STICK, Items.KNOWLEDGE_BOOK,
      Items.TIPPED_ARROW, Items.PLAYER_HEAD, Items.WRITTEN_BOOK, Items.ARMADILLO_SPAWN_EGG, Items.ALLAY_SPAWN_EGG,
      Items.AXOLOTL_SPAWN_EGG, Items.BAT_SPAWN_EGG, Items.BEE_SPAWN_EGG, Items.BLAZE_SPAWN_EGG,
      Items.BOGGED_SPAWN_EGG, Items.BREEZE_SPAWN_EGG, Items.CAT_SPAWN_EGG, Items.CAMEL_SPAWN_EGG,
      Items.CAVE_SPIDER_SPAWN_EGG, Items.CHICKEN_SPAWN_EGG, Items.COD_SPAWN_EGG, Items.COW_SPAWN_EGG,
      Items.CREEPER_SPAWN_EGG, Items.DOLPHIN_SPAWN_EGG, Items.DONKEY_SPAWN_EGG, Items.DROWNED_SPAWN_EGG,
      Items.ELDER_GUARDIAN_SPAWN_EGG, Items.ENDER_DRAGON_SPAWN_EGG, Items.ENDERMAN_SPAWN_EGG,
      Items.ENDERMITE_SPAWN_EGG, Items.EVOKER_SPAWN_EGG, Items.FOX_SPAWN_EGG, Items.FROG_SPAWN_EGG,
      Items.GHAST_SPAWN_EGG, Items.HAPPY_GHAST_SPAWN_EGG, Items.GLOW_SQUID_SPAWN_EGG, Items.GOAT_SPAWN_EGG,
      Items.GUARDIAN_SPAWN_EGG, Items.HOGLIN_SPAWN_EGG, Items.HORSE_SPAWN_EGG, Items.HUSK_SPAWN_EGG,
      Items.IRON_GOLEM_SPAWN_EGG, Items.LLAMA_SPAWN_EGG, Items.MAGMA_CUBE_SPAWN_EGG, Items.MOOSHROOM_SPAWN_EGG,
      Items.MULE_SPAWN_EGG, Items.OCELOT_SPAWN_EGG, Items.PANDA_SPAWN_EGG, Items.PARROT_SPAWN_EGG,
      Items.PHANTOM_SPAWN_EGG, Items.PIG_SPAWN_EGG, Items.PIGLIN_SPAWN_EGG, Items.PIGLIN_BRUTE_SPAWN_EGG,
      Items.PILLAGER_SPAWN_EGG, Items.POLAR_BEAR_SPAWN_EGG, Items.PUFFERFISH_SPAWN_EGG, Items.RABBIT_SPAWN_EGG,
      Items.RAVAGER_SPAWN_EGG, Items.SALMON_SPAWN_EGG, Items.SHEEP_SPAWN_EGG, Items.SHULKER_SPAWN_EGG,
      Items.SILVERFISH_SPAWN_EGG, Items.SKELETON_SPAWN_EGG, Items.SKELETON_HORSE_SPAWN_EGG, Items.SLIME_SPAWN_EGG,
      Items.SNIFFER_SPAWN_EGG, Items.SNOW_GOLEM_SPAWN_EGG, Items.SPIDER_SPAWN_EGG, Items.SQUID_SPAWN_EGG,
      Items.STRAY_SPAWN_EGG, Items.STRIDER_SPAWN_EGG, Items.TADPOLE_SPAWN_EGG, Items.TRADER_LLAMA_SPAWN_EGG,
      Items.TROPICAL_FISH_SPAWN_EGG, Items.TURTLE_SPAWN_EGG, Items.VEX_SPAWN_EGG, Items.VILLAGER_SPAWN_EGG,
      Items.VINDICATOR_SPAWN_EGG, Items.WANDERING_TRADER_SPAWN_EGG, Items.WARDEN_SPAWN_EGG, Items.WITCH_SPAWN_EGG,
      Items.WITHER_SPAWN_EGG, Items.WITHER_SKELETON_SPAWN_EGG, Items.WOLF_SPAWN_EGG, Items.ZOGLIN_SPAWN_EGG,
      Items.CREAKING_SPAWN_EGG, Items.ZOMBIE_SPAWN_EGG, Items.ZOMBIE_HORSE_SPAWN_EGG,
      Items.ZOMBIE_VILLAGER_SPAWN_EGG, Items.ZOMBIFIED_PIGLIN_SPAWN_EGG, Items.FILLED_MAP, Items.STRUCTURE_BLOCK,
      Items.JIGSAW, Items.TEST_BLOCK, Items.TEST_INSTANCE_BLOCK, Items.BARRIER, Items.LIGHT, Items.COMMAND_BLOCK,
      Items.SPAWNER, Items.BEDROCK, Items.PETRIFIED_OAK_SLAB);

  public static String[] fetchWordList(String url) throws McGamesException {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .build();

    String[] list;

    try {
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() != 200) {
        throw new McGamesException("Failed to fetch word list: " + response.statusCode(), null);
      }
      String content = response.body();
      content.trim();
      list = content.split("\n");
    } catch (Exception e) {
      throw new McGamesException("Failed to fetch word list", e);
    }

    return list;
  }

  public static String pickWordFromList(String[] wordList, int minWordLen, int maxWordLen) {
    String word = "";

    while (!(word.length() >= minWordLen && word.length() <= maxWordLen)) {
      int index = (int) (Math.random() * wordList.length);
      word = wordList[index];
    }

    return word;
  }

  public static boolean mcGamesMessage(ReceiveMessageEvent event, String title) {
    return event.getMessage().getString().startsWith(String.format("[MC Games] [%s]", title));
  }

  public static String calculateDuration(LocalTime start) {
    LocalTime end = LocalTime.now();

    Duration duration = Duration.between(start, end);
    long seconds = duration.getSeconds();

    long hours = seconds / 3600;
    long minutes = (seconds % 3600) / 60;
    long secs = seconds % 60;

    String time = String.format("%d hours, %d minutes, %d seconds", hours, minutes, secs);

    return time;
  }
}
