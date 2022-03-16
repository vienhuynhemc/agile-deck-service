package com.axonactive.agiletools.agiledeck.game.entity;

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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "tbl_answers")
@NamedQueries({
    @NamedQuery( name = Answer.FIND_BY_GAME, query = "SELECT ans FROM Answer ans WHERE ans.game.id = :gameId ORDER BY ans.numberOrder")
})
public class Answer {

    private static final String QUALIFIER = "com.axonactive.agiletools.agiledeck.game.entity.";
    
    public static final String FIND_BY_GAME = QUALIFIER + "findByGame";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private AnswerContent content;

    @Column(name = "number_order", nullable = false)
    private Integer numberOrder;

    @ManyToOne
    @JoinColumn(name = "answer_group_id", nullable = false)
    private AnswerGroup answerGroup;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    public Answer(Long id) {
        this.id = id;
    }

    public Answer(AnswerContent content, Integer numberOrder, AnswerGroup answerGroup, Game game, Question question) {
        this.content = content;
        this.numberOrder = numberOrder;
        this.answerGroup = answerGroup;
        this.game = game;
        this.question = question;
    }

}
