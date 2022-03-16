package com.axonactive.agiletools.agiledeck.game.entity;

import com.axonactive.agiletools.agiledeck.MsgCodes;

public enum QuestionMsgCodes implements MsgCodes{
    NO_QUESTIONS_LEFT;

    @Override
    public String getValue() {
        return this.toString();
    }
    
}
