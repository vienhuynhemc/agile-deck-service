package com.axonactive.agiletools.agiledeck.gameboard.entity;

import com.axonactive.agiletools.agiledeck.MsgCodes;

public enum AnsweredQuestionDetailMsgCodes implements MsgCodes{
    ANSWER_QUESTION_DETAIL_NOT_FOUND;

    @Override
    public String getValue() {
        return this.toString();
    }
    
}
