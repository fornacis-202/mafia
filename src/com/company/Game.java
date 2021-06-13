package com.company;

import java.util.ArrayList;

/**
 * The type Game.
 */
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

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    /**
     * Start.
     */
    public void start() {
        initialMafias();
        introductionNight();
        String nightEvent = null;
        while (true) {
            day(nightEvent);
            nightEvent=night();
        }

    }

    /**
     * simulating a day in game
     * @param nightEvent things happened last night
     */
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

    /**
     * simulating a night in the game
     * @return the things happened tonight
     */

    private String night() {
        controller.sendToAll(ConsoleColor.BLUE_BOLD + "Change to " + ConsoleColor.BLACK_BRIGHT + "Night!");
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

    /**
     * calculate the result of outs,muted&... at night
     * @param mafiaShot
     * @param lectorChoice
     * @param doctorChoice
     * @param sniperChoice
     * @param sniper
     * @param psychologistChoice
     * @param armoredChoice
     * @return the result as a string
     */
    private String calculateResult(Player mafiaShot,Player lectorChoice,Player doctorChoice,Player sniperChoice,Player sniper,Player psychologistChoice,boolean armoredChoice){
        String result = "";
        if(mafiaShot!=null  && !mafiaShot.equals(doctorChoice)){
            if(mafiaShot.getRole().equals(Role.ARMORED) && mafiaShot.isProtected()){
                mafiaShot.setProtected(false);
            }else {
                killAtNight(mafiaShot);
                result += ConsoleColor.YELLOW + mafiaShot.getName() + ConsoleColor.CYAN_BOLD + " was killed last night.\n";
            }
        }
        if(sniperChoice!=null){
            if((sniperChoice.getRole().equals(Role.GOD_FATHER) || sniperChoice.getRole().equals(Role.DOCTOR_LECTER) || sniperChoice.getRole().equals(Role.MAFIA))){
                if(!sniperChoice.equals(lectorChoice)){
                    killAtNight(sniperChoice);
                    result += ConsoleColor.YELLOW + sniperChoice.getName() +ConsoleColor.CYAN_BOLD +" was killed last night.\n";
                }
            }else {
                killAtNight(sniper);
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

    /**
     * ask head mafia to chose someone to kill at night
     * @return the player mafia choosed to kill
     */
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

    /**
     * ask lector to save a mafia
     * @return the mafia he saves
     */
    private Player lectorOperation() {
        Player lector;
        if ((lector = roleFinder(Role.DOCTOR_LECTER)) != null && (mafias.size() > 1 || lector.getAbilityRemain() > 0)) {
            controller.send(lector, ConsoleColor.RED + "Chose someone to protect:");
            Player lectorChoice = new NightChoices(mafias, lector, ConsoleColor.RED).start((lector.getAbilityRemain()>0));
            if (lectorChoice != null && lectorChoice.equals(lector)) {

                lector.oneUseAbility();
            }
            return lectorChoice;
        }
        return null;
    }

    /**
     * ask doctor to save someone
     * @return the player he save
     */
    private Player doctorOperation() {
        Player doctor;
        if ((doctor = roleFinder(Role.DOCTOR)) != null) {
            controller.send(doctor, ConsoleColor.CYAN + "Chose someone to protect:");
            Player doctorChoice = new NightChoices(players, doctor, ConsoleColor.CYAN).start((doctor.getAbilityRemain()>0));
            if (doctorChoice != null && doctorChoice.equals(doctor)) {

                doctor.oneUseAbility();
            }
            return doctorChoice;
        }
        return null;
    }

    /**
     * ask sniper if he wants to shoot someone
     * @return the player he choosed to shoot
     */
    private Player sniperOperation() {
        Player sniper;
        if ((sniper = roleFinder(Role.SNIPER)) != null) {
            controller.send(sniper, ConsoleColor.CYAN + "Do you want to shoot?\n1)Yes\n2)No\n");
            Integer num = controller.receiveInt(sniper, 1, 2);
            if (num != null && num == 1) {
                controller.send(sniper, ConsoleColor.CYAN + "Chose someone to shoot:");
                Player sniperChoice = new NightChoices(players, sniper, ConsoleColor.CYAN).start(false);
                return sniperChoice;
            }
        }
        return null;
    }

    /**
     * tell detective if a he choose person is mafia or not
     */
    private void detectiveOperation() {
        Player detective;
        if ((detective = roleFinder(Role.DETECTIVE)) != null) {
            controller.send(detective, ConsoleColor.CYAN + "Chose someone to know it's role:");
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

    /**
     * ask armored if he wants to ask the remaining roles or not
     * @return
     */
    private boolean armoredOperation() {
        Player armored;
        if ((armored = roleFinder(Role.ARMORED)) != null && armored.getAbilityRemain()>0) {
            controller.send(armored, ConsoleColor.CYAN + "Do you want to ask to know the remaining roles?\n1)Yes\n2)No\n");
            Integer num = controller.receiveInt(armored, 1, 2);
            if (num != null && num == 1) {
                armored.oneUseAbility();
                return true;
            }
        }
        return false;
    }

    /**
     * ask psychologist if ha wants to mute someone
     * @return
     */
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


    /**
     * check if the game is finished or not and if so , finish the program
     */
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


    /**
     * ask mayor if he wants to cancel the voting or not
     *
     * @param player the player who is going to be out
     */
    private void mayorOperation(Player player) {
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

    /**
     * Kill in day.
     *
     * @param player the player
     */
    private void killInDay(Player player) {
        player.setAlive(false);
        controller.sendToAll(ConsoleColor.YELLOW + player.getName() + ConsoleColor.BLUE_BOLD + " was kicked out!");
        controller.send(player, ConsoleColor.BLUE_BOLD + "You were kicked out of the game :( \nYou can still spectate the game or you can type\"exit\"to leave the game. ");
    }

    /**
     * Kill at night.
     *
     * @param player the player
     */
    private void killAtNight(Player player){
        player.setAlive(false);
        controller.send(player, ConsoleColor.BLUE_BOLD + "You were killed :( \nYou can still spectate the game or you can type\"exit\"to leave the game. ");
    }

    /**
     * simulating introduction night in the game
     */
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


    /**
     * Send to mafias.
     *
     * @param string the string
     */
    private void sendToMafias(String string) {
        for (Player player : mafias) {
            controller.send(player, string);
        }
    }

    /**
     * Sleep.
     *
     * @param seconds the seconds
     */
    private void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Mafias roles string.
     *
     * @return the string contains mafia names and roles
     */
    private String mafiasRoles() {
        String mafia = "";
        for (Player player : mafias) {
            mafia += player.getName() + "\t" + player.getRole()+"\n";
        }
        return mafia;
    }

    /**
     * add mafias to the arraylist of mafias
     */
    private void initialMafias() {
        for (Player player : players) {
            if (player.getRole().equals(Role.GOD_FATHER) || player.getRole().equals(Role.DOCTOR_LECTER) || player.getRole().equals(Role.MAFIA)) {
                mafias.add(player);
            }
        }

    }

    /**
     * finds a role if it is in the game
     *
     * @param role the role
     * @return the player
     */
    public Player roleFinder(Role role) {
        for (Player player : players) {
            if (player.getRole().equals(role) && player.isAlive()) {
                return player;
            }
        }
        return null;
    }

    /**
     * Add player.
     *
     * @param player the player
     */
    public void addPlayer(Player player) {
        players.add(player);
    }

    /**
     * Remove player.
     *
     * @param player the player
     */
    public void removePlayer(Player player) {
        players.remove(player);
        mafias.remove(player);
    }

}



