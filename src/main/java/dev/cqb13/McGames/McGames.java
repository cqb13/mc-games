package dev.cqb13.McGames;

import dev.cqb13.McGames.modules.*;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class McGames extends MeteorAddon {
  public static final Logger LOG = LogUtils.getLogger();
  public static final Category CATEGORY = new Category("MC games");

  @Override
  public void onInitialize() {
    LOG.info("Initializing MC Games...");

    // Modules
    LOG.info("Adding modules...");
    Modules modules = Modules.get();
    modules.add(new Hangman());
    modules.add(new Navigator());
    modules.add(new Wordle());
    modules.add(new TicTacToe());

    LOG.info("Initialized MC Games.");
  }

  @Override
  public void onRegisterCategories() {
    Modules.registerCategory(CATEGORY);
  }

  @Override
  public String getPackage() {
    return "dev.cqb13.McGames";
  }

  @Override
  public GithubRepo getRepo() {
    return new GithubRepo("cqb13", "mc-games");
  }
}
