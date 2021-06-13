package com.company;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * The type Role generator , generates the role for each client
 */
public class RoleGenerator {
    private int num;

    /**
     * Instantiates a new Role generator.
     *
     * @param num1 the number of the player in the game
     */
    public RoleGenerator(int num1){
        num=num1;
    }
    private void roleAdder(ArrayList<Role> roles,Role role){
        roles.add(role);
    }

    /**
     * generates a list of the roles must be in the game
     * @return the list
     */
    private ArrayList<Role> generateList(){
        int mafiaNum=num/3;
        int cityNum=num-mafiaNum;
        ArrayList<Role> roles =new ArrayList<>();
        roleAdder(roles,Role.GOD_FATHER);
        mafiaNum--;
        if(mafiaNum>0)
            roleAdder(roles,Role.DOCTOR_LECTER);
        mafiaNum--;
        for (;mafiaNum>0;mafiaNum--){
            roleAdder(roles,Role.MAFIA);
        }
        roleAdder(roles,Role.DOCTOR);
        cityNum--;
        if(cityNum>0) {
            roleAdder(roles, Role.ARMORED);
        }
        cityNum--;
        if(cityNum>0) {
            roleAdder(roles, Role.MAYOR);
        }
        cityNum--;
        if(cityNum>0) {
            roleAdder(roles, Role.DETECTIVE);
        }
        cityNum--;
        if(cityNum>0) {
            roleAdder(roles, Role.PSYCHOLOGIST);
        }
        cityNum--;
        if(cityNum>0) {
            roleAdder(roles, Role.SNIPER);
        }
        cityNum--;
        for (;cityNum>0;cityNum--){
            roleAdder(roles,Role.CITIZEN);
        }
        return roles;
    }

    /**
     * disturb roles between the player randomly
     * @param roles
     */
    private void disturbRoles(ArrayList<Role> roles){
        Random random = new Random();
        int index;
        for (Player player : Controller.getInstance().getPlayerStreamsMap().keySet()){
            index = random.nextInt(roles.size());
            player.setRole(roles.get(index));
            if(roles.get(index).equals(Role.DOCTOR)||roles.get(index).equals(Role.DOCTOR_LECTER)){
                player.setAbilityRemain(1);
            }else if( roles.get(index).equals(Role.ARMORED)){
                player.setAbilityRemain(2);
                player.setProtected(true);
            }
            Controller.getInstance().send(player,ConsoleColor.BLUE_BOLD + "your role is : "+ConsoleColor.PURPLE + roles.get(index).name()+ConsoleColor.BLUE_BOLD + " !");
            roles.remove(index);
        }
    }

    /**
     * Start the role generating and disturbing it
     */
    public void start(){
        disturbRoles(generateList());
    }
}
