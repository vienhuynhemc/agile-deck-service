package com.axonactive.agiletools.agiledeck.gameboard.boundary;

import java.util.Arrays;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;

import com.axonactive.agiletools.agiledeck.gameboard.entity.AnsweredQuestionDetail;
import com.axonactive.agiletools.agiledeck.gameboard.entity.Player;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;

@QuarkusTest
public class AnsweredQuestionDetailResourceTest {
    public static final String ANSWERED_QUESTION_DETAIL_PATH = "answeredquestiondetails";

    @Test
    void when_GetAllAnsweredQuestion_thenReturnListOfAnsweredQuestionOfPlayers() {
        Response response = RestAssured.given().pathParam("id", 1).when().get(ANSWERED_QUESTION_DETAIL_PATH + "/{id}")
                .then().assertThat().statusCode(200).extract().response();

        AnsweredQuestionDetail[] answeredQuestionDetailArray = response.as(AnsweredQuestionDetail[].class);
        List<AnsweredQuestionDetail> answeredQuestionDetailList = Arrays.asList(answeredQuestionDetailArray);

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertTrue(answeredQuestionDetailList.size() > 0);
    }

    @Test
    void when_GetAllWithAnsweredQuestionHasNoAnsweredQuestionDetails_thenReturnEmptyList() {
        Response response = RestAssured.given().pathParam("id", 0).when().get(ANSWERED_QUESTION_DETAIL_PATH + "/{id}")
                .then().assertThat().statusCode(200).extract().response();

        AnsweredQuestionDetail[] answeredQuestionDetailArray = response.as(AnsweredQuestionDetail[].class);
        List<AnsweredQuestionDetail> answeredQuestionDetailList = Arrays.asList(answeredQuestionDetailArray);

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals(0, answeredQuestionDetailList.size());
    }

    @Test
    void when_GetAllPlayers_thenReturnListPlayers() {
        Response response = RestAssured.given().pathParam("id", 1).when()
                .get(ANSWERED_QUESTION_DETAIL_PATH + "/players/{id}").then().assertThat().statusCode(200).extract()
                .response();

        Player[] playerArray = response.as(Player[].class);
        List<Player> playerList = Arrays.asList(playerArray);

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertTrue(playerList.size() > 0);

    }

    @Test
    void when_GetAllPlayersWithoutAnsweredQuestion_thenReturnEmptyList() {
        Response response = RestAssured.given().pathParam("id", 0).when()
                .get(ANSWERED_QUESTION_DETAIL_PATH + "/players/{id}").then().assertThat().statusCode(200).extract()
                .response();

        Player[] playerArray = response.as(Player[].class);
        List<Player> playerList = Arrays.asList(playerArray);

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals(0, playerList.size());

    }

    @Test
    void when_ResetAnswerQuestion_thenReturnStatus() {
        RestAssured.given().pathParam("currentQuestId", 1).when()
                .put(ANSWERED_QUESTION_DETAIL_PATH + "/reset/{currentQuestId}").then().statusCode(200);
    }

    @Test
    void when_ResetAnswerQuestionWithNoneId_thenReturnStatusOK() {
        RestAssured.given().pathParam("currentQuestId", -1).when()
                .put(ANSWERED_QUESTION_DETAIL_PATH + "/reset/{currentQuestId}").then().statusCode(200);
    }

    @Test
    void when_UpdateAnswerQuestionDetailWithNoneId_thenReturnBadRequest() {
        RestAssured.given().pathParam("currentQuestId", 0).header("Content-Type", "application/json").body("test")
                .when().put(ANSWERED_QUESTION_DETAIL_PATH + "/{currentQuestId}").then().statusCode(400);
    }

    @Test
    void when_UpdateAnswerQuestionDetailWithId_thenReturnStatusOk() {
        JsonObject answerContent = Json.createObjectBuilder()
                .add("content", Json.createObjectBuilder().add("content", "New Problem1").build()).build();
        RestAssured.given().pathParam("currentQuestId", 2).header("Content-Type", "application/json")
                .body(answerContent).when().put(ANSWERED_QUESTION_DETAIL_PATH + "/{currentQuestId}").then().assertThat()
                .statusCode(200);

    }

}
