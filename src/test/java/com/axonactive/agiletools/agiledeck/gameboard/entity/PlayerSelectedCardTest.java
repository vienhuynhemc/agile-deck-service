package com.axonactive.agiletools.agiledeck.gameboard.entity;

import com.axonactive.agiletools.agiledeck.game.entity.Game;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class PlayerSelectedCardTest {

    private PlayerSelectedCard player1;
    private PlayerSelectedCard player2;
    private PlayerSelectedCard player3;

    @BeforeEach
    public void init() {
        Player player01 = new Player();
        player01.setId(1L);
        player1 = new PlayerSelectedCard();
        player1.setPlayer(player01);

        Player player02 = new Player();
        player02.setId(1L);
        player2 = new PlayerSelectedCard();
        player2.setPlayer(player02);

        Player player03 = new Player();
        player03.setId(3L);
        player3 = new PlayerSelectedCard();
        player3.setPlayer(player03);
    }

    @Test
    public void testPlayerSelectedCardEqualTrue() {
        Assertions.assertEquals(this.player1, this.player1);
    }

    @Test
    public void testPlayerSelectedCardEqualFalse() {
        Game game = new Game();
        Assertions.assertNotEquals(this.player1, game);
    }

    @Test
    public void testPlayerSelectedCardEqual() {
        Assertions.assertEquals(this.player1, this.player2);
    }

    @Test
    public void testPlayerSelectedCardNotEqual() {
        Assertions.assertNotEquals(this.player1, this.player3);
    }
}