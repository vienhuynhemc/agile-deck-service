package com.axonactive.agiletools.agiledeck.gameboard.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.axonactive.agiletools.agiledeck.game.entity.Game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Table(name = "tbl_game_boards")
@Entity
@NamedQueries({ @NamedQuery(name = GameBoard.GET_BY_CODE, query = "SELECT gb FROM GameBoard gb WHERE gb.code = :code"),
		@NamedQuery(name = GameBoard.GET_BY_ID, query = "SELECT gb FROM GameBoard gb WHERE gb.id = :id") })
public class GameBoard {

	private static final String QUALIFIER = "com.axonactive.agiletools.agiledeck.play.entity.GameBoard";

	public static final String GET_BY_CODE = QUALIFIER + "getByCode";

	public static final String GET_BY_ID = QUALIFIER + "getById";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "code", nullable = false, unique = true, length = 50)
	private String code;

	@ManyToOne
	@JoinColumn(name = "game_id", nullable = false)
	private Game game;

	public GameBoard(String code, Game game) {
		this.code = code;
		this.game = game;
	}

}
