package com.axonactive.agiletools.agiledeck.game.control;

import java.util.List;

import javax.inject.Inject;

import com.axonactive.agiletools.agiledeck.game.entity.Answer;
import com.axonactive.agiletools.agiledeck.game.entity.Game;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class AnswerServiceTest {

    Game game;

    @Inject
    AnswerService answerService;

    @Inject
    GameService gameService;

    @BeforeEach
    public void init() {
        game = gameService.findById((long) 1);
    }

    @Test
    void givenGameBoardCode_whenGetListAnswerByGame_thenReturnListAnswer() {
        List<Answer> listAnswer = answerService.getByGame(this.game.getId());
        Assertions.assertEquals(3, listAnswer.size());
    }
}
