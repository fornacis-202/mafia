package com.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ChatRoom {
    private int time;
    private ArrayList<Player> players;
    public ChatRoom(int time , ArrayList<Player> players){
        this.time=time;
        this.players=players;
    }
    public void start(){
        Controller controller = Controller.getInstance();
        controller.sendToAll(ConsoleColor.BLUE_BOLD+"ChatRoom started!\nyou can send message by typing it an pressing enter.\nsend \"ready\" whenever you are ready to vote.\nthis chat room will last for "+time+" minutes.");
        ExecutorService executorService = Executors.newCachedThreadPool();
        HashMap<Player, Integer> playerIntegerHashMap = new HashMap<>();
        HashSet<Callable<Boolean>> callables = new HashSet<>();

        for (Player player : players) {
            callables.add(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    String message;
                    while (true){
                        message=controller.receiveString(player);
                        if(message==null){
                            return null;
                        }else if(message.trim().equals("ready")){
                            controller.sendToAll(ConsoleColor.YELLOW +player.getName()+ConsoleColor.BLUE_BOLD + "is ready to vote!");
                            return null;
                        }
                        else {
                            controller.sendToAll(ConsoleColor.YELLOW +player.getName()+ ":" +message);
                        }

                    }
                }
            });
        }
        try {
            executorService.invokeAll(callables, time, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            //nothing yet
        }
    }
}
