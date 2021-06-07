package com.company;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class RoleGenerator {
    private int num;

    public RoleGenerator(int num1){
        num=num1;
    }
    private void roleAdder(ArrayList<Role> roles,Role role){
        roles.add(role);
    }

    private ArrayList<Role> generateList(){
        int mafiaNum=num/3;
        int cityNum=num-mafiaNum;
        ArrayList<Role> roles =new ArrayList<>();
        roleAdder(roles,Role.GOD_FATHER);
        roleAdder(roles,Role.DOCTOR_LECTER);
        for (int i=0;i<mafiaNum-2;i++){
            roleAdder(roles,Role.MAFIA);
        }
        roleAdder(roles,Role.DOCTOR);
        roleAdder(roles,Role.DETECTIVE);
        roleAdder(roles,Role.SNIPER);
        roleAdder(roles,Role.ARMORED);
        roleAdder(roles,Role.MAYOR);
        roleAdder(roles,Role.PSYCHOLOGIST);
        for (int i=0;i<cityNum-6;i++){
            roleAdder(roles,Role.CITIZEN);
        }
        return roles;
    }

    private void disturbRoles(ArrayList<Role> roles){
        Random random = new Random();
        int index;
        for (Player player : Controller.getInstance().getPlayerStreamsMap().keySet()){
            index = random.nextInt(roles.size());
            player.setRole(roles.get(index));
            Controller.getInstance().send(player,ConsoleColor.BLUE_BOLD + "your role is : \"" + roles.get(index).name() + "\" !");
            roles.remove(index);
        }
    }
    public void start(){
        disturbRoles(generateList());
    }
}
