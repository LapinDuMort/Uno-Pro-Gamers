package com.progamers.uno.domain.multiplayer.game.dto;

import org.junit.jupiter.api.Test;

public class DrawCardRequestDTOTests {

    // there are no additional fields to tests here
    @Test
    void testConstructor_whenCalled_thenSetsFields() {
        DrawCardRequestDTO action = new DrawCardRequestDTO();
        action.setToken("sampleToken");
        action.setPlayerId("player123");

        assert action.getToken().equals("sampleToken");
        assert action.getPlayerId().equals("player123");
    }
}
