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
               out.writeObject(line);
            }
        } catch (IOException e) {
            System.out.println("output disconnected");
        }
        finally {
            try {
                out.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("can not close streams");
            }

        }
    }
}