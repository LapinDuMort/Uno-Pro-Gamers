package com.progamers.uno.domain;

//Create an Enum (group of constant values) to represent the games 5 colours/suits
public enum Colour {
    Red(0),
    Blue(100),
    Green(200),
    Yellow(300),
    Wild(400);

    private static final Colour[] colours = Colour.values();
    public final Integer colourNumber;

    private Colour(Integer colourNumber){
        this.colourNumber = colourNumber;
    }

    //Getter function for colours
    public static Colour getColour(int i) {
        return Colour.colours[i];
    }
}

