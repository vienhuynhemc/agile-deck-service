package com.axonactive.agiletools.agiledeck.game.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@Embeddable
public class QuestionContent {
    
    @Column(name = "question_content")
    private String content;

    @Column(name = "question_content_as_image")
    private String image;
    
}
