package com.company;

import java.util.HashMap;

public class Controller {
    private static Controller instance = null;
    private HashMap<Player,Streams> playerStreamsMap;

    private Controller(){
        playerStreamsMap = new HashMap<>();
    }

    public static Controller getInstance() {
        if(instance==null){
            instance = new Controller();
        }
        return instance;
    }

    public synchronized boolean addPlayer(Player player , Streams streams){
          for(Player player1 : playerStreamsMap.keySet()){
              if(player.getName().equals(player1.getName()))
                  return false;
          }
          playerStreamsMap.put(player,streams);
          return true;
    }
}
