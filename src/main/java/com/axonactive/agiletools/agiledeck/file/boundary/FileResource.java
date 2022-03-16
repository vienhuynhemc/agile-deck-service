package com.axonactive.agiletools.agiledeck.file.boundary;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.axonactive.agiletools.agiledeck.file.control.FileService;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/files")
@Transactional
public class FileResource {

    @Inject
    FileService fileService;

    @Context
    UriInfo uriInfo;

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response upload(@MultipartForm MultipartFormDataInput input) {
        return this.uploadResponse(input, "");
    }

    @POST
    @Path("/upload/{code}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response uploadCustomFile(@MultipartForm MultipartFormDataInput input, @PathParam("code") String code) {
        return this.uploadResponse(input, code);
    }

    private Response uploadResponse(MultipartFormDataInput input, String code) {
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<String> fileNames = fileService.saveMultiFiles(uploadForm.get("file"), code);

        List<String> pathFileName = fileNames.stream()
                .map(s -> s = uriInfo.getBaseUri().toString() + "files/download/" + s)
                .collect(Collectors.toList());

        return Response.ok()
                .header("files", pathFileName)
                .build();
    }

    @GET
    @Path("/download/{filename}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response download(@PathParam("filename") String fileName) {
        return this.downloadResponse(fileName, "");
    }


    @GET
    @Path("/download/{code}/{filename}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadCustomFile(@PathParam("filename") String fileName, @PathParam("code") String code) {
        return this.downloadResponse(fileName, code);
    }


    private Response downloadResponse(String fileName, String code) {
        File file = fileService.getFile(fileName, code);
        return Response.ok(file)
                .header("Content-Disposition", "attachment;filename=" + fileName)
                .build();
    }

}