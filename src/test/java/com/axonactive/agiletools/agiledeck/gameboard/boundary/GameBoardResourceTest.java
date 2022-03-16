package com.axonactive.agiletools.agiledeck.gameboard.boundary;

import javax.json.Json;
import javax.json.JsonObject;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;

@QuarkusTest
class GameBoardResourceTest {
	private static final String GAME_BOARD_PATH = "gameboards";

	@Test
	void whenCreateNewGameBoard_thenReturnGameNotFound() {
		RestAssured.given().queryParam("game", 1000).when().put(GAME_BOARD_PATH).then().statusCode(400)
				.header("MSG_CODE", CoreMatchers.is("GAME_NOT_FOUND"));
	}

	@Test
	void whenCreateNewGameBoard_thenReturnLocationInHeader() {
		Response response = RestAssured.given().queryParam("game", 1).when().put(GAME_BOARD_PATH);

		Assertions.assertEquals(201, response.getStatusCode());
		Assertions.assertNotNull(response.getHeader("Location"));
	}

	private static final String JOIN_GAME_BOARD_PATH = GAME_BOARD_PATH + "/join/{code}";

	@Test
	void whenJoinGame_thenReturnAnswerQuestionDetail() {
		Response response = RestAssured.given().pathParam("code", "e3bb8a9d-704e-430e-acae-1fb0a9695205").when()
				.get(JOIN_GAME_BOARD_PATH);

		Assertions.assertEquals(200, response.getStatusCode());
	}

	@Test
	void whenJoinGame_thenReturnAnswerQuestionDetailByCustomAnswer() {
		Response response = RestAssured.given().pathParam("code", "asd6gfga-f296-sdf3-0fn2-asf86gc1crt2").when()
				.get(JOIN_GAME_BOARD_PATH);

		Assertions.assertEquals(200, response.getStatusCode());
	}

	@Test
	void whenJoinGame_thenReturnAnswer() {
		RestAssured.given().pathParam("code", "code-not-found").when().get(JOIN_GAME_BOARD_PATH).then().statusCode(400)
				.header("MSG_CODE", CoreMatchers.is("GAME_BOARD_NOT_FOUND"));
	}

	private static final String REJOIN_GAME_BOARD_PATH = GAME_BOARD_PATH + "/rejoin/{code}";

	@Test
	void whenPlayerRejoinGame_thenReturnReturnAnswerQuestionDetail() {
		Response response = RestAssured.given().pathParam("code", "e3bb8a9d-704e-430e-acae-1fb0a9695205")
				.queryParam("playerId", 1).when().get(REJOIN_GAME_BOARD_PATH);

		Assertions.assertEquals(200, response.getStatusCode());
	}

	@Test
	void whenPlayerRejoinGame_thenReturnGameBoardNotFound() {
		RestAssured.given().pathParam("code", "code-not-found").queryParam("playerId", 5).when()
				.get(REJOIN_GAME_BOARD_PATH).then().statusCode(400)
				.header("MSG_CODE", CoreMatchers.is("GAME_BOARD_NOT_FOUND"));
	}

	@Test
	void whenPlayerRejoinGame_thenReturnPlayerNotFound() {
		RestAssured.given().pathParam("code", "e3bb8a9d-704e-430e-acae-1fb0a9695205").queryParam("playerId", -1).when()
				.get(REJOIN_GAME_BOARD_PATH).then().statusCode(400)
				.header("MSG_CODE", CoreMatchers.is("PLAYER_NOT_FOUND"));
	}

	@Test
	void whenPlayerRejoinGame_thenReturnNewAnswerQuestionDetail() {
		RestAssured.given().pathParam("code", "e3bb8a9d-704e-430e-acae-1fb0a9695205").queryParam("playerId", 1).when()
				.get(REJOIN_GAME_BOARD_PATH).then().statusCode(200);
	}

	@Test
	void whenPlayerRejoinGame_thenReturnDefaultAnswerQuestionDetail() {
		RestAssured.given().pathParam("code", "e3bb8a9d-704e-430e-acae-1fb0a96dsy76").queryParam("playerId", 1).when()
				.get(REJOIN_GAME_BOARD_PATH).then().statusCode(200);
	}

	private static final String ADD_ANSWER_GAME_BOARD_PATH = GAME_BOARD_PATH + "/add-answer/{code}";

	@Test
	void whenPlayerAddAnswer_thenReturnStatusOk() {
		JsonObject answerContent = Json.createObjectBuilder().add("content", Json.createObjectBuilder()
				.add("content", "Abc").add("contentAsDescription", "").add("contentAsImage", "abc.png").build())
				.build();

		Response response = RestAssured.given().pathParam("code", "e3bb8a9d-704e-430e-acae-1fb0a96rtfu8")
				.header("Content-Type", "application/json").body(answerContent).when().put(ADD_ANSWER_GAME_BOARD_PATH);

		Assertions.assertEquals(200, response.getStatusCode());
	}

	@Test
	void whenPlayerAddAnswerAtTheFirstTime_thenReturnStatusOk() {
		JsonObject answerContent = Json.createObjectBuilder().add("content", Json.createObjectBuilder()
				.add("content", "Abc").add("contentAsDescription", "").add("contentAsImage", "abc.png").build())
				.build();

		Response response = RestAssured.given().pathParam("code", "e3bb8a9d-704e-430e-acae-1fb0a96rtfu8")
				.header("Content-Type", "application/json").body(answerContent).when().put(ADD_ANSWER_GAME_BOARD_PATH);

		Assertions.assertEquals(200, response.getStatusCode());
	}

	@Test
	void whenPlayerAddAnswer_thenReturnGameBoardNotFound() {
		JsonObject answerContent = Json.createObjectBuilder().add("content", Json.createObjectBuilder()
				.add("content", "Abc").add("contentAsDescription", "").add("contentAsImage", "abc.png").build())
				.build();

		RestAssured.given().pathParam("code", "game-not-found").header("Content-Type", "application/json")
				.body(answerContent).when().put(ADD_ANSWER_GAME_BOARD_PATH).then().statusCode(400)
				.header("MSG_CODE", CoreMatchers.is("GAME_BOARD_NOT_FOUND"));
	}

	@Test
	void whenPlayerAddAnswer_thenReturnListAnswerOverLimitation() {
		JsonObject answerContent = Json.createObjectBuilder().add("content", Json.createObjectBuilder()
				.add("content", "Abc").add("contentAsDescription", "").add("contentAsImage", "abc.png").build())
				.build();

		RestAssured.given().pathParam("code", "asd6gfga-f296-sdf3-0fn2-asf86gc1crt2")
				.header("Content-Type", "application/json").body(answerContent).when().put(ADD_ANSWER_GAME_BOARD_PATH)
				.then().statusCode(400).header("MSG_CODE", CoreMatchers.is("LIST_ANSWER_OVER_LIMITATION"));
	}

	private static final String UPDATE_ANSWER_CONTENT_PATH = GAME_BOARD_PATH + "/update-answer-content/{code}";

	@Test
	void whenPlayerUpdateAnswer_thenReturnStatusOk() {
		JsonObject answerContent = Json.createObjectBuilder().add("content", Json.createObjectBuilder()
				.add("content", "Abc").add("contentAsDescription", "").add("contentAsImage", "abc.png").build())
				.add("id", 1).build();

		RestAssured.given().pathParam("code", "e3bb8a9d-704e-430e-acae-1fb0a9695205")
				.header("Content-Type", "application/json").body(answerContent).when().put(UPDATE_ANSWER_CONTENT_PATH)
				.then().statusCode(200);
	}

	@Test
	void whenPlayerUpdateAnswerAtTheFirstTime_thenReturnStatusOk() {
		JsonObject answerContent = Json.createObjectBuilder().add("content", Json.createObjectBuilder()
				.add("content", "Abc").add("contentAsDescription", "").add("contentAsImage", "abc.png").build())
				.add("id", 1).build();

		RestAssured.given().pathParam("code", "asd6gfga-f296-sdf3-0fn2-asf86gc1crt2")
				.header("Content-Type", "application/json").body(answerContent).when().put(UPDATE_ANSWER_CONTENT_PATH)
				.then().statusCode(200);
	}

	@Test
	void whenPlayerUpdateAnswer_thenReturnGameBoardNotFound() {
		JsonObject answerContent = Json.createObjectBuilder().add("content", Json.createObjectBuilder()
				.add("content", "Abc").add("contentAsDescription", "").add("contentAsImage", "abc.png").build())
				.add("id", 1).build();

		RestAssured.given().pathParam("code", "game-board-not-found").header("Content-Type", "application/json")
				.body(answerContent).when().put(UPDATE_ANSWER_CONTENT_PATH).then().statusCode(400)
				.header("MSG_CODE", CoreMatchers.is("GAME_BOARD_NOT_FOUND"));
	}

	private static final String DELETE_ANSWER_PATH = GAME_BOARD_PATH + "/delete-answer/{code}";

	@Test
	void whenPlayerDeleteAnswer_thenReturnStatusOk() {
		RestAssured.given().pathParam("code", "e3bb8a9d-704e-430e-acae-1fb0a9695205")
				.header("Content-Type", "application/json").queryParam("answerId", 20).when().delete(DELETE_ANSWER_PATH)
				.then().statusCode(200);
	}

	@Test
	void whenPlayerChooseAnswer_thenReturnAnswerQuestionDetail() {

		JsonObject answerContent = (JsonObject) Json.createObjectBuilder().add("content", "Bigbang")
				.add("contentAsImage", "bigbang.png").build();

		Response response = RestAssured.given().pathParam("answerQuestionDetailId", 5)
				.header("Content-Type", "application/json").body(answerContent).when()
				.put(AnsweredQuestionDetailResourceTest.ANSWERED_QUESTION_DETAIL_PATH + "/{answerQuestionDetailId}");

		Assertions.assertEquals(200, response.getStatusCode());
	}

	@Test
	void whenPlayerChooseAnswer_thenReturnNotFoundAnswerQuestionDetail() {

		JsonObject answerContent = (JsonObject) Json.createObjectBuilder().add("content", "Bigbang")
				.add("contentAsImage", "bigbang.png").build();

		RestAssured.given().pathParam("answerQuestionDetailId", -1).header("Content-Type", "application/json")
				.body(answerContent).when()
				.put(AnsweredQuestionDetailResourceTest.ANSWERED_QUESTION_DETAIL_PATH + "/{answerQuestionDetailId}")
				.then().statusCode(400).header("MSG_CODE", CoreMatchers.is("ANSWER_QUESTION_DETAIL_NOT_FOUND"));
	}
}