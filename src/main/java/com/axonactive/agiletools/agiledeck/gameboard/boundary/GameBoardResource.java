package com.axonactive.agiletools.agiledeck.gameboard.boundary;

import com.axonactive.agiletools.agiledeck.AgileDeckException;
import com.axonactive.agiletools.agiledeck.file.control.FileService;
import com.axonactive.agiletools.agiledeck.game.control.AnswerService;
import com.axonactive.agiletools.agiledeck.game.control.QuestionService;
import com.axonactive.agiletools.agiledeck.game.entity.Answer;
import com.axonactive.agiletools.agiledeck.game.entity.Game;
import com.axonactive.agiletools.agiledeck.game.entity.Question;
import com.axonactive.agiletools.agiledeck.gameboard.control.*;
import com.axonactive.agiletools.agiledeck.gameboard.dto.QuestionWithStatusDTO;
import com.axonactive.agiletools.agiledeck.gameboard.entity.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Path("/gameboards")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Transactional
public class GameBoardResource {

    @Inject
    GameBoardService gameBoardService;

    @Inject
    PlayerService playerService;

    @Inject
    QuestionService questionService;

    @Inject
    AnsweredQuestionDetailService answeredQuestionDetailService;

    @Inject
    AnsweredQuestionService answeredQuestionService;

    @Inject
    AnswerService answerService;

    @Inject
    CustomAnswerService customAnswerService;

    @Inject
    FileService fileService;

    @Context
    UriInfo uriInfo;

    @PUT
    public Response create(@QueryParam("game") Long gameId) {
        GameBoard gameBoard = gameBoardService.create(gameId);
        List<Question> questions = questionService.getAllByGameID(gameId, null);
//		Question question = questionService.random(questions, gameBoard.getId());
//		answeredQuestionService.create(question, gameBoard);
        // Lấy câu hỏi đầu tiên ----------------------------
        answeredQuestionService.create(questions.get(0), gameBoard);
        // -------------------------------------------------
        URI location = this.uriInfo.getAbsolutePathBuilder().path(String.valueOf(gameBoard.getCode())).build();
        return Response.created(location).entity(gameBoard).build();
    }

    @GET
    @Path("/join/{code}")
    public Response join(@PathParam("code") String code) {

        AnsweredQuestion currentQuestion = gameBoardService.join(code);
        Player player = playerService.create(code);

        AnsweredQuestionDetail answeredQuestionDetail = answeredQuestionDetailService.create(currentQuestion, player);
        GameBoard gameBoard = gameBoardService.getByCode(code);
        Long gameId = gameBoard.getGame().getId();
        // Lấy tất cả các question của game này theo ID để bên user khi join game mà hiển thị ra -----
        List<Question> questions = questionService.getAllByGameID(gameId, null);
        List<AnsweredQuestion> answeredQuestions = answeredQuestionService.findByGameBoardId(gameBoard.getId());
        List<QuestionWithStatusDTO> questionWithStatusDTOs = new ArrayList<>();
        // Update question xem thử câu này được trả lời chưa, câu nào thì đang được chơi
        for (Question question : questions) {
            boolean isPlayed = false;
            boolean isPlaying = false;
            for (AnsweredQuestion answeredQuestion : answeredQuestions) {
                if (question.getId().equals(answeredQuestion.getQuestion().getId())) {
                    isPlayed = answeredQuestion.isPlayed();
                    isPlaying = answeredQuestion.isPlaying();
                    break;
                }
            }
            questionWithStatusDTOs.add(new QuestionWithStatusDTO(question, isPlayed, isPlaying));
        }
        // -------------------------------------------------------------------------------------------
        List<CustomAnswer> customAnswers = customAnswerService.getByGameBoardId(gameBoardService.getByCode(code));
        if (customAnswers.isEmpty()) {
            answeredQuestionDetail.getAnsweredQuestion().setAnswerOptions(answerService.getByGame(gameId));
        } else {
            answeredQuestionDetail.getAnsweredQuestion().setCustomAnswersOptions(customAnswers);
        }

        boolean isLastOne = false;
//		List<Question> listQuestion = questionService.getAllByGameID(gameId, code);
//		try {
//			questionService.random(listQuestion, gameBoardService.getByCode(code).getId());
//		} catch (AgileDeckException agileDeckException) {
//			isLastOne = true;
//		}
		// Kiểm tra isLastOne ------------------------------
		int count = 0;
		for(QuestionWithStatusDTO questionWithStatusDTO: questionWithStatusDTOs){
			if(!questionWithStatusDTO.isPlayed()){
				break;
			}else{
				count++;
			}
		}
		if(count == questionWithStatusDTOs.size()-1){
			isLastOne =true;
		}
		// -------------------------------------------------
        Map<String, Object> data = new ConcurrentHashMap<>();
        data.put("answeredQuestionDetail", answeredQuestionDetail);
        data.put("isLastOne", isLastOne);
        // Thêm list question để bên user khi join game hiển thị ra ------------------
        data.put("questions", questionWithStatusDTOs);
        // ---------------------------------------------------------------------------
        return Response.ok(data).build();
    }

    @GET
    @Path("/rejoin/{code}")
    public Response rejoin(@PathParam("code") String code, @QueryParam("playerId") Long playerId) {
        AnsweredQuestion currentQuestion = gameBoardService.join(code);

        Player player = playerService.findById(playerId);

        AnsweredQuestionDetail answeredQuestionDetail = answeredQuestionDetailService.rejoin(currentQuestion, player);
        if (Objects.isNull(answeredQuestionDetail)) {
            answeredQuestionDetail = answeredQuestionDetailService.create(currentQuestion, player);
        }

        GameBoard gameBoard = gameBoardService.getByCode(code);
        Long gameId = gameBoard.getGame().getId();
        // Lấy tất cả các question của game này theo ID để bên user khi join game mà hiển thị ra -----
        List<Question> questions = questionService.getAllByGameID(gameId, null);
        List<AnsweredQuestion> answeredQuestions = answeredQuestionService.findByGameBoardId(gameBoard.getId());
        List<QuestionWithStatusDTO> questionWithStatusDTOs = new ArrayList<>();
        // Update question xem thử câu này được trả lời chưa, câu nào thì đang được chơi
        for (Question question : questions) {
            boolean isPlayed = false;
            boolean isPlaying = false;
            for (AnsweredQuestion answeredQuestion : answeredQuestions) {
                if (question.getId().equals(answeredQuestion.getQuestion().getId())) {
                    isPlayed = answeredQuestion.isPlayed();
                    isPlaying = answeredQuestion.isPlaying();
                    break;
                }
            }
            questionWithStatusDTOs.add(new QuestionWithStatusDTO(question, isPlayed, isPlaying));
        }
        // -------------------------------------------------------------------------------------------

        List<CustomAnswer> customAnswers = customAnswerService.getByGameBoardId(gameBoard);
        if (customAnswers.isEmpty()) {
            answeredQuestionDetail.getAnsweredQuestion().setAnswerOptions(answerService.getByGame(gameId));
        } else {
            answeredQuestionDetail.getAnsweredQuestion().setCustomAnswersOptions(customAnswers);
        }

        boolean isLastOne = false;
//        List<Question> listQuestion = questionService.getAllByGameID(gameId, code);
//        try {
//            questionService.random(listQuestion, gameBoardService.getByCode(code).getId());
//        } catch (AgileDeckException ade) {
//            isLastOne = true;
//        }
		// Kiểm tra isLastOne ------------------------------
		int count = 0;
		for(QuestionWithStatusDTO questionWithStatusDTO: questionWithStatusDTOs){
			if(!questionWithStatusDTO.isPlayed()){
				break;
			}else{
				count++;
			}
		}
		if(count == questionWithStatusDTOs.size()-1){
			isLastOne =true;
		}
		// -------------------------------------------------

        Map<String, Object> data = new ConcurrentHashMap<>();
        data.put("answeredQuestionDetail", answeredQuestionDetail);
        data.put("isLastOne", isLastOne);
        // Thêm list question để bên user khi join game hiển thị ra ------------------
        data.put("questions", questionWithStatusDTOs);
        // ---------------------------------------------------------------------------
        return Response.ok(data).build();
    }

    @GET
    @Path("/history/{code}")
    public Response getHistory(@PathParam("code") String code) {
        GameBoard currentGameBoard = gameBoardService.getByCode(code);
        gameBoardService.validate(currentGameBoard);

        Map<String, Object> history = new LinkedHashMap<>();
        List<AnsweredQuestion> questionList = answeredQuestionService.findByGameBoardId(currentGameBoard.getId());
        questionList.forEach(answeredQuestion -> {
            List<AnsweredQuestionDetail> answerList = answeredQuestionDetailService
                    .getAllByAnsweredQuestionId(answeredQuestion.getId()).stream()
                    .filter(answeredQuestionDetail -> Objects.nonNull(answeredQuestionDetail.getAnswer()))
                    .collect(Collectors.toList());

            history.put(answeredQuestion.getContent().getContent() + "<<>>" + answeredQuestion.getId(), answerList);
        });

        return Response.ok(history).build();
    }

    @PUT
    @Path("/add-answer/{code}")
    public Response addNewAnswer(@PathParam("code") String code, Answer answer) {

        GameBoard gameBoard = gameBoardService.getByCode(code);
        gameBoardService.validate(gameBoard);
        gameBoardService.validateLengthListAnswerOfGameBoard(gameBoard);

        if (gameBoardService.validateGameBoardInCustomAnswer(gameBoard)) {
            customAnswerService.addAnswerOfGameBoard(answer, gameBoard);
            return Response.ok().build();
        }

        Game game = gameBoard.getGame();
        List<Answer> defaultAnswer = answerService.getByGame(game.getId());
        customAnswerService.createNewCustomAnswerOfGameBoard(defaultAnswer, gameBoard);
        customAnswerService.addAnswerOfGameBoard(answer, gameBoard);
        return Response.ok().build();
    }

    @PUT
    @Path("/update-answer-content/{code}")
    public Response updateAnswerContent(@PathParam("code") String code, CustomAnswer customAnswer) {

        GameBoard gameBoard = gameBoardService.getByCode(code);
        gameBoardService.validate(gameBoard);

        if (gameBoardService.validateGameBoardInCustomAnswer(gameBoard)) {
            customAnswerService.editContent(customAnswer);
            return Response.ok().build();
        }

        Game game = gameBoard.getGame();
        List<Answer> defaultAnswer = answerService.getByGame(game.getId());
        List<Answer> answerCopy = new ArrayList<>();
        defaultAnswer.forEach(answer -> {
            if (answer.getId() == customAnswer.getId()) {
                Answer item = new Answer(customAnswer.getContent(), answer.getNumberOrder(), answer.getAnswerGroup(),
                        answer.getGame(), answer.getQuestion());
                answerCopy.add(item);
            } else {
                answerCopy.add(answer);
            }
        });

        customAnswerService.createNewCustomAnswerOfGameBoard(answerCopy, gameBoard);

        return Response.ok().build();
    }

    @DELETE
    @Path("/delete-answer/{code}")
    public Response deleteAnswer(@PathParam("code") String code, @QueryParam("answerId") Long customAnswerId) {
        customAnswerService.delete(customAnswerId);
        return Response.ok().build();
    }

}
