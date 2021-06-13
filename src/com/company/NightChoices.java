package com.company;

import java.util.ArrayList;

/**
 * The type Night choices , does the ask for player who is going to be choosed at night
 */
public class NightChoices {
    private  Player player;
    private String consoleColor;
    private ArrayList<Player> players;

    /**
     * Instantiates a new Night choices.
     *
     * @param players      the players in the choices
     * @param player       the player who is going to chose
     * @param consoleColor the console color
     */
    public NightChoices(ArrayList<Player> players , Player player, String consoleColor){
        this.consoleColor=consoleColor;
        this.player=player;
        this.players=players;
    }

    /**
     * Start player.
     *
     * @param show to show the player who is choosing in the options or not
     * @return the player
     */
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
        try {
            Controller.getInstance().send(player,options);
            Integer num = Controller.getInstance().receiveInt(player,1,optionPlayers.size());
            System.out.println("num is : "+num);
            if(num !=null)
                return optionPlayers.get(num-1);
            else
                return null;
        }
        catch (NullPointerException e){
            return null;
        }
    }
}
