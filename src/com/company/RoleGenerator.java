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
        mafiaNum--;
        if(mafiaNum>0)
            roleAdder(roles,Role.DOCTOR_LECTER);
        mafiaNum--;
        for (;mafiaNum>0;mafiaNum--){
            roleAdder(roles,Role.MAFIA);
        }
        roleAdder(roles,Role.DOCTOR);
        cityNum--;
        if(cityNum>0)
            roleAdder(roles,Role.DETECTIVE);
        cityNum--;
        if(cityNum>0)
            roleAdder(roles,Role.SNIPER);
        cityNum--;
        if(cityNum>0)
            roleAdder(roles,Role.ARMORED);
        cityNum--;
        if(cityNum>0)
            roleAdder(roles,Role.MAYOR);
        cityNum--;
        if(cityNum>0)
            roleAdder(roles,Role.PSYCHOLOGIST);
        cityNum--;
        for (;cityNum>0;cityNum--){
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
