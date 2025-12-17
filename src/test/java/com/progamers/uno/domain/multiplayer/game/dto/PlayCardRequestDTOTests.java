package com.progamers.uno.domain.multiplayer.game.dto;

import org.junit.jupiter.api.Test;

public class PlayCardRequestDTOTests {

    @Test
    void testConstructor_whenCalled_thenSetsFields() {
        PlayCardRequestDTO action = new PlayCardRequestDTO();
        action.setToken("sampleToken");
        action.setPlayerId("player123");
        action.setCardIndex(5);
        action.setWildColour("Red");

        assert action.getToken().equals("sampleToken");
        assert action.getPlayerId().equals("player123");
        assert action.getCardIndex() == 5;
        assert action.getWildColour().equals("Red");

    }
}
