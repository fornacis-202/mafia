package com.company;

import java.util.ArrayList;

public class Game {
    private ArrayList<Player> players;
    private ArrayList<Player> mafias;
    private static Game instance;
    private Controller controller;

    private Game() {
        players = new ArrayList<>();
        controller = Controller.getInstance();
        mafias=new ArrayList<>();
    }

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }
    public void start(){
        initialMafias();
        introductionNight();
        String nightEvent=null;
        while (true){
            day(nightEvent);


        }

    }
    private void day(String nightEvent){
        controller.sendToAll(ConsoleColor.BLUE_BOLD + "change to "+ ConsoleColor.GREEN_BRIGHT + "Day!");
        if(nightEvent!=null && !nightEvent.equals("")){
            controller.sendToAll(ConsoleColor.BLUE_BOLD + "Night event:\n" + ConsoleColor.PURPLE + nightEvent);
            sleep(5);
        }
        //check game end
        new ChatRoom(5,players).start();
        Player player = new DayVoting(players,30).start();
        mayorOperation(player);


    }

    public void mayorOperation(Player player){
        Player mayor;
        if((mayor=roleFinder(Role.MAYOR))!=null&& mayor.isAlive() && player!=null && !player.getRole().equals(Role.MAYOR) ){
            controller.send(mayor,ConsoleColor.YELLOW + player.getName() + ConsoleColor.CYAN + " is going to be kicked out ,do you want to cancel it?\n1)Yes\n2)No");
            Integer num=controller.receiveInt(mayor,1,2);
            if(!(num==null) && num==1){
                controller.sendToAll(ConsoleColor.PURPLE + "MAYOR" + ConsoleColor.BLUE_BOLD + " canceled voting!");
            }else {
                killInDay(player);
            }
        }else if(player!=null){
            killInDay(player);
        }
    }
    public void killInDay(Player player){
        player.setAlive(false);
        controller.sendToAll(ConsoleColor.YELLOW + player.getName()+ ConsoleColor.BLUE_BOLD + " was kicked out!");
        controller.send(player,ConsoleColor.BLUE_BOLD + "You were kicked out of the game :( \n you can still spectate the game or you can type\"exit\"to leave the game. ");
        mafias.remove(player);
    }

    private void introductionNight(){
        controller.sendToAll(ConsoleColor.BLUE_BOLD + "change to "+ ConsoleColor.BLACK_BRIGHT + "Introduction Night!");
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
            Thread.sleep(seconds* 1000);
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
            if(player.getRole().equals(Role.GOD_FATHER)||player.getRole().equals(Role.DOCTOR_LECTER)||player.getRole().equals(Role.MAFIA)){
                mafias.add(player);
            }
        }

    }

    public Player roleFinder(Role role){
        for(Player player : players){
            if(player.getRole().equals(role) && player.isAlive()){
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



