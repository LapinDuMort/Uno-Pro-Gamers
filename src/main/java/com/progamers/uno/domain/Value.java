package com.progamers.uno.domain;

public enum Value {
    //Create an Enum (group of constant values) to represent the values a card can have including specials
    Zero, One, Two, Three, Four, Five, Six, Seven, Eight, Nine, DrawTwo, Skip, Reverse, Wild, WildFour;

    private static final Value[] values = Value.values();

    //Getter function for values
    public static Value getValue(int i) {
        return Value.values[i];
    }
}