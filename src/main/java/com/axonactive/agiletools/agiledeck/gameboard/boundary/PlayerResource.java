package com.axonactive.agiletools.agiledeck.gameboard.boundary;

import com.axonactive.agiletools.agiledeck.gameboard.control.PlayerService;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/players")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Transactional
public class PlayerResource {

    @Inject
    PlayerService playerService;

    @PUT
    @Path("{id}")
    public Response changeName(@PathParam("id") Long id, @QueryParam("name") String name) {
        return Response.ok(playerService.changeName(id, name)).build();
    }
}
