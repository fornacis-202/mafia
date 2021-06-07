package com.company;

import java.util.ArrayList;

public class Game {
    private ArrayList<Player> players;
    private static Game instance;

    private Game() {
        players = new ArrayList<>();
    }

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public void addPlayer(Player player){
        players.add(player);
    }
    public void removePlayer(Player player){
        players.remove(player);
    }

}



