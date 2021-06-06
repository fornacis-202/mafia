package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
}
