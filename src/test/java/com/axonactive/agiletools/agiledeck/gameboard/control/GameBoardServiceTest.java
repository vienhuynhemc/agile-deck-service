package com.axonactive.agiletools.agiledeck.gameboard.control;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;

import com.axonactive.agiletools.agiledeck.AgileDeckException;
import com.axonactive.agiletools.agiledeck.gameboard.entity.AnsweredQuestion;
import com.axonactive.agiletools.agiledeck.gameboard.entity.GameBoard;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class GameBoardServiceTest {
    @Inject
    GameBoardService gameBoardService;

    private final String WRONG_GAME_BOARD_CODE = "2";
    private final String GAME_BOARD_CODE_WITH_ANSWERED_QUESTION = "asd6gfga-f296-sdf3-0fn2-asf86gc1crt2";
    private final String GAME_BOARD_CODE_WITH_NO_ANSWERED_QUESTION = "e3bb8a9d-704e-430e-acae-1fb0a96rtfu8";

    @Test
    void when_joinWrongGameBoard_thenThrowAgileDeckException() {
        Assertions.assertThrows(AgileDeckException.class, () -> {
            gameBoardService.join(WRONG_GAME_BOARD_CODE);
        });
    }

    @Test
    void when_joinGameBoardWithNoAnswerQuestion_thenGameBoardHaveZeroQuestion() {
        AnsweredQuestion answeredQuestion = gameBoardService.join(GAME_BOARD_CODE_WITH_NO_ANSWERED_QUESTION);
        assertNull(answeredQuestion.getId());

    }

    @Test
    void when_joinGameBoardWithAnswerQuestion_thenGiveListOfAnsweredQuestion() {
        AnsweredQuestion answeredQuestion = gameBoardService.join(GAME_BOARD_CODE_WITH_ANSWERED_QUESTION);
        assertTrue(answeredQuestion.getId() > 0);

    }

    @Test
    void when_createGameBoard_thenGameBoardWasCreated() {
        GameBoard gameBoard = gameBoardService.create(2L);
        assertNotNull(gameBoard);
    }

    @Test
    void when_createGameBoardWithNoGameId_thenThrowAgileDeckException() {
        Assertions.assertThrows(AgileDeckException.class, () -> {
            gameBoardService.create(-1L);
        });
    }

}
