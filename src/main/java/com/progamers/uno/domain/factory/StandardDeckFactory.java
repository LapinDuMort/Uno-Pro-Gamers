package com.progamers.uno.domain.factory;

import com.progamers.uno.domain.Card;
import com.progamers.uno.domain.Colour;
import com.progamers.uno.domain.Deck;
import com.progamers.uno.domain.Value;

import java.util.ArrayList;
import java.util.List;

public class StandardDeckFactory implements DeckFactory{

    @Override
    public Deck createDeck() {
        List<Card> cards = new ArrayList<Card>(108);
            //Make a list, colours, equal to the five colours/suits
            Colour[] colours = Colour.values();
            //Set value to 0 and increment with each card added

            //Loop through the first four colours (ignoring Wild)
            for(int i = 0; i < colours.length-1; i++){
                Colour colour = colours[i];
                //Make a single card with value 0 for each colour
                cards.add(Card.builder()
                        .colour(colour)
                        .value(Value.getValue(0)
                                ).build());

                //For card values 1-9 make two copies each of each colour
                for (int j = 1; j < 10; j++){
                    cards.add(Card.builder()
                            .colour(colour)
                            .value(Value.getValue(j)
                            ).build());
                    cards.add(Card.builder()
                            .colour(colour)
                            .value(Value.getValue(j)
                            ).build());
                }

                // For the card values DrawTwo, Skip and Reverse make two copies for each colour
                Value[] values = new Value[]{Value.DrawTwo, Value.Skip, Value.Reverse};
                for (Value value : values){
                    cards.add(Card.builder()
                            .colour(colour)
                            .value(value)
                            .build());
                    cards.add(Card.builder()
                            .colour(colour)
                            .value(value)
                            .build());
                }
            }
            //Finally, make four copies of the Wild and WildFour cards, each with the Wild colour
            Value[] values = new Value[]{Value.Wild, Value.WildFour};
            for (Value value : values){

                for (int i = 0; i<4 ;i++ ){
                    cards.add(Card.builder()
                            .colour(Colour.Wild)
                            .value(value)
                            .build());
                }
            }
            return new Deck(cards);
    }
}
