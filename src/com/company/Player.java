package com.company;

/**
 * The type Player.
 */
public class Player {
    private String name;
    private Role role;
    private boolean isAlive;
    private int abilityRemain;
    private boolean isProtected;
    private boolean isMuted;

    /**
     * Instantiates a new Player.
     *
     * @param name the name
     */
    public Player(String name){
        this.name = name;
        isAlive=true;
        isMuted=false;
        isProtected=false;


    }

    /**
     * Is muted boolean.
     *
     * @return the boolean
     */
    public boolean isMuted() {
        return isMuted;
    }

    /**
     * Sets muted.
     *
     * @param muted the muted
     */
    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    /**
     * Is protected boolean.
     *
     * @return the boolean
     */
    public boolean isProtected() {
        return isProtected;
    }

    /**
     * Sets protected.
     *
     * @param aProtected the a protected
     */
    public void setProtected(boolean aProtected) {
        isProtected = aProtected;
    }

    /**
     * Gets ability remain.
     *
     * @return the ability remain
     */
    public int getAbilityRemain() {
        return abilityRemain;
    }

    /**
     * One use ability.
     */
    public void oneUseAbility(){
        abilityRemain--;
    }

    /**
     * Sets ability remain.
     *
     * @param abilityRemain the ability remain
     */
    public void setAbilityRemain(int abilityRemain) {
        this.abilityRemain = abilityRemain;
    }

    /**
     * Is alive boolean.
     *
     * @return the boolean
     */
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * Sets alive.
     *
     * @param alive the alive
     */
    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets role.
     *
     * @param role the role
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Gets role.
     *
     * @return the role
     */
    public Role getRole() {
        return role;
    }
}
