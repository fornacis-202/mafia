package com.company;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class ChatRoom {
    private int time;
    private ArrayList<Player> players;
    private String color;
    public ChatRoom(int time , ArrayList<Player> players , String color){
        this.time=time;
        this.players=players;
        this.color=color;
    }
    public void start(){
        Controller controller = Controller.getInstance();
        controller.sendToGroup(players,color+"ChatRoom started!\nYou can send message by typing it an pressing enter.\nSend \"ready\" Whenever you are done chatting.\nThis chat room will last for "+time+" minutes.");
        ExecutorService executorService = Executors.newCachedThreadPool();
        HashMap<Player, Integer> playerIntegerHashMap = new HashMap<>();
        HashSet<Callable<Boolean>> callables = new HashSet<>();
        ArrayList<Player> readyPlayers = new ArrayList<>();

        for (Player player : players) {
            if(player.isAlive() && !player.isMuted()) {
                callables.add(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        String message;
                        while (true) {
                            message = controller.receiveString(player);
                            if(Thread.currentThread().isInterrupted()){
                                return null;
                            }
                            if (message == null) {
                                return null;
                            } else if (message.trim().equals("ready")) {
                                controller.sendToGroup(players,ConsoleColor.YELLOW + player.getName() + color + " is ready!");
                                readyPlayers.add(player);
                                return null;
                            } else {
                                controller.sendToGroup(players,ConsoleColor.YELLOW + player.getName() + ":" + ConsoleColor.YELLOW_BRIGHT + message);
                            }

                        }
                    }
                });
            }
        }
        try {
            List<Future<Boolean>>future = executorService.invokeAll(callables, time, TimeUnit.MINUTES);
            executorService.shutdownNow();
            for (Player player:players){
                if(!readyPlayers.contains(player))
                    controller.send(player,"#send#");
            }
        } catch (InterruptedException e ) {
            e.printStackTrace();
        }
    }
}
