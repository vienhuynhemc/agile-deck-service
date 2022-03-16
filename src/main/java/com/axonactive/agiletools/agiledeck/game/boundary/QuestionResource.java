package com.axonactive.agiletools.agiledeck.game.boundary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.axonactive.agiletools.agiledeck.AgileDeckException;
import com.axonactive.agiletools.agiledeck.game.control.GameService;
import com.axonactive.agiletools.agiledeck.game.control.QuestionService;
import com.axonactive.agiletools.agiledeck.game.entity.Game;
import com.axonactive.agiletools.agiledeck.game.entity.Question;
import com.axonactive.agiletools.agiledeck.game.entity.QuestionMsgCodes;
import com.axonactive.agiletools.agiledeck.gameboard.control.AnsweredQuestionService;
import com.axonactive.agiletools.agiledeck.gameboard.control.GameBoardService;
import com.axonactive.agiletools.agiledeck.gameboard.dto.QuestionWithStatusDTO;
import com.axonactive.agiletools.agiledeck.gameboard.entity.AnsweredQuestion;
import com.axonactive.agiletools.agiledeck.gameboard.entity.GameBoard;

@Path("/questions")
@Transactional
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class QuestionResource {

	@Inject
	QuestionService questionService;

	@Inject
	GameService gameService;

	@Inject
	GameBoardService gameBoardService;

	@Inject
	AnsweredQuestionService answeredQuestionService;

	@GET
	@Path("/{code}")
	public Response getQuestion(@PathParam("code") String code) {
		boolean isLastOne = false;
		GameBoard gameBoard = gameBoardService.getByCode(code);
		if (Objects.isNull(gameBoard))
			return Response.status(Status.BAD_REQUEST).build();
		Game game = gameService.findById(gameBoard.getGame().getId());

		AnsweredQuestion currentAnsweredQuestion = answeredQuestionService.findCurrentPLaying(gameBoard.getId());
		currentAnsweredQuestion.setPlaying(false);
		currentAnsweredQuestion.setPlayed(true);
		answeredQuestionService.updateStatusPlaying(currentAnsweredQuestion);

		List<Question> listQuestion = questionService.getAllByGameID(game.getId(), code);
		// Lấy câu hỏi tiếp theo mà không cần random -----------
		List<AnsweredQuestion> answeredQuestions = answeredQuestionService.findByGameBoardId(gameBoard.getId());
		List<QuestionWithStatusDTO> questionWithStatusDTOs = new ArrayList<>();
		// Update question xem thử câu này được trả lời chưa, câu nào thì đang được chơi
		for (Question question : listQuestion) {
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
		Question question = null;
		for(QuestionWithStatusDTO questionWithStatusDTO : questionWithStatusDTOs){
			if(!questionWithStatusDTO.isPlayed()){
				question = questionWithStatusDTO.getQuestion();
				break;
			}
		}
		// -----------------------------------------------------
//		Question question = questionService.random(listQuestion, gameBoard.getId());
		AnsweredQuestion newAnsweredQuestion = answeredQuestionService.create(question, gameBoard);
//		try {
//			questionService.random(listQuestion, gameBoard.getId());
//		} catch (AgileDeckException ade) {
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
		data.put("answeredQuestion", newAnsweredQuestion);
		data.put("isLastOne", isLastOne);
		return Response.ok(data).build();
	}

	// ERROR
	@GET
	@Path("/{code}/order")
	public Response getQuestionOrder(@PathParam("code") String code) {
		boolean isLastOne = false;
		GameBoard gB = gameBoardService.getByCode(code);

		if (Objects.isNull(gB))
			return Response.status(Status.BAD_REQUEST).build();
		Game g = gameService.findById(gB.getGame().getId());

		List<AnsweredQuestion> answeredQuestionsWithNotNullQuestion = answeredQuestionService
				.findNotNullQuestionId(gB.getId());

		AnsweredQuestion currentAnsweredQuestion = answeredQuestionService.findCurrentPLaying(gB.getId());
		currentAnsweredQuestion.setPlaying(false);
		answeredQuestionService.updateStatusPlaying(currentAnsweredQuestion);

		List<Question> listQuestion = questionService.getAllByGameID(g.getId(), code);
		listQuestion.remove(0);
		Question nextQuestion = null;
		if (answeredQuestionsWithNotNullQuestion.isEmpty() || answeredQuestionsWithNotNullQuestion.size() - 1 == 0) {
			nextQuestion = listQuestion.get(0);
		} else {
			final int latestQuestionId = answeredQuestionsWithNotNullQuestion.size() - 1;
			if (latestQuestionId > listQuestion.size())
				throw new AgileDeckException(QuestionMsgCodes.NO_QUESTIONS_LEFT);
			else if (latestQuestionId == listQuestion.size() - 1)
				isLastOne = true;
			nextQuestion = listQuestion.get(latestQuestionId);
		}
		Map<String, Object> data = new ConcurrentHashMap<>();
		data.put("answeredQuestion", answeredQuestionService.create(nextQuestion, gB));
		data.put("isLastOne", isLastOne);
		return Response.ok(data).build();
	}

	@POST
	public Response createQuestions(@QueryParam("gameBoardCode") String gameBoardCode, List<Question> questions) {
		GameBoard gameBoard = gameBoardService.getByCode(gameBoardCode);
		gameBoardService.validate(gameBoard);
		questionService.createQuestion(questions, gameBoard);

		return Response.ok().build();
	}

	@GET
	@Path("/{code}/all")
	public Response getQuestions(@PathParam("code") String code) {

		GameBoard gB = gameBoardService.getByCode(code);
		if (Objects.isNull(gB))
			return Response.status(Status.BAD_REQUEST).build();
		Game g = gameService.findById(gB.getGame().getId());

		List<Question> listQuestion = questionService.getAllByGameID(g.getId(), code);

		Map<String, Object> data = new ConcurrentHashMap<>();
		data.put("questions", listQuestion);
		return Response.ok(data).build();
	}

}
