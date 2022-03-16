package com.axonactive.agiletools.agiledeck.game.boundary;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

@QuarkusTest
public class GameResourceTest {

	@Test
	public void when_GoToHomePage_thenReturnInformationGames() {
		RestAssured.given().when().get("/games").then().statusCode(200);
	}

	@Test
	public void when_ViewHistoryOfGameBoard_thenReturnHistoryOfGameBoard() {
		RestAssured.given().pathParam("code", "asd6gfga-f296-sdf3-0fn2-asf86gc1crt2").when()
				.get("gameboards/history/{code}").then().statusCode(200);
	}

}
