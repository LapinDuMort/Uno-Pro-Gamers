package com.progamers.uno.domain;

//Create an Enum (group of constant values) to represent the games 5 colours/suits
public enum Colour {
    Red, Blue, Green, Yellow, Wild;

    private static final Colour[] colours = Colour.values();

    //Getter function for colours
    public static Colour getColour(int i) {
        return Colour.colours[i];
    }
}