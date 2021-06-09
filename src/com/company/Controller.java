package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Controller {
    private static Controller instance = null;
    private HashMap<Player, Streams> playerStreamsMap;
    private int maxNum;
    private ServerSocket welcomingSocket;

    private Controller() {
        playerStreamsMap = new HashMap<>();
    }

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    public void setWelcomingSocket(ServerSocket welcomingSocket) {
        this.welcomingSocket = welcomingSocket;
    }

    public void setMaxNum(int num) {
        maxNum = num;
    }

    public int getSize() {
        return playerStreamsMap.size();
    }

    public synchronized void addPlayer(Player player, Streams streams) throws RoomIsFullException, NameExistsException {
        for (Player player1 : playerStreamsMap.keySet()) {
            if (getSize() >= maxNum) {
                try {
                    welcomingSocket.close();
                } catch (IOException e) {
                    //nothing yet
                }
                throw new RoomIsFullException();
            }

            if (player.getName().equals(player1.getName()))
                throw new NameExistsException();
        }
        playerStreamsMap.put(player, streams);
        if (getSize() >= maxNum) {
            try {
                welcomingSocket.close();
            } catch (IOException e) {
                //nothing yet
            }
        }

    }

    public void send(Player player, String string) {
        try {
            playerStreamsMap.get(player).getOut().writeObject(string);
        } catch (IOException e) {
            //should be added sth
        }
    }

    public String receiveString(Player player) {
        try {
            //playerStreamsMap.get(player).getIn()
            return (String) playerStreamsMap.get(player).getIn().readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
            //should be added sth
        }
    }

    public Integer receiveInt(Player player, int min, int max) {
        String num;
        int number;
        while (true) {
            num = receiveString(player);
            if (num == null) {
                return null;
            }
            try {
                number = Integer.parseInt(num);
                if (number >= min && number <= max) {
                    return number;
                } else {
                    send(player, ConsoleColor.BLUE_BOLD + "please enter a valid number");
                }
            } catch (NumberFormatException e) {
                send(player, ConsoleColor.BLUE_BOLD + "please enter a valid number");
            }
        }
    }

    public HashMap<Player, Integer> receiveIntFromAll(int min, int max, int seconds) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        HashMap<Player, Integer> playerIntegerHashMap = new HashMap<>();
        HashSet<Callable<Boolean>> callables = new HashSet<>();
        for (Player player : playerStreamsMap.keySet()) {
            callables.add(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {

                    Integer num = receiveInt(player, min, max);
                    if (num == null)
                        return null;
                    playerIntegerHashMap.put(player, num);
                    return null;
                }
            });
        }
        try {
            executorService.invokeAll(callables, seconds, TimeUnit.SECONDS);
            return playerIntegerHashMap;
        } catch (InterruptedException e) {
            //nothing yet
            return null;
        }

    }

    public HashMap<Player, Integer> receiveIntFromAll(int min, int max) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        HashMap<Player, Integer> playerIntegerHashMap = new HashMap<>();
        HashSet<Callable<Boolean>> callables = new HashSet<>();
        for (Player player : playerStreamsMap.keySet()) {
            callables.add(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {

                    Integer num = receiveInt(player, min, max);
                    if (num == null)
                        return null;
                    playerIntegerHashMap.put(player, num);
                    return null;
                }
            });
        }
        try {
            executorService.invokeAll(callables);
            return playerIntegerHashMap;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void sendToAll(String string) {
        for (Player player : playerStreamsMap.keySet()) {
            send(player, string);
        }
    }

    public void removePlayer(Player player) {
        playerStreamsMap.remove(player);
    }

    public HashMap<Player, Streams> getPlayerStreamsMap() {
        return playerStreamsMap;
    }
}
