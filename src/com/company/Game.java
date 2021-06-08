package com.company;

import java.util.ArrayList;

public class Game {
    private ArrayList<Player> players;
    private ArrayList<Player> mafias;
    private static Game instance;
    private Controller controller;

    private Game() {
        players = new ArrayList<>();
    }

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }
    public void start(){
        initialMafias();

        while (true){

        }

    }

    public void introductionNight(){
        controller.sendToAll(ConsoleColor.BLUE_BOLD + "change to "+ ConsoleColor.BLACK_BRIGHT + "Introduction Night");
        sendToMafias(ConsoleColor.RED+mafiasRoles());
        Player mayor;
        Player doctor;
        if(( mayor = roleFinder(Role.MAYOR)) !=null  && (doctor = roleFinder(Role.DOCTOR))!=null){
            controller.send(mayor,ConsoleColor.YELLOW+doctor.getName()+ConsoleColor.BLUE_BOLD + " is DOCTOR!");
        }
        sleep(10);


    }



    public void sendToMafias(String string){
        for (Player player : mafias){
            controller.send(player,string);
        }
    }

    public void sleep(int seconds){
        try {
            Thread.sleep(seconds);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    public String mafiasRoles(){
        String mafia= "";
        for (Player player : mafias){
            mafia+=player.getName()+"\t" + player.getRole();
        }
        return mafia;
    }

    private void initialMafias(){
        for (Player player : players){
            if(player.getRole()==Role.GOD_FATHER||player.getRole()==Role.DOCTOR_LECTER||player.getRole()==Role.MAFIA){
                mafias.add(player);
            }
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public Player roleFinder(Role role){
        for(Player player : players){
            if(player.getRole()==role && player.isAlive()){
                return player;
            }
        }
        return null;
    }
    public void addPlayer(Player player){
        players.add(player);
    }
    public void removePlayer(Player player){
        players.remove(player);
    }

}



