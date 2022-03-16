package com.axonactive.agiletools.agiledeck.gameboard.control;

import javax.inject.Inject;

import com.axonactive.agiletools.agiledeck.AgileDeckException;
import com.axonactive.agiletools.agiledeck.game.entity.Game;
import com.axonactive.agiletools.agiledeck.gameboard.entity.GameBoard;
import com.axonactive.agiletools.agiledeck.gameboard.entity.Player;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class PlayerServiceTest {

    GameBoard gameBoard;

    @Inject
    PlayerService playerService;

    @Inject
    GameBoardService gameBoardService;

    @BeforeEach
    public void init() {
        Game game = new Game();
        game.setName("Iterative - Incremental - Big Bang");
        gameBoard = gameBoardService.getByCode("asd6gfga-f296-sdf3-0fn2-asf86gc1crt2");
    }

    @Test
    public void whenCreatePlayer_thenReturnNonNullPlayer() {
        Player player = playerService.create(this.gameBoard.getCode());
        Assertions.assertNotNull(player);
        Assertions.assertNotEquals(0, player.getName().length());
        Assertions.assertTrue(player.getName().length() <= 15);
    }

    @Test
    public void whenCreatePlayer_thenReturnGameBoardNotFound() {
        Assertions.assertThrows(AgileDeckException.class, () -> {
            playerService.create("GAME_BOARD_NOT_FOUND");
        });
    }
}
