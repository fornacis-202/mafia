package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The type Game starter , starts server
 */
public class GameStarter {
    /**
     * wait for the players to join the sever
     *
     * @param num the num
     */
    public void join(int num){
        try {
            //connecting to client
            ServerSocket welcomingSocket = new ServerSocket(8080);
            ExecutorService executorService = Executors.newCachedThreadPool();
            Controller.getInstance().setWelcomingSocket(welcomingSocket);
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

    /**
     * Initial the class Game to be ready to start
     */
    public void initialGame(){
        for (Player player:Controller.getInstance().getPlayerStreamsMap().keySet()){
            Game.getInstance().addPlayer(player);
        }

    }

    /**
     * Wait for clients to send ready
     */
    public void waitForReady(){
        Controller.getInstance().sendToAll(ConsoleColor.BLUE_BOLD + "send 1 if you are ready ");
        HashMap<Player,Integer> playerIntegerHashMap=Controller.getInstance().receiveIntFromAll(1,1);
        System.out.println("finished");
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        System.out.println("enter the number of the player: ");
        int num;
        while (true){
            Scanner scanner = new Scanner(System.in);
            num = scanner.nextInt();
            if(num>=2)
                break;
            else
                System.out.println("please enter a valid number");
        }
        GameStarter gameStarter = new GameStarter();
        Controller.getInstance().setMaxNum(num);

        gameStarter.join(num);
        new RoleGenerator(num).start();
        gameStarter.waitForReady();
        gameStarter.initialGame();
        Game.getInstance().start();

    }
}
