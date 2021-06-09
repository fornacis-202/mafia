package com.company;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionChecker {
    private static ConnectionChecker instance = null;
    private ExecutorService executorService;


    private ConnectionChecker(){
        executorService = Executors.newCachedThreadPool();
    }

    public static ConnectionChecker getInstance() {
        if(instance==null){
            instance = new ConnectionChecker();
        }
        return instance;
    }

    public void addPlayer(Player player,Streams streams){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true){
                        streams.getOut().writeObject("");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }catch (IOException  e){
                    deletePlayerEverywhere(player);
                }
            }
        });
    }

    public void deletePlayerEverywhere(Player player){
        Controller.getInstance().removePlayer(player);
        Game.getInstance().removePlayer(player);
        Controller.getInstance().sendToAll(ConsoleColor.YELLOW+ player.getName() +ConsoleColor.BLUE_BOLD +" left the match.");
    }


}
