package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameStarter {
    public void join(int num){
        try {
            //connecting to client
            ServerSocket welcomingSocket = new ServerSocket(8080);
            ExecutorService executorService = Executors.newCachedThreadPool();
            while (Controller.getInstance().getSize()<num){
                Socket socket = welcomingSocket.accept();
                executorService.execute(new PlayerBuilder(socket,num));

            }
            welcomingSocket.close();
            executorService.shutdown();
            while (!(executorService.isTerminated()));

        }catch (IOException e){
            System.out.println("welcoming socket closed");

        }

    }
    public void waitForReady(){
        Controller.getInstance().sendToAll(ConsoleColor.BLUE_BOLD + "send 1 if you are ready ");
        HashMap<Player,Integer> playerIntegerHashMap=Controller.getInstance().receiveIntFromAll(1,1);
        System.out.println("finished");
    }

    public static void main(String[] args) {
        System.out.println("enter the number of the player: ");
        int num;
        while (true){
            Scanner scanner = new Scanner(System.in);
            num = scanner.nextInt();
            if(num>=4)
                break;
            else
                System.out.println("please enter a valid number");
        }
        GameStarter gameStarter = new GameStarter();
        Controller.getInstance().setMaxNum(num);
        gameStarter.join(num);
        new RoleGenerator(num).start();
        gameStarter.waitForReady();

    }
}
