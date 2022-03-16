package com.axonactive.agiletools.agiledeck.game.control;

import java.util.List;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import com.axonactive.agiletools.agiledeck.AgileDeckException;
import com.axonactive.agiletools.agiledeck.game.entity.Game;
import com.axonactive.agiletools.agiledeck.game.entity.GameMsgCodes;

@RequestScoped
@Transactional
public class GameService {

    @PersistenceContext
    EntityManager em;

    public List<Game> getInformationGame(){
        TypedQuery<Game> query = em.createNamedQuery(Game.GET_ALL_GAME, Game.class);
        return query.getResultList();
    }

    public Game findById(Long id){
        return em.find(Game.class, id);
    }

    public void validate(Game game){
        if(Objects.isNull(game)){
            throw new AgileDeckException(GameMsgCodes.GAME_NOT_FOUND); 
        }
    }
    
}