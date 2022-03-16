package com.axonactive.agiletools.agiledeck.game.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class GameBoardConfig {

    @Column(name = "question_title")
    private String questionTitle;

    @Column(name = "answer_title")
    private String answerTitle;

    @Column(name = "player_title")
    private String playerTitle;

    @Column(name = "image_player_start")
    private String imagePlayerStart;

    @Column(name = "image_backside")
    private String imageBackside;

    private Boolean editable;

    @Column(name = "random_when_next")
    private Boolean randomWhenNext;

    @Column(name = "additional_possibility")
    private Boolean additionalPossibility;
}
