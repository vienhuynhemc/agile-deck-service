package com.axonactive.agiletools.agiledeck.gameboard.entity;

import com.axonactive.agiletools.agiledeck.MsgCodes;

public enum CustomAnswerMsgCodes implements MsgCodes{
    CUSTOM_ANSWER_NOT_FOUND;

    @Override
    public String getValue() {
        return this.toString();
    }
    
}
