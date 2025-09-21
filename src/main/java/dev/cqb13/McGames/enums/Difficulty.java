package dev.cqb13.McGames.enums;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public enum Difficulty {
  Easy,
  Normal,
  Hard;

  public Text getStyledDifficulty() {
    MutableText difficultyText = Text.empty();

    switch (this) {
      case Difficulty.Easy:
        difficultyText.setStyle(difficultyText.getStyle().withBold(true).withFormatting(Formatting.BLUE));
        difficultyText.append("EASY");
        break;
      case Difficulty.Normal:
        difficultyText.setStyle(difficultyText.getStyle().withBold(true).withFormatting(Formatting.GREEN));
        difficultyText.append("NORMAL");
        break;
      case Difficulty.Hard:
        difficultyText.setStyle(difficultyText.getStyle().withBold(true).withFormatting(Formatting.RED));
        difficultyText.append("HARD");
        break;
    }

    return difficultyText;
  }
}
