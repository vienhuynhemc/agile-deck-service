package com.axonactive.agiletools.agiledeck.gameboard.boundary;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.axonactive.agiletools.agiledeck.game.entity.Question;
import com.axonactive.agiletools.agiledeck.gameboard.control.AnsweredQuestionService;
import com.axonactive.agiletools.agiledeck.gameboard.control.GameBoardService;
import com.axonactive.agiletools.agiledeck.gameboard.entity.AnsweredQuestion;
import com.axonactive.agiletools.agiledeck.gameboard.entity.GameBoard;

@Path("/answeredquestions")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@Transactional
public class AnsweredQuestionResource {

    @Inject
    AnsweredQuestionService answeredQuestionService;
    @Inject
    GameBoardService gameBoardService;

    @PUT
    @Path("{code}")
    public Response update(@PathParam("code") String gameBoardCode, AnsweredQuestion newAnsweredQuestion) {
        GameBoard gameBoard = gameBoardService.getByCode(gameBoardCode);
        gameBoardService.validate(gameBoard);
        return Response.ok(answeredQuestionService.update(gameBoard, newAnsweredQuestion)).build();
    }

    @POST
    @Path("{Code}")
    public Response add(@PathParam("Code") String gameBoardCode, AnsweredQuestion newAnsweredQuestion) {

        GameBoard gameBoard = gameBoardService.getByCode(gameBoardCode);
        AnsweredQuestion previousAnsweredQuestion = answeredQuestionService.findCurrentPLaying(gameBoard.getId());
        previousAnsweredQuestion.setPlaying(false);
        answeredQuestionService.updateStatusPlaying(previousAnsweredQuestion);
        Question question = new Question();
        question.setContent(newAnsweredQuestion.getContent());
        question.setGame(gameBoard.getGame());
        question.setGameBoard(gameBoard);

        AnsweredQuestion answeredQuestion = answeredQuestionService.create(question, gameBoard);
        return Response.ok(answeredQuestion).build();
    }

    @PUT
    @Path("/{code}/updatePlayedQuestion")
    public Response updatePlayedQuestion(@PathParam("code") String gameBoardCode) {
        try {
            GameBoard gameBoard = gameBoardService.getByCode(gameBoardCode);
            gameBoardService.validate(gameBoard);
            return Response.ok(answeredQuestionService.updatePlayedQuestion(gameBoard)).build();
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST).build();
        }

    }

    @GET
    @Path("/{code}/all")
    public Response getQuestions(@PathParam("code") String code) {

        GameBoard gameBoard = gameBoardService.getByCode(code);
        try {
            gameBoardService.validate(gameBoard);
            List<AnsweredQuestion> listAnsweredQuestion = answeredQuestionService.findByGameBoardId(gameBoard.getId());
            Map<String, Object> data = new ConcurrentHashMap<>();
            data.put("answeredQuestions", listAnsweredQuestion);
            return Response.ok(data).build();
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST).build();
        }

    }
}
