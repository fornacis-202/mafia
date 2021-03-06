package com.company;

import java.io.IOException;
import java.net.Socket;

/**
 * The type Player builder .
 */
public class PlayerBuilder implements Runnable{
    private Socket socket ;
    private int num;

    /**
     * Instantiates a new Player builder.
     *
     * @param socket the socket
     * @param num    the num
     */
    public PlayerBuilder(Socket socket,int num){
        this.socket = socket;
        this.num=num;

    }

    /**
     * ask the player name and add it to Controller if possible
     */
    @Override
    public void run() {
        try {
            Streams streams = new Streams(socket);
            String name;
            while (true){
                streams.getOut().writeObject(ConsoleColor.BLUE_BOLD + "write your name : ");
                name= (String) streams.getIn().readObject();
                name= "Ɨ" + name.trim() + "Ɨ";
                Player player = new Player(name);
                try {
                Controller.getInstance().addPlayer(player,streams);
                    streams.getOut().writeObject(ConsoleColor.BLUE_BOLD + "waiting for others to join...");
                    ConnectionChecker.getInstance().addPlayer(player,streams);
                    return;
                }catch (NameExistsException e){
                    streams.getOut().writeObject(ConsoleColor.BLUE_BOLD + "this player already exists ");

                }catch (RoomIsFullException e){
                    streams.getOut().writeObject(ConsoleColor.BLUE_BOLD + "room is full! ");
                    socket.close();
                    return;

                }

            }

        } catch (IOException | ClassNotFoundException e) {
           //nothing
        }

    }
}
