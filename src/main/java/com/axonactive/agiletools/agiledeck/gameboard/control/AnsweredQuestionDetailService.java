package com.axonactive.agiletools.agiledeck.gameboard.control;

import java.util.List;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import com.axonactive.agiletools.agiledeck.AgileDeckException;
import com.axonactive.agiletools.agiledeck.game.entity.AnswerContent;
import com.axonactive.agiletools.agiledeck.gameboard.entity.AnsweredQuestion;
import com.axonactive.agiletools.agiledeck.gameboard.entity.AnsweredQuestionDetail;
import com.axonactive.agiletools.agiledeck.gameboard.entity.AnsweredQuestionDetailMsgCodes;
import com.axonactive.agiletools.agiledeck.gameboard.entity.Player;

@RequestScoped
@Transactional
public class AnsweredQuestionDetailService {

	@PersistenceContext
	EntityManager em;

	public AnsweredQuestionDetail create(AnsweredQuestion answeredQuestion, Player player) {

		// this.deleteOldAnswer();
		AnsweredQuestionDetail answeredQuestionDetail = new AnsweredQuestionDetail(answeredQuestion, player);
		em.persist(answeredQuestionDetail);
		return answeredQuestionDetail;
	}

	// private void deleteOldAnswer() {
	// TypedQuery<AnsweredQuestionDetail> query =
	// em.createNamedQuery(AnsweredQuestionDetail.GET_ALL_BY_NULL_ANS,
	// AnsweredQuestionDetail.class);
	// List<AnsweredQuestionDetail> answeredQuestionDetails = query.getResultList();
	// answeredQuestionDetails.forEach(aqd -> em.remove(aqd));
	// }

	public AnsweredQuestionDetail update(Long answerQuestionDetailId, AnswerContent answerContent) {
		AnsweredQuestionDetail answeredQuestionDetail = this.getById(answerQuestionDetailId);
		this.validate(answeredQuestionDetail);
		answeredQuestionDetail.setAnswer(answerContent);
		return em.merge(answeredQuestionDetail);
	}

	private AnsweredQuestionDetail getById(Long answerQuestionDetailId) {
		TypedQuery<AnsweredQuestionDetail> query = em.createNamedQuery(AnsweredQuestionDetail.GET_BY_ID,
				AnsweredQuestionDetail.class);
		query.setParameter("id", answerQuestionDetailId);
		return query.getResultStream().findFirst().orElse(null);
	}

	private void validate(AnsweredQuestionDetail answeredQuestionDetail) {
		if (Objects.isNull(answeredQuestionDetail)) {
			throw new AgileDeckException(AnsweredQuestionDetailMsgCodes.ANSWER_QUESTION_DETAIL_NOT_FOUND);
		}
	}

	public List<AnsweredQuestionDetail> getAllByAnsweredQuestionId(Long id) {
		TypedQuery<AnsweredQuestionDetail> query = em.createNamedQuery(
				AnsweredQuestionDetail.GET_ALL_OF_PLAYING_ANSWERED_QUESTION, AnsweredQuestionDetail.class);
		query.setParameter("id", id);
		return query.getResultList();
	}

	public AnsweredQuestionDetail rejoin(AnsweredQuestion currentQuestion, Player player) {
		TypedQuery<AnsweredQuestionDetail> query = em.createNamedQuery(
				AnsweredQuestionDetail.GET_BY_ANSWER_QUESTION_AND_PLAYER, AnsweredQuestionDetail.class);
		query.setParameter("answeredQuestionId", currentQuestion.getId());
		query.setParameter("playerId", player.getId());
		return query.getResultStream().findFirst().orElse(null);
	}

	public List<Player> getAllPlayers(Long id) {
		TypedQuery<Player> query = em.createNamedQuery(AnsweredQuestionDetail.GET_ALL_PLAYERS, Player.class);
		query.setParameter("id", id);
		return query.getResultList();
	}

	public void resetAnswer(Long ansQuestId) {
		List<AnsweredQuestionDetail> answeredQuestionDetails = getAllByAnsweredQuestionId(ansQuestId);
		answeredQuestionDetails.forEach(aqd -> {
			aqd.setAnswer(null);
			em.merge(aqd);
		});
	}
}
