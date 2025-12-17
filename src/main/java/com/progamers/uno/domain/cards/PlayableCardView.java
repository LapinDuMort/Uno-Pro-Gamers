package com.progamers.uno.domain.cards;

public class PlayableCardView {
    private final Card card;
    private final boolean playable;

    public PlayableCardView(Card card, boolean playable) {
        this.card = card;
        this.playable = playable;
    }

    public Card getCard() {
        return card;
    }

    public boolean isPlayable() {
        return playable;
    }
}
