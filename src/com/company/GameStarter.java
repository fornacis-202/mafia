package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameStarter {
    public void join(int num){
        try {
            //connecting to client
            ServerSocket welcomingSocket = new ServerSocket(8080);
            ExecutorService executorService = Executors.newCachedThreadPool();
            for (int i = 0 ; i < num ; i++){
                Socket socket = welcomingSocket.accept();
                executorService.execute(new PlayerBuilder(socket));

            }

        }catch (IOException e){
            System.out.println("no connection");

        }

    }

    public static void main(String[] args) {
        System.out.println("enter the number of the player: ");
        int num;
        while (true){
            Scanner scanner = new Scanner(System.in);
            num = scanner.nextInt();
            if(num>=8)
                break;
            else
                System.out.println("please enter a valid number");
        }
        new GameStarter().join(num);
        new RoleGenerator(num).start();
    }
}
