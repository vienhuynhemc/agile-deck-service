package com.axonactive.agiletools.agiledeck.game.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@Embeddable
public class AnswerContent {
    
    @Column(name = "answer_content")
    private String content;

    @Column(name = "answer_content_as_description", nullable = true)
    private String contentAsDescription;

    @Column(name = "answer_content_as_image")
    private String contentAsImage;
}
