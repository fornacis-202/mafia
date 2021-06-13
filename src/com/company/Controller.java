package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The type Controller , responsible for send and receive data from client
 */
public class Controller {
    private static Controller instance = null;
    private HashMap<Player, Streams> playerStreamsMap;
    private int maxNum;
    private ServerSocket welcomingSocket;

    private Controller() {
        playerStreamsMap = new HashMap<>();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    /**
     * Sets welcoming socket.
     *
     * @param welcomingSocket the welcoming socket
     */
    public void setWelcomingSocket(ServerSocket welcomingSocket) {
        this.welcomingSocket = welcomingSocket;
    }

    /**
     * Sets max num.
     *
     * @param num the num
     */
    public void setMaxNum(int num) {
        maxNum = num;
    }

    /**
     * Gets size.
     *
     * @return the size
     */
    public int getSize() {
        return playerStreamsMap.size();
    }

    /**
     * Add player.
     *
     * @param player  the player
     * @param streams the streams
     * @throws RoomIsFullException the room is full exception
     * @throws NameExistsException the name exists exception
     */
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

    /**
     * Send a string to a player
     *
     * @param player the player
     * @param string the string
     */
    public void send(Player player, String string) {
        try {
            playerStreamsMap.get(player).getOut().writeObject(string);
        } catch (IOException e) {
            //should be added sth
        }
    }

    /**
     * Send to group.
     *
     * @param group  the group
     * @param string the string
     */
    public void sendToGroup(ArrayList<Player> group, String string){
        for (Player player : group) {
            send(player, string);
        }
    }

    /**
     * Receive string from a player
     *
     * @param player the player
     * @return the string
     */
    public String receiveString(Player player) {
        try {
            return (String) playerStreamsMap.get(player).getIn().readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
            //should be added sth
        }
    }

    /**
     * Close players socket
     */
    public void closeEverything(){
        for (Player player : playerStreamsMap.keySet()){
            playerStreamsMap.get(player).close();
        }
    }

    /**
     * Receive int integer.
     *
     * @param player the player
     * @param min    the min
     * @param max    the max
     * @return the integer
     */
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

    /**
     * Receive int from all hash map without time limit
     *
     * @param min the min
     * @param max the max
     * @return the hash map
     */
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

    /**
     * Send a string to all.
     *
     * @param string the string
     */
    public void sendToAll(String string) {
        for (Player player : playerStreamsMap.keySet()) {
            send(player, string);
        }
    }

    /**
     * Remove player.
     *
     * @param player the player
     */
    public void removePlayer(Player player) {
        playerStreamsMap.remove(player);
    }

    /**
     * Gets player streams map.
     *
     * @return the player streams map
     */
    public HashMap<Player, Streams> getPlayerStreamsMap() {
        return playerStreamsMap;
    }
}
