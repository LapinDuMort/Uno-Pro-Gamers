package com.progamers.uno.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(HomeController.class)
public class HomeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    /* --- GET / --- */

    @Test
    void testIndex_whenVisited_thenDisplaysMainMenu() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home/index"))
                .andExpect(content().string(containsString("<h1>UNO!</h1>")));

    }

    /* --- GET /lobby --- */

    @Test
    void testLobby_whenVisited_thenDisplaysLobbyPage() throws Exception {
        mockMvc.perform(get("/lobby"))
                .andExpect(status().isOk())
                .andExpect(view().name("lobby/index"))
                .andExpect(content().string(containsString("<h2>UNO Lobby</h2>")));
    }

    @Test
    void testGame_whenRequested_thenRendersGameView() throws Exception {
        mockMvc.perform(get("/game"))
                .andExpect(status().isOk())
                .andExpect(view().name("game/game"));
    }
}
