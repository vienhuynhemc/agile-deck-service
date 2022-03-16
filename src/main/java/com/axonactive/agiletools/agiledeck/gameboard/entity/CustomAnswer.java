package com.axonactive.agiletools.agiledeck.gameboard.entity;

import javax.persistence.Column;
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

@Getter 
@Setter
@NoArgsConstructor
@Entity
@Table( name = "tbl_custom_answers")
@NamedQueries({
    @NamedQuery( name = CustomAnswer.GET_BY_GAME_BOARD_ID, query = "SELECT ca FROM CustomAnswer ca WHERE ca.gameBoard.id = :gameBoardId ORDER BY ca.numberOrder"),
    @NamedQuery( name = CustomAnswer.GET_BY_ID, query = "SELECT ca FROM CustomAnswer ca WHERE ca.id = :customAnswerId"),
    @NamedQuery( name = CustomAnswer.GET_MAX_NUMBER_ORDER_BY_GAME_BOARD_ID, query = "SELECT MAX(ca.numberOrder) FROM CustomAnswer ca WHERE ca.gameBoard.id = :gameBoardId")
})
public class CustomAnswer {

    private static final String QUALIFIER = "com.axonactive.agiletools.agiledeck.gameboard.entity.";

    public static final String GET_BY_GAME_BOARD_ID = QUALIFIER + "getByGameBoardId";

    public static final String GET_BY_ID = QUALIFIER + "getById";

    public static final String GET_MAX_NUMBER_ORDER_BY_GAME_BOARD_ID = QUALIFIER + "getMaxNumberOrderByGameBoardId";
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private AnswerContent content;

    @Column(name = "number_order", nullable = false)
    private Integer numberOrder;

    @ManyToOne
    @JoinColumn(name = "game_board_id", nullable = false)
    private GameBoard gameBoard;

	public CustomAnswer(AnswerContent content, Integer numberOrder, GameBoard gameBoard) {
		this.content = content;
		this.numberOrder = numberOrder;
		this.gameBoard = gameBoard;
	}

}
