package org.example;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main{

    public static void main(String[] args) {
        GameRestClient gameRestClient = new GameRestClient("http://localhost:8080/api");
        gameRestClient.getGames();
        gameRestClient.getOneGame();
        JSONObject object = new JSONObject();
        object.put("id", 11);
        object.put("team1", "Rapid");
        object.put("team2", "U Cluj");
        object.put("team1Score", 3);
        object.put("team2Score", 0);
        object.put("competition", "Supercupa");
        object.put("capacity", 100);
        object.put("stage", "Finals");
        object.put("ticketPrice", 10.99);
        String response = gameRestClient.addGame(object.toString());
        if (response != null) {
            JSONObject object1 = new JSONObject(response);
            int id = object1.getInt("id");
            object.put("ticketPrice", 25.99);
            gameRestClient.updateGame(id, object.toString());

            try {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Delete added game? (1 for yes, 0 for no)");
                int yes = scanner.nextInt();
                if (yes == 1) {
                    gameRestClient.deleteGame(id);
                } else {
                    System.out.println("no");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}