package com.axonactive.agiletools.agiledeck.gameboard.control;

import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import com.axonactive.agiletools.agiledeck.AgileDeckException;
import com.axonactive.agiletools.agiledeck.Faker;
import com.axonactive.agiletools.agiledeck.gameboard.entity.GameBoard;
import com.axonactive.agiletools.agiledeck.gameboard.entity.Player;
import com.axonactive.agiletools.agiledeck.gameboard.entity.PlayerMsgCodes;

@RequestScoped
@Transactional
public class PlayerService {

    @PersistenceContext
    EntityManager em;

    @Inject
    GameBoardService gameBoardService;

    public Player create(String code) {
        GameBoard gameBoard = gameBoardService.getByCode(code);
        gameBoardService.validate(gameBoard);
        Player player = this.init(gameBoard);
        em.persist(player);
        return player;
    }

    private Player init(GameBoard gameBoard) {
        Faker faker = new Faker();
        String name = "";
        do {
            name = faker.food().fruit();
        } while (isExisted(gameBoard.getCode(), name) || name.length() > 15);
        return new Player(gameBoard, name);
    }

    private boolean isExisted(String code, String name) {
        TypedQuery<Player> query = em.createNamedQuery(Player.GET_BY_GAME_BOARD, Player.class);
        query.setParameter("gameBoardCode", code);
        query.setParameter("playerName", name);
        Player player = query.getResultStream().findFirst().orElse(null);
        return Objects.nonNull(player);
    }

    private void validate(Player player) {
        if (Objects.isNull(player)) {
            throw new AgileDeckException(PlayerMsgCodes.PLAYER_NOT_FOUND);
        }
    }

    public Player findById(Long playerId) {
        TypedQuery<Player> query = em.createNamedQuery(Player.GET_BY_ID, Player.class);
        query.setParameter("id", playerId);
        Player player = query.getResultStream().findFirst().orElse(null);
        validate(player);
        return player;
    }

    public Player changeName(Long id, String name) {
        Player existedPlayer = findById(id);
        existedPlayer.setName(name);
        return em.merge(existedPlayer);
    }
}
