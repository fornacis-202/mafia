package com.company;

import javax.imageio.IIOException;
import java.io.IOException;
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

    public void send(Player player,String string){
        try {
            playerStreamsMap.get(player).getOut().writeObject(string);
        }catch (IOException e){
            //should be added sth
        }
    }
    public String receiveString(Player player){
        try {
           return(String) playerStreamsMap.get(player).getIn().readObject();
        }catch (IOException |ClassNotFoundException e){
            return null;
            //should be added sth
        }
    }
    public int receiveInt(Player player , int min , int max){
        String num;
        int number;
        while (true){
            num=receiveString(player);
            try {
                number  = Integer.parseInt(num);
                if(number>=min && number<=max){
                    return number;
                }else {
                    send(player,ConsoleColor.BLUE_BOLD + "please enter a valid number");
                }
            }catch (NumberFormatException e){
                send(player,ConsoleColor.BLUE_BOLD + "please enter a valid number");
            }
        }
    }

    public void sendToAll(String string){
        for (Player player : playerStreamsMap.keySet()){
            send(player,string);
        }
    }

    public HashMap<Player, Streams> getPlayerStreamsMap() {
        return playerStreamsMap;
    }
}
