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
                        streams.getIn().readObject();
                    }
                }catch (IOException | ClassNotFoundException e){
                    deletePlayerEverywhere(player);
                }
            }
        });
    }

    public void deletePlayerEverywhere(Player player){
        Controller.getInstance().removePlayer(player);
        Game.getInstance().removePlayer(player);
        Controller.getInstance().sendToAll(ConsoleColor.BLUE_BOLD + player.getName() + " left the match.");
    }


}
