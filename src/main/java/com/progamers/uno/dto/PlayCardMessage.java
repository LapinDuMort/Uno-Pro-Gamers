package com.progamers.uno.dto;

import lombok.Data;

@Data
public class PlayCardMessage {
    private int cardIndex;
    private String wildColor;
}