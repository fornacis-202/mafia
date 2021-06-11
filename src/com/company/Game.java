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
        mafias = new ArrayList<>();
    }

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public void start() {
        initialMafias();
        introductionNight();
        String nightEvent = null;
        while (true) {
            day(nightEvent);
            nightEvent=night();
        }

    }

    private void day(String nightEvent) {
        controller.sendToAll(ConsoleColor.BLUE_BOLD + "Change to " + ConsoleColor.CYAN_BRIGHT + "Day!");
        if (nightEvent != null && !nightEvent.equals("")) {
            controller.sendToAll(ConsoleColor.BLUE_BOLD + "Night event:\n" + ConsoleColor.PURPLE + nightEvent);
            sleep(5);
        }
        endGame();
        new ChatRoom(5, players,ConsoleColor.CYAN).start();
        Player player = new DayVoting(players, 30).start();
        mayorOperation(player);
        endGame();


    }

    private String night() {
        controller.sendToAll(ConsoleColor.BLUE_BOLD + "Change to " + ConsoleColor.BLUE_BRIGHT + "Night!");
        int aliveMafia=0;
        for (Player player : mafias){
            if(player.isAlive())
                aliveMafia++;
        }
        if(aliveMafia>1)
            new ChatRoom(1,mafias,ConsoleColor.RED).start();
        Player mafiaShot=mafiaShoot();
        Player lectorChoice = lectorOperation();
        Player doctorChoice = doctorOperation();
        detectiveOperation();
        Player sniperChoice = sniperOperation();
        Player sniper = roleFinder(Role.SNIPER);
        Player psychologistChoice = psychologistOperation();
        boolean armoredChoice = armoredOperation();
        return calculateResult(mafiaShot,lectorChoice,doctorChoice,sniperChoice,sniper,psychologistChoice,armoredChoice);

    }
    private String calculateResult(Player mafiaShot,Player lectorChoice,Player doctorChoice,Player sniperChoice,Player sniper,Player psychologistChoice,boolean armoredChoice){
        String result = "";
        if(mafiaShot!=null && !mafiaShot.getRole().equals(Role.ARMORED) && !mafiaShot.equals(doctorChoice)){
            killAtNight(mafiaShot);
            result += ConsoleColor.YELLOW + mafiaShot.getName() +ConsoleColor.CYAN_BOLD +" was killed last night.\n";
        }
        if(sniperChoice!=null){
            if((sniperChoice.getRole().equals(Role.GOD_FATHER) || sniperChoice.getRole().equals(Role.DOCTOR_LECTER) || sniperChoice.equals(Role.MAFIA))){
                if(!sniperChoice.equals(lectorChoice)){
                    killAtNight(sniperChoice);
                    result += ConsoleColor.YELLOW + sniperChoice.getName() +ConsoleColor.CYAN_BOLD +" was killed last night.\n";
                }
            }else {
                result += ConsoleColor.YELLOW + sniper.getName() +ConsoleColor.CYAN_BOLD +" was killed last night.\n";
            }
        }
        if(psychologistChoice!=null && psychologistChoice.isAlive()){
            psychologistChoice.setMuted(true);
            result += ConsoleColor.YELLOW + psychologistChoice.getName() +ConsoleColor.CYAN_BOLD +" should be muted today :)\n";
        }
        if(armoredChoice){
            result+=ConsoleColor.CYAN_BOLD + "Remaining roles :\n";
            for(Player player:players){
                if(player.isAlive())
                    result+=ConsoleColor.CYAN_BOLD + player.getRole() + "\n";
            }
        }
        return result;

    }
    private Player mafiaShoot(){
        Player mafiaShoot=null;
        Player godFather;
        Player lector;
        Player chooser;
        while (mafiaShoot==null){
            if((godFather = roleFinder(Role.GOD_FATHER))!=null)
                chooser=godFather;
            else if((lector = roleFinder(Role.DOCTOR_LECTER))!=null)
                chooser=lector;
            else
                chooser=roleFinder(Role.MAFIA);

            controller.send(chooser, ConsoleColor.RED + "Chose someone to kill:");
            mafiaShoot = new NightChoices(players,chooser, ConsoleColor.RED).start(true);

        }
        return mafiaShoot;
    }

    private Player lectorOperation() {
        Player lector;
        if ((lector = roleFinder(Role.DOCTOR_LECTER)) != null && (mafias.size() > 1 || lector.getAbilityRemain() > 0)) {
            controller.send(lector, ConsoleColor.RED + "Chose someone to protect\n");
            Player lectorChoice = new NightChoices(mafias, lector, ConsoleColor.RED).start((lector.getAbilityRemain()>0));
            if (lectorChoice != null && lectorChoice.equals(lector)) {

                lector.oneUseAbility();
            }
            return lectorChoice;
        }
        return null;
    }

    private Player doctorOperation() {
        Player doctor;
        if ((doctor = roleFinder(Role.DOCTOR)) != null) {
            controller.send(doctor, ConsoleColor.CYAN + "Chose someone to protect\n");
            Player doctorChoice = new NightChoices(players, doctor, ConsoleColor.CYAN).start((doctor.getAbilityRemain()>0));
            if (doctorChoice != null && doctorChoice.equals(doctor)) {

                doctor.oneUseAbility();
            }
            return doctorChoice;
        }
        return null;
    }

    private Player sniperOperation() {
        Player sniper;
        if ((sniper = roleFinder(Role.SNIPER)) != null) {
            controller.send(sniper, ConsoleColor.CYAN + "Do you want to shoot?\n1)Yes\n2)No\n");
            Integer num = controller.receiveInt(sniper, 1, 2);
            if (num != null && num == 1) {
                controller.send(sniper, ConsoleColor.CYAN + "Chose someone to shoot\n");
                Player sniperChoice = new NightChoices(players, sniper, ConsoleColor.CYAN).start(false);
                return sniperChoice;
            }
        }
        return null;
    }

    private void detectiveOperation() {
        Player detective;
        if ((detective = roleFinder(Role.DETECTIVE)) != null) {
            controller.send(detective, ConsoleColor.CYAN + "Chose someone to know it's role\n");
            Player detectiveChoice = new NightChoices(players, detective, ConsoleColor.CYAN).start(false);
            if (detectiveChoice != null) {
                if (detectiveChoice.getRole().equals(Role.DOCTOR_LECTER) || detectiveChoice.getRole().equals(Role.MAFIA)) {
                    controller.send(detective, ConsoleColor.YELLOW + detectiveChoice.getName() + ConsoleColor.CYAN + " is in mafia team!\n");
                } else {
                    controller.send(detective, ConsoleColor.YELLOW + detectiveChoice.getName() + ConsoleColor.CYAN + " is in citizen team!\n");
                }
            }

        }

    }
    private boolean armoredOperation() {
        Player armored;
        if ((armored = roleFinder(Role.ARMORED)) != null) {
            controller.send(armored, ConsoleColor.CYAN + "Do you want to ask to know the remaining roles?\n1)Yes\n2)No\n");
            Integer num = controller.receiveInt(armored, 1, 2);
            if (num != null && num == 1) {
                return true;
            }
        }
        return false;
    }

    private Player psychologistOperation() {
        Player psychologist;
        if ((psychologist = roleFinder(Role.PSYCHOLOGIST)) != null) {
            controller.send(psychologist, ConsoleColor.CYAN + "Do you want to mute someone?\n1)Yes\n2)No\n");
            Integer num = controller.receiveInt(psychologist, 1, 2);
            if (num != null && num == 1) {
                controller.send(psychologist, ConsoleColor.CYAN + "Chose someone to shoot\n");
                Player sniperChoice = new NightChoices(players, psychologist, ConsoleColor.CYAN).start(false);
                return sniperChoice;
            }
        }
        return null;
    }


    public void endGame() {
        ArrayList<Player> aliveMafias = new ArrayList<>();
        for (Player player : mafias) {
            if (player.isAlive()) {
                aliveMafias.add(player);
            }
        }
        if (aliveMafias.size() == 0) {
            controller.sendToAll(ConsoleColor.CYAN_BRIGHT + "\n\nGame is over! citizens won!");
            controller.closeEverything();
            System.exit(0);
        }
        ArrayList<Player> alivePlayers = new ArrayList<>();
        for (Player player : players) {
            if (player.isAlive()) {
                alivePlayers.add(player);
            }
        }
        if (aliveMafias.size() * 2 >= alivePlayers.size()) {
            controller.sendToAll(ConsoleColor.RED + "\n\nGame is over! mafia won!");
            controller.closeEverything();
            System.exit(0);
        }
    }


    public void mayorOperation(Player player) {
        Player mayor;
        if ((mayor = roleFinder(Role.MAYOR)) != null && mayor.isAlive() && player != null && !player.getRole().equals(Role.MAYOR)) {
            controller.send(mayor, ConsoleColor.YELLOW + player.getName() + ConsoleColor.CYAN + " is going to be kicked out ,do you want to cancel it?\n1)Yes\n2)No");
            Integer num = controller.receiveInt(mayor, 1, 2);
            if (!(num == null) && num == 1) {
                controller.sendToAll(ConsoleColor.PURPLE + "MAYOR" + ConsoleColor.BLUE_BOLD + " canceled voting!");
            } else {
                killInDay(player);
            }
        } else if (player != null) {
            killInDay(player);
        }
    }

    public void killInDay(Player player) {
        player.setAlive(false);
        controller.sendToAll(ConsoleColor.YELLOW + player.getName() + ConsoleColor.BLUE_BOLD + " was kicked out!");
        controller.send(player, ConsoleColor.BLUE_BOLD + "You were kicked out of the game :( \nYou can still spectate the game or you can type\"exit\"to leave the game. ");
    }

    public void killAtNight(Player player){
        player.setAlive(false);
        controller.send(player, ConsoleColor.BLUE_BOLD + "You were killed :( \nYou can still spectate the game or you can type\"exit\"to leave the game. ");
    }

    private void introductionNight() {
        controller.sendToAll(ConsoleColor.BLUE_BOLD + "change to " + ConsoleColor.BLACK_BRIGHT + "Introduction Night!");
        sendToMafias(ConsoleColor.RED + mafiasRoles());
        Player mayor;
        Player doctor;
        if ((mayor = roleFinder(Role.MAYOR)) != null && (doctor = roleFinder(Role.DOCTOR)) != null) {
            controller.send(mayor, ConsoleColor.YELLOW + doctor.getName() + ConsoleColor.BLUE_BOLD + " is DOCTOR!");
        }
        sleep(10);


    }


    public void sendToMafias(String string) {
        for (Player player : mafias) {
            controller.send(player, string);
        }
    }

    public void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    public String mafiasRoles() {
        String mafia = "";
        for (Player player : mafias) {
            mafia += player.getName() + "\t" + player.getRole()+"\n";
        }
        return mafia;
    }

    private void initialMafias() {
        for (Player player : players) {
            if (player.getRole().equals(Role.GOD_FATHER) || player.getRole().equals(Role.DOCTOR_LECTER) || player.getRole().equals(Role.MAFIA)) {
                mafias.add(player);
            }
        }

    }

    public Player roleFinder(Role role) {
        for (Player player : players) {
            if (player.getRole().equals(role) && player.isAlive()) {
                return player;
            }
        }
        return null;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
        mafias.remove(player);
    }

}



