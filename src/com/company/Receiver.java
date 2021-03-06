package com.company;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * The type Receiver , receives data from server and print them for the client
 */
public class Receiver implements Runnable{
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket socket;

    /**
     * Instantiates a new Receiver.
     *
     * @param in     the input stream
     * @param socket the socket
     * @param out    the output stream
     */
    public Receiver(ObjectInputStream in , Socket socket , ObjectOutputStream out){
        this.in=in;
        this.socket = socket;
        this.out=out;
    }
    @Override
    public void run() {
        Scanner sc =new Scanner(System.in);
        try {
            String line;
            while (true){
                line = (String) in.readObject();
                if(line.equals("#send#")){
                    out.writeObject(null);
                }
                else if(!(line.equals(""))){
                    System.out.println(line);
                }
            }
        } catch (IOException | ClassNotFoundException e) {

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
