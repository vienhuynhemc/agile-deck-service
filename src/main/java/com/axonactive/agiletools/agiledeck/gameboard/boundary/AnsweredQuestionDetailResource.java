package com.axonactive.agiletools.agiledeck.gameboard.boundary;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.axonactive.agiletools.agiledeck.game.entity.AnswerContent;
import com.axonactive.agiletools.agiledeck.gameboard.control.AnsweredQuestionDetailService;
import com.axonactive.agiletools.agiledeck.gameboard.entity.AnsweredQuestionDetail;
import com.axonactive.agiletools.agiledeck.gameboard.entity.Player;

@Path("/answeredquestiondetails")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@Transactional
public class AnsweredQuestionDetailResource {

    @Inject
    AnsweredQuestionDetailService answeredQuestionDetailService;

    @GET
    @Path("{id}")
    public Response getAll(@PathParam("id") Long id) {
        List<AnsweredQuestionDetail> listAnsweredQuestionDetail = answeredQuestionDetailService
                .getAllByAnsweredQuestionId(id);
        return Response.ok(listAnsweredQuestionDetail).build();
    }

    @GET
    @Path("/players/{id}")
    public Response getAllPlayers(@PathParam("id") Long id) {
        List<Player> listPlayers = answeredQuestionDetailService.getAllPlayers(id);
        return Response.ok(listPlayers).build();
    }

    @PUT
    @Path("{answerQuestionDetailId}")
    public Response answerQuestionDetailOfPlayer(@PathParam("answerQuestionDetailId") Long answerQuestionDetailId,
            AnswerContent answerContent) {
        AnsweredQuestionDetail answeredQuestionDetail = answeredQuestionDetailService.update(answerQuestionDetailId,
                answerContent);
        return Response.ok(answeredQuestionDetail).build();
    }

    @PUT
    @Path("reset/{currentQuestId}")
    public Response resetQuest(@PathParam("currentQuestId") Long ansQuestId) {
        answeredQuestionDetailService.resetAnswer(ansQuestId);
        return Response.ok().build();
    }
}
