package com.company;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * The type Sender, sends everything the client rights to server
 */
public class Sender implements Runnable{
    private ObjectOutputStream out;
    private Socket socket;

    /**
     * Instantiates a new Sender.
     *
     * @param out    the output stream
     * @param socket the socket
     */
    public Sender(ObjectOutputStream out , Socket socket){
        this.out=out;
        this.socket = socket;
    }
    @Override
    public void run() {
        Scanner sc =new Scanner(System.in);
        try {
            String line;
            while (true){
               line = sc.nextLine();
               if(line.trim().equals("exit")){
                   break;
               }
               out.writeObject(line);
            }
        } catch (IOException e) {

        }finally {
            System.out.println(ConsoleColor.RESET+"you got disconnected");
            try {
                if(!socket.isClosed())
                    socket.close();
            } catch (IOException ee) {
                //nothing
            }
            System.exit(0);
        }

    }
}
