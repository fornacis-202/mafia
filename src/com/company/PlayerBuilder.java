package com.company;

import java.io.IOException;
import java.net.Socket;

public class PlayerBuilder implements Runnable{
    private Socket socket ;
    public PlayerBuilder(Socket socket){
        this.socket = socket;

    }
    @Override
    public void run() {
        try {
            Streams streams = new Streams(socket);
            String name;
            while (true){
                streams.getOut().writeObject(ConsoleColor.BLUE_BOLD + "write your name : ");
                name= (String) streams.getIn().readObject();
                name=name.trim();
                Player player = new Player(name);
                if(Controller.getInstance().addPlayer(player,streams)){
                    streams.getOut().writeObject(ConsoleColor.BLUE_BOLD + "waiting for others to join...");
                    break;
                }else {
                    streams.getOut().writeObject(ConsoleColor.BLUE_BOLD + "this player already exists ");

                }

            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
