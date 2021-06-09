package com.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DayVoting {
    private ArrayList<Player> players;
    private int seconds;
    Controller controller;

    public DayVoting(ArrayList<Player> players,int seconds){
        for(Player player : players){
            if(player.isAlive()){
                this.players.add(player);
            }
        }
        this.seconds=seconds;
        controller=Controller.getInstance();
    }

    public Player start(){
        sendOptions();
        HashMap<Player , Integer> playerIntegerHashMap = receiveVotes();
        sendResult(playerIntegerHashMap);
        return result(new ArrayList<Integer>(playerIntegerHashMap.values()));


    }
    private void sendOptions(){
        int i = 1;
        String options =ConsoleColor.BLUE_BOLD+  "vote someone to be kicked out!\nVoting will last for "+seconds+" seconds !\n";
        for (Player player : players){
            options+=ConsoleColor.BLUE_BOLD + i+ ")" + ConsoleColor.YELLOW + player.getName() + "\n";
            i++;
        }
        controller.sendToAll(options);
    }
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
                        if (num == null)
                            return null;
                        playerIntegerHashMap.put(player, num);

                    }
                }
            });
        }
        try {
            executorService.invokeAll(callables, seconds, TimeUnit.SECONDS);
            return playerIntegerHashMap;
        } catch (InterruptedException e) {
            //nothing yet
            return null;
        }
    }
    private Player result(ArrayList<Integer> votes){
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
