package com.company;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
            executorService.invokeAll(callables, time, TimeUnit.MINUTES);
            executorService.shutdownNow();
        } catch (InterruptedException e) {
            //nothing yet
        }
    }
}
