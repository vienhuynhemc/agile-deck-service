package com.axonactive.agiletools.agiledeck.gameboard.entity;

import com.axonactive.agiletools.agiledeck.MsgCodes;

public enum GameBoardMsgCodes implements MsgCodes {

    GAME_BOARD_NOT_FOUND, GAME_BOARD_CODE_NOT_FOUND, UNMATCHED_CODE_FORMAT, LIST_ANSWER_OVER_LIMITATION;

    @Override
    public String getValue() {
        return this.toString();
    }

}
