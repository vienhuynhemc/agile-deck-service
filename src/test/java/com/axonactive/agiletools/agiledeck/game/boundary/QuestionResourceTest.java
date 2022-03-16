package com.axonactive.agiletools.agiledeck.game.boundary;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

@QuarkusTest
public class QuestionResourceTest {
	private static final String QUESTION_PATH = "questions";

	@Test
	void when_clickNextScenario_thenReturnNewAnsweredQuestion() {
		RestAssured.given().pathParam("code", "asd6gfga-f296-sdf3-0fn2-asf86gc1crt2").when()
				.get(QUESTION_PATH + "/{code}").then().statusCode(200);
	}

	@Test
	void when_ClickNextScenarioWithNullGameBoardCode_thenReturnCode405() {
		RestAssured.given().pathParam("code", "").when().get(QUESTION_PATH + "/{code}").then().statusCode(405);
	}

	@Test
	void when_ClickNextScenarioWithNoneGameBoardCode_thenReturnBadRequest() {
		RestAssured.given().pathParam("code", "h").when().get(QUESTION_PATH + "/{code}").then().statusCode(400);
	}

	@Test
	void when_ClickNextScenario_thenReturnNewAnsweredQuestionByOrder() {
		RestAssured.given().pathParam("code", "asd6gfga-f296-sdf3-0fn2-asf86gc1crt2").when()
				.get(QUESTION_PATH + "/{code}/order").then().statusCode(200);
	}

	@Test
	void when_ClickNextScenarioWithWrongGameBoardCode_StatusBadRequest() {
		RestAssured.given().pathParam("code", "hihi").when().get(QUESTION_PATH + "/{code}/order").then()
				.statusCode(400);
	}

	@Test
	void when_addListQuestionIntoServer_thenReturnOkStatus() {
		String data = "[{\"content\":{\"content\":\"New Question 1\"}},{\"content\":{\"content\":\"New Question 2\"}},{\"content\":{\"content\":\"New Question 3\"}},{\"content\":{\"content\":\"New Question 4\"}},{\"content\":{\"content\":\"New Question 5\"}}]";
		RestAssured.given().queryParam("gameBoardCode", "asd6gfga-f296-sdf3-0fn2-asf86gc1crt2")
				.header("Content-Type", "application/json").body(data).when().post(QUESTION_PATH).then()
				.statusCode(200);
	}

	@Test
	void whenGetAllQuestions_thenReturnOk() {
		RestAssured.given().pathParam("code", "e3bb8a9d-704e-430e-acae-1fb0a9695205").when()
				.get(QUESTION_PATH + "/{code}/all").then().statusCode(200);
	}

	@Test
	void when_getAllQuestionsWithWrongGameBoardCode_thenReturnBadRequest() {
		RestAssured.given().pathParam("code", "hihi").when().get(QUESTION_PATH + "/{code}/all").then().statusCode(400);
	}
}
