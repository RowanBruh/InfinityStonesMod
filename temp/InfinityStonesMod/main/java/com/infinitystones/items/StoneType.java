package com.infinitystones.items;

/**
 * Enum representing different types of Infinity Stones
 */
public enum StoneType {
    SPACE("space", "Space Stone", "Blue"),
    MIND("mind", "Mind Stone", "Yellow"),
    REALITY("reality", "Reality Stone", "Red"),
    POWER("power", "Power Stone", "Purple"),
    TIME("time", "Time Stone", "Green"),
    SOUL("soul", "Soul Stone", "Orange");
    
    private final String id;
    private final String displayName;
    private final String color;
    
    StoneType(String id, String displayName, String color) {
        this.id = id;
        this.displayName = displayName;
        this.color = color;
    }
    
    public String getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getColor() {
        return color;
    }
}