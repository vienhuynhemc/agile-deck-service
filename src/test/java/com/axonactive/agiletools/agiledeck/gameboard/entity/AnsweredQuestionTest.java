package com.axonactive.agiletools.agiledeck.gameboard.entity;

import com.axonactive.agiletools.agiledeck.game.entity.Answer;
import com.axonactive.agiletools.agiledeck.game.entity.Game;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class AnsweredQuestionTest {

    private List<Answer> answers;
    private GameBoard gameBoard;

    @BeforeEach
    public void init() {
        answers = Arrays.asList(
                new Answer(1L),
                new Answer(2L)
        );

        Game game = new Game();
        game.setName("Planning Poker");

        gameBoard = new GameBoard("dfe24c11-e3fe-45db-9a11-033cd6a51fec", game);
    }

    @Test
    public void whenCreateWithoutQuestion_thenReturnCorrectAnswer() {
        AnsweredQuestion answeredQuestion = AnsweredQuestion.createWithoutQuestion(gameBoard, answers);

        Assertions.assertNotNull(answeredQuestion);
    }

}