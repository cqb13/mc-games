package dev.cqb13.McGames.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GameUtils {
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
}
