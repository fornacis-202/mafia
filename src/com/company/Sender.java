package com.company;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Sender implements Runnable{
    private ObjectOutputStream out;
    private Socket socket;
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
               out.reset();
               out.writeObject(line);
            }
        } catch (IOException e) {
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
