package com.progamers.uno.domain;

public enum Value {
    //Create an Enum (group of constant values) to represent the values a card can have including specials
    Zero(0),
    One(1),
    Two(2),
    Three(3),
    Four(4),
    Five(5),
    Six(6),
    Seven(7),
    Eight(8),
    Nine(9),
    DrawTwo(10),
    Skip(11),
    Reverse(12),
    Wild(13),
    WildFour(14);

    private static final Value[] values = Value.values();
    public final Integer valueNumber;

    private Value(Integer valueNumber){
        this.valueNumber = valueNumber;
    }

    //Getter function for values
    public static Value getValue(int i) {
        return Value.values[i];
    }
}