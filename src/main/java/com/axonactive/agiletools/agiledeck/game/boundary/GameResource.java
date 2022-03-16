package com.axonactive.agiletools.agiledeck.game.boundary;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.axonactive.agiletools.agiledeck.game.control.GameService;
import com.axonactive.agiletools.agiledeck.game.entity.Game;

@Path("/games")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class GameResource {
    
    @Inject
    GameService gameService;

    @GET
    public Response getAllGame(){
        List<Game> listGame = gameService.getInformationGame();
        return Response.ok(listGame).build();
    }
}
