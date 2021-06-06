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
                System.out.println(line);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            try {
                in.close();
                if(!socket.isClosed())
                     socket.close();
            } catch (IOException e) {
                System.out.println("can not close streams");
            }

        }
    }
}
