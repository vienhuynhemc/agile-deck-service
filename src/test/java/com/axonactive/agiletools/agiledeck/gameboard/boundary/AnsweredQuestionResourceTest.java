package com.axonactive.agiletools.agiledeck.gameboard.boundary;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;

@QuarkusTest
public class AnsweredQuestionResourceTest {
	private static final String ANSWERED_QUESTION_PATH = "answeredquestions";

	@Test
	void whenAddNewQuestion_thenReturnNewQuestion() {
		JsonObject answerContent = Json.createObjectBuilder()
				.add("content", Json.createObjectBuilder().add("content", "Add new problem").build()).build();

		Response response = RestAssured.given().pathParam("gameBoardCode", "asd6gfga-f296-sdf3-0fn2-asf86gc1crt2")
				.header("Content-Type", "application/json").body(answerContent).when()
				.post(ANSWERED_QUESTION_PATH + "/{gameBoardCode}");
		Assertions.assertEquals(200, response.getStatusCode());
	}

	@Test
	void whenChangeNameQuestion_thenReturnNewNameQuestion() {
		JsonObject answerContent = Json.createObjectBuilder()
				.add("content", Json.createObjectBuilder().add("content", "New Problem").build()).build();

		Response res = RestAssured.given().pathParam("id", "asd6gfga-f296-sdf3-0fn2-asf86gc1crt2")
				.header("Content-Type", "application/json").body(answerContent).when()
				.put(ANSWERED_QUESTION_PATH + "/{id}");

		Assertions.assertEquals(200, res.getStatusCode());

	}

	@Test
	void when_getAllAnsweredQuestions_thenReturnStatusOK() {

		Response res = RestAssured.given().pathParam("code", "e3bb8a9d-704e-430e-acae-1fb0a9695205")
				.header("Content-Type", "application/json").when().get(ANSWERED_QUESTION_PATH + "/{code}/all");

		Assertions.assertEquals(200, res.getStatusCode());

	}

	@Test
	void when_getAllAnsweredQuestionsWithWrongCode_thenReturnBadRequest() {

		Response res = RestAssured.given().pathParam("code", "example-code").header("Content-Type", "application/json")
				.when().get(ANSWERED_QUESTION_PATH + "/{code}/all");

		Assertions.assertEquals(400, res.getStatusCode());

	}

	@Test
	void whenUpdatePlayedQuestion_thenReturnNewUpdatedQuestion() {

		Response res = RestAssured.given().pathParam("code", "asd6gfga-f296-sdf3-0fn2-asf86gc1crt2")
				.header("Content-Type", "application/json").when()
				.put(ANSWERED_QUESTION_PATH + "/{code}/updatePlayedQuestion");
		Assertions.assertEquals(Status.OK.getStatusCode(), res.getStatusCode());
		String responseBody = res.getBody().asString();
		int isPlayedPosition = responseBody.indexOf("isPlayed");
		String substringContainIsPlayed = responseBody.substring(isPlayedPosition, isPlayedPosition + 14).replace("\"",
				"");
		Assertions.assertEquals("isPlayed:true", substringContainIsPlayed);

	}
}
