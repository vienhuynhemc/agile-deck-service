package com.axonactive.agiletools.agiledeck.gameboard.entity;

import com.axonactive.agiletools.agiledeck.MsgCodes;

public enum PlayerMsgCodes implements MsgCodes {
    PLAYER_NOT_FOUND;

    @Override
    public String getValue() {
        return this.toString();
    }
}
