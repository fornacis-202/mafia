package com.company;

public class Player {
    private String name;
    private Role role;
    private boolean isAlive;
    private boolean isProtected;
    private boolean isMuted;

    public Player(String name){
        this.name = name;
        isAlive=true;
        isMuted=false;
        isProtected=false;

    }


    public String getName() {
        return name;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
