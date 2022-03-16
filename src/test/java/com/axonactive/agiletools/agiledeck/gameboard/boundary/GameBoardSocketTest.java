package com.axonactive.agiletools.agiledeck.gameboard.boundary;

import java.net.URI;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class GameBoardSocketTest {

	private static final LinkedBlockingDeque<String> MESSAGES = new LinkedBlockingDeque<>();

	@TestHTTPResource("/ws/asd6gfga-f296-sdf3-0fn2-asf86gc1crt2")
	URI uri;

	@Test
	public void testGameBoardSocket() throws Exception {

		String joinGameData = "{\"action\":\"join-game\",\"info\":{\"player\":{\"gameBoard\":{\"code\":\"asd6gfga-f296-sdf3-0fn2-asf86gc1crt2\",\"game\":{\"description\":\"The objective of the game is to make a decision as to how to best maximize the profit of this process\",\"gameAsImage\":\"nd_home.png\",\"gameBoardConfig\":{\"answerTitle\":\"Answers\",\"imageBackside\":\"nd_backside.png\",\"imagePlayerStart\":\"nd_playerstart.png\",\"playerTitle\":\"Players\",\"questionTitle\":\"Problems\"},\"id\":2,\"name\":\"New Deck\"},\"id\":3},\"id\":8,\"name\":\"Abiu\"},\"isLatestQuestion\":true}}";

		// String selectCardData =
		// "{\"action\":\"selected-card\",\"playerId\":11,\"selectedCardId\":\"2\"}";

		try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri)) {
			Assertions.assertEquals("CONNECT", MESSAGES.poll(5, TimeUnit.SECONDS));

			session.getAsyncRemote().sendText(joinGameData);
			Assertions.assertEquals("{\"isLastOne\":false,\"isFlip\":false,\"action\":\"init-data\"}",
					MESSAGES.poll(5, TimeUnit.SECONDS));

			// session.getAsyncRemote().sendText(selectCardData);
			// Assertions.assertEquals("{\"isLastOe\":true,\"isFlip\":false,\"action\":\"init-data\"}",
			// MESSAGES.poll(10, TimeUnit.SECONDS));

			// Assertions.assertEquals(
			// "{\"data\":[\"{\\\"player\\\":{\\\"gameBoard\\\":{\\\"code\\\":\\\"b4661d5e-f296-4cf6-887d-cfa0f97d1f36\\\",\\\"game\\\":{\\\"description\\\":\\\"The
			// objective of the game is to make a decision as to how to best maximize the
			// profit of this
			// process\\\",\\\"gameAsImage\\\":\\\"nd_home.png\\\",\\\"gameBoardConfig\\\":{\\\"answerTitle\\\":\\\"Answers\\\",\\\"imageBackside\\\":\\\"nd_backside.png\\\",\\\"imagePlayerStart\\\":\\\"nd_playerstart.png\\\",\\\"playerTitle\\\":\\\"Players\\\",\\\"questionTitle\\\":\\\"Problems\\\"},\\\"id\\\":2,\\\"name\\\":\\\"New
			// Deck\\\"},\\\"id\\\":3},\\\"id\\\":11,\\\"name\\\":\\\"Soursop\\\"},\\n\\\"selectedCardId\\\":null,\\n\\\"content\\\":null,\\n\\\"contentAsDescription\\\":null}\"],\"action\":\"join-game\"}",
			// MESSAGES.poll(10, TimeUnit.SECONDS));
			//
			// session.getAsyncRemote().sendText("{\"action\":\"flip-card\"}");
			// Assertions.assertEquals("{\"action\":\"flip-card\"}", MESSAGES.poll(10,
			// TimeUnit.SECONDS));
			//
			// session.getAsyncRemote().sendText("{\"action\":\"reset-answer\"}");
			// Assertions.assertEquals(
			// "{\"data\":[\"{\\\"player\\\":{\\\"gameBoard\\\":{\\\"code\\\":\\\"b4661d5e-f296-4cf6-887d-cfa0f97d1f36\\\",\\\"game\\\":{\\\"description\\\":\\\"The
			// objective of the game is to make a decision as to how to best maximize the
			// profit of this
			// process\\\",\\\"gameAsImage\\\":\\\"nd_home.png\\\",\\\"gameBoardConfig\\\":{\\\"answerTitle\\\":\\\"Answers\\\",\\\"imageBackside\\\":\\\"nd_backside.png\\\",\\\"imagePlayerStart\\\":\\\"nd_playerstart.png\\\",\\\"playerTitle\\\":\\\"Players\\\",\\\"questionTitle\\\":\\\"Problems\\\"},\\\"id\\\":2,\\\"name\\\":\\\"New
			// Deck\\\"},\\\"id\\\":3},\\\"id\\\":11,\\\"name\\\":\\\"Soursop\\\"},\\n\\\"selectedCardId\\\":null,\\n\\\"content\\\":null,\\n\\\"contentAsDescription\\\":null}\"],\"action\":\"join-game\"}",
			// MESSAGES.poll(10, TimeUnit.SECONDS));
			//
			// session.getAsyncRemote().sendText("{\"action\":\"next-question\"}");
			// Assertions.assertEquals("{\"isLastOne\":true,\"isFlip\":false,\"action\":\"init-data\"}",
			// MESSAGES.poll(10, TimeUnit.SECONDS));
			//
			// session.getAsyncRemote().sendText("{\"action\":\"update-player\"}");
			// Assertions.assertEquals("{\"action\":\"reset-answer\"}", MESSAGES.poll(10,
			// TimeUnit.SECONDS));
			//
			// session.getAsyncRemote().sendText("{\"action\":\"update-question\"}");
			// Assertions.assertEquals("{\"action\":\"update-question\"}", MESSAGES.poll(10,
			// TimeUnit.SECONDS));
		}
	}

	@Test
	public void whenUpdatedName() throws Exception {

		try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri)) {
			Assertions.assertEquals("CONNECT", MESSAGES.poll(10, TimeUnit.SECONDS));

			session.getAsyncRemote().sendText("{\"action\": \"update-player\",\"id\": 1,\"name\": \"Agile Tony\"}");
			Assertions.assertEquals("{\"isLastOne\":false,\"isFlip\":false,\"action\":\"init-data\"}",
					MESSAGES.poll(10, TimeUnit.SECONDS));

		}
	}

	@Test
	public void whenPlayerSelectedCard() throws Exception {

		try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri)) {
			Assertions.assertEquals("CONNECT", MESSAGES.poll(10, TimeUnit.SECONDS));

			session.getAsyncRemote()
					.sendText("{\"action\": \"selected-card\",\"playerId\": 1,\"selectedCardId\": \"bigbang.png\"}");
			Assertions.assertEquals("{\"isLastOne\":false,\"isFlip\":false,\"action\":\"init-data\"}",
					MESSAGES.poll(10, TimeUnit.SECONDS));

		}
	}

	@ClientEndpoint
	public static class Client {

		@OnOpen
		public void open(Session session) {
			MESSAGES.add("CONNECT");
			session.getAsyncRemote().sendText("_ready_");
		}

		@OnMessage
		public void message(String message) {
			MESSAGES.add(message);
		}
	}

}