package com.company;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The type Client.
 */
public class Client {

    /**
     * Start.
     */
    public void start()  {
        String port;
        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("enter port");
            port = scanner.nextLine().trim();

            try  {
                Socket socket = new Socket("127.0.0.1", Integer.parseInt(port));
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                ExecutorService executorService = Executors.newCachedThreadPool();
                executorService.execute(new Sender(out,socket));
                executorService.execute(new Receiver(in , socket ,out));
                break;

            } catch (Exception e) {
                System.out.println("can not connect to this server");
            }
        }
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        new Client().start();
    }

}
