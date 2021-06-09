package com.company;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Receiver implements Runnable{
    private ObjectInputStream in;
    private Socket socket;
    public Receiver(ObjectInputStream in , Socket socket){
        this.in=in;
        this.socket = socket;
    }
    @Override
    public void run() {
        Scanner sc =new Scanner(System.in);
        try {
            String line;
            while (true){
                line = (String) in.readObject();
                if(!(line.equals(""))){
                    System.out.println(line);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(ConsoleColor.RESET+"you got disconnected");
            System.exit(0);
        }
        finally {
            try {
                if(!socket.isClosed())
                     socket.close();
            } catch (IOException e) {
                //nothing
            }

        }
    }
}
