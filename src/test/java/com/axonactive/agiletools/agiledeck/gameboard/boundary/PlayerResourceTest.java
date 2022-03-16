package com.axonactive.agiletools.agiledeck.gameboard.boundary;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


@QuarkusTest
class PlayerResourceTest {

    @Test
    public void whenPlayerChangeName_thenReturnNewName() {
        Response response = RestAssured.given()
                .pathParam("id", 1)
                .queryParam("name", "Mr. Apple")
                .when()
                .put("players/{id}");

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals("Mr. Apple", response.getBody().jsonPath().getString("name"));
    }
}