package com.company;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The type Day voting.
 */
public class DayVoting {
    private ArrayList<Player> players;
    private int seconds;
    /**
     * The Controller.
     */
    Controller controller;

    /**
     * Instantiates a new Day voting.
     *
     * @param players the players
     * @param seconds the time limit
     */
    public DayVoting(ArrayList<Player> players,int seconds){
        this.players=new ArrayList<>();
        for(Player player : players){
            if(player.isAlive()){
                this.players.add(player);
            }
        }
        this.seconds=seconds;
        controller=Controller.getInstance();
    }

    /**
     * Starts the voting.
     *
     * @return the player
     */
    public Player start(){
        sendOptions();
        HashMap<Player , Integer> playerIntegerHashMap = receiveVotes();
        sendResult(playerIntegerHashMap);
        return result(new ArrayList<Integer>(playerIntegerHashMap.values()));


    }

    /**
     * send options to the players
     */
    private void sendOptions(){
        int i = 1;
        String options =ConsoleColor.BLUE_BOLD+  "vote someone to be kicked out!\nVoting will last for "+seconds+" seconds !\n";
        for (Player player : players){
            options+=ConsoleColor.BLUE_BOLD + i+ ")" + ConsoleColor.YELLOW + player.getName() + "\n";
            i++;
        }
        controller.sendToAll(options);
    }

    /**
     * receive votes from clients
     * @return a hash map containing players and votes
     */
    private HashMap<Player,Integer> receiveVotes(){
        ExecutorService executorService = Executors.newCachedThreadPool();
        HashMap<Player, Integer> playerIntegerHashMap = new HashMap<>();
        HashSet<Callable<Boolean>> callables = new HashSet<>();
        for (Player player : players) {
            callables.add(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    Integer num;
                    while (true) {
                        num = controller.receiveInt(player, 1, players.size());
                        if(Thread.currentThread().isInterrupted()){
                            return null;
                        }
                        if (num == null)
                            return null;
                        playerIntegerHashMap.put(player, num);

                    }
                }
            });
        }
        try {
            executorService.invokeAll(callables, seconds, TimeUnit.SECONDS);
            executorService.shutdownNow();
            controller.sendToGroup(players,"#send#");
            return playerIntegerHashMap;
        } catch (InterruptedException e) {
            //nothing yet
            return null;
        }
    }

    /**
     * calculate the result of the voting
     * @param votes
     * @return the player who has the highest vote
     */
    private Player result(ArrayList<Integer> votes){
        if(votes.size()==0)
            return null;
        HashMap<Integer, Integer> map = new HashMap<>();

        for (Integer integer : votes) {
            Integer val = map.get(integer);
            map.put(integer, val == null ? 1 : val + 1);
        }

        Map.Entry<Integer, Integer> max = null;

        for (Map.Entry<Integer, Integer> e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue())
                max = e;
        }

        return players.get(max.getKey()-1);
    }

    /**
     * prints the result of the voting
     * @param playerIntegerHashMap
     */
    private void sendResult(HashMap<Player,Integer> playerIntegerHashMap){
        String result="";
        for (Player player : playerIntegerHashMap.keySet()){
            result+=ConsoleColor.YELLOW + player.getName() + ConsoleColor.BLUE_BOLD + " voted to " + ConsoleColor.YELLOW + players.get(playerIntegerHashMap.get(player)-1).getName()+"\n";
        }
        if(result.equals("")){
            result=ConsoleColor.BLUE_BOLD + "Nobody voted!\n";
        }
        controller.sendToAll(result);
    }
}
