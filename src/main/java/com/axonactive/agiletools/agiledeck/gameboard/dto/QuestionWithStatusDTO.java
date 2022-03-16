package com.axonactive.agiletools.agiledeck.gameboard.dto;

import com.axonactive.agiletools.agiledeck.game.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionWithStatusDTO {

    private Question question;
    private boolean isPlayed;
    private boolean isPlaying;

}
