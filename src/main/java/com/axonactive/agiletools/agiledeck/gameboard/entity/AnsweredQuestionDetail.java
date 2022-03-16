package com.axonactive.agiletools.agiledeck.gameboard.entity;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.axonactive.agiletools.agiledeck.game.entity.AnswerContent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "tbl_answered_question_details")
@Entity
@NoArgsConstructor
@Getter
@Setter
@NamedQueries({
        @NamedQuery(name = AnsweredQuestionDetail.GET_BY_ID, query = "SELECT aqd FROM AnsweredQuestionDetail aqd WHERE aqd.id = :id"),
        @NamedQuery(name = AnsweredQuestionDetail.GET_ALL_OF_PLAYING_ANSWERED_QUESTION, query = "SELECT aqd FROM AnsweredQuestionDetail aqd WHERE answeredQuestion.id = :id"),
        @NamedQuery(name = AnsweredQuestionDetail.GET_BY_ANSWER_QUESTION_AND_PLAYER, query = "SELECT aqd FROM AnsweredQuestionDetail aqd "
                + "WHERE aqd.player.id = :playerId AND aqd.answeredQuestion.id = :answeredQuestionId"),
        @NamedQuery(name = AnsweredQuestionDetail.GET_ALL_PLAYERS, query = "SELECT player FROM AnsweredQuestionDetail aqd WHERE aqd.answeredQuestion.id = :id"),
        @NamedQuery(name = AnsweredQuestionDetail.GET_ALL_BY_NULL_ANS, query = "SELECT aqd FROM AnsweredQuestionDetail aqd WHERE answer = null")})
public class AnsweredQuestionDetail {

    private static final String QUALIFIER = "com.axonactive.agiletools.agiledeck.gameboard.entity.AnsweredQuestionDetail";

    public static final String GET_BY_ID = QUALIFIER + "getById";
    public static final String GET_ALL_OF_PLAYING_ANSWERED_QUESTION = QUALIFIER + "getAllOfPlayingAnsweredQuestion";
    public static final String GET_BY_ANSWER_QUESTION_AND_PLAYER = QUALIFIER + "getByAnswerQuestionAndPlayer";
    public static final String GET_ALL_PLAYERS = QUALIFIER + "getAllPlayers";
    public static final String GET_ALL_BY_NULL_ANS = QUALIFIER + "getAllByNullAns";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "answered_question_id")
    private AnsweredQuestion answeredQuestion;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @Embedded
    private AnswerContent answer;

    public AnsweredQuestionDetail(AnsweredQuestion answeredQuestion, Player player) {
        this.answeredQuestion = answeredQuestion;
        this.player = player;
    }

}
