package com.axonactive.agiletools.agiledeck.gameboard.control;

import java.util.List;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import com.axonactive.agiletools.agiledeck.AgileDeckException;
import com.axonactive.agiletools.agiledeck.game.entity.Answer;
import com.axonactive.agiletools.agiledeck.gameboard.entity.CustomAnswer;
import com.axonactive.agiletools.agiledeck.gameboard.entity.CustomAnswerMsgCodes;
import com.axonactive.agiletools.agiledeck.gameboard.entity.GameBoard;

@Transactional
@RequestScoped
public class CustomAnswerService {

    @PersistenceContext
    EntityManager em;

    public CustomAnswer addAnswerOfGameBoard(Answer answer, GameBoard gameBoard) {

        TypedQuery<Integer> query = em.createNamedQuery(CustomAnswer.GET_MAX_NUMBER_ORDER_BY_GAME_BOARD_ID,
                Integer.class);
        query.setParameter("gameBoardId", gameBoard.getId());
        Integer maxNumberOrder = query.getResultStream().findFirst().orElse(null);
        CustomAnswer newCustomAnswer = new CustomAnswer(answer.getContent(), maxNumberOrder + 1, gameBoard);
        em.persist(newCustomAnswer);
        return newCustomAnswer;
    }

    public void createNewCustomAnswerOfGameBoard(List<Answer> defaultAnswers, GameBoard gameBoard) {
        defaultAnswers.forEach(answer -> {
            CustomAnswer customAnswer = new CustomAnswer(answer.getContent(), answer.getNumberOrder(), gameBoard);
            em.persist(customAnswer);
        });
    }

    public void cloneCustomAnswerOfGameBoard(List<CustomAnswer> defaultAnswers, GameBoard gameBoard) {
        defaultAnswers.forEach(answer -> {
            CustomAnswer customAnswer = new CustomAnswer(answer.getContent(), answer.getNumberOrder(), gameBoard);
            em.persist(customAnswer);
        });
    }

    public void delete(Long customAnswerId) {
        CustomAnswer isExitedCustomAnswer = findById(customAnswerId);
        if (!Objects.isNull(isExitedCustomAnswer)) {
            em.remove(isExitedCustomAnswer);
        }
    }

    public List<CustomAnswer> getByGameBoardId(GameBoard gameBoard) {
        TypedQuery<CustomAnswer> query = em.createNamedQuery(CustomAnswer.GET_BY_GAME_BOARD_ID, CustomAnswer.class);
        query.setParameter("gameBoardId", gameBoard.getId());
        return query.getResultList();
    }

    public void editContent(CustomAnswer customAnswer) {
        CustomAnswer existedCustomAnswer = findById(customAnswer.getId());
        validate(existedCustomAnswer);
        existedCustomAnswer.setContent(customAnswer.getContent());
        em.merge(existedCustomAnswer);
    }

    private CustomAnswer findById(Long customAnswerId) {

        TypedQuery<CustomAnswer> query = em.createNamedQuery(CustomAnswer.GET_BY_ID, CustomAnswer.class);
        query.setParameter("customAnswerId", customAnswerId);
        return query.getResultStream().findFirst().orElse(null);
    }

    private void validate(CustomAnswer customAnswer) {
        if (Objects.isNull(customAnswer)) {
            throw new AgileDeckException(CustomAnswerMsgCodes.CUSTOM_ANSWER_NOT_FOUND);
        }
    }

}
