package com.company;

public class Player {
    private String name;
    private Role role;
    private boolean isAlive;
    private int abilityRemain;
    private boolean isProtected;
    private boolean isMuted;

    public Player(String name){
        this.name = name;
        isAlive=true;
        isMuted=false;
        isProtected=false;


    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected(boolean aProtected) {
        isProtected = aProtected;
    }

    public int getAbilityRemain() {
        return abilityRemain;
    }

    public void oneUseAbility(){
        abilityRemain--;
    }

    public void setAbilityRemain(int abilityRemain) {
        this.abilityRemain = abilityRemain;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public String getName() {
        return name;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return role;
    }
}
