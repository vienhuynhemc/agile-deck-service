package com.axonactive.agiletools.agiledeck;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class AgileDeckException extends WebApplicationException {

    public AgileDeckException(MsgCodes msgCode) {
       this(Status.BAD_REQUEST, msgCode);
    }

    public AgileDeckException(Status status, MsgCodes msgCode) {
        super(Response.status(status).header("MSG_CODE", msgCode.getValue()).build());
    }
}
