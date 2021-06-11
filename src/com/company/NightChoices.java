package com.company;

import java.util.ArrayList;

public class NightChoices {
    private  Player player;
    private String consoleColor;
    private ArrayList<Player> players;
    public NightChoices(ArrayList<Player> players , Player player, String consoleColor){
        this.consoleColor=consoleColor;
        this.player=player;
        this.players=players;
    }

    public Player start(boolean show){
        ArrayList<Player> optionPlayers=new ArrayList<>();
        String options="";
        int i=1;
        for(Player player1:players){
            if(player1.isAlive()){
                if(!(player.equals(player1)) || show) {
                    optionPlayers.add(player1);
                    options += consoleColor + i + ")" + ConsoleColor.YELLOW + player1.getName() + "\n";
                    i++;
                }
            }
        }
        Controller.getInstance().send(player,options);
        Integer num = Controller.getInstance().receiveInt(player,1,optionPlayers.size());
        if(num !=null)
            return optionPlayers.get(num-1);
        else
            return null;
    }
}