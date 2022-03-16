package com.axonactive.agiletools.agiledeck.game.entity;

import com.axonactive.agiletools.agiledeck.gameboard.entity.GameBoard;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tbl_questions")
@NamedQueries({
        @NamedQuery(name = Question.GET_ALL_BY_GAME_ID, query = "SELECT q FROM Question q WHERE q.game.id = :gameId") })
public class Question {

    private static final String QUALIFIER = "com.axonactive.agiletools.agiledeck.game.entity.Question";

    public static final String GET_ALL_BY_GAME_ID = QUALIFIER + "getAllByGameId";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne
    @JoinColumn(name = "game_board_id")
    private GameBoard gameBoard;

    @Embedded
    private QuestionContent content;

}
