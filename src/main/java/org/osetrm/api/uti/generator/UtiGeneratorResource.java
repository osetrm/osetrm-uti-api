package org.osetrm.api.uti.generator;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/uti-generator")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "uti", description = "Generate Universal Transaction Identifier (uti)")
public class UtiGeneratorResource {

    private final UtiGeneratorService utiGeneratorService;

    @Inject
    public UtiGeneratorResource(UtiGeneratorService utiGeneratorService) {
        this.utiGeneratorService = utiGeneratorService;
    }

    @POST
    @APIResponse(
            responseCode = "201",
            description = "Uti Created",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(type = SchemaType.OBJECT, implementation = Uti.class)
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Invalid Request",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    public Response get(@Valid UtiRequest utiRequest) {
        return Response.ok(new Uti(utiGeneratorService.generate(utiRequest.regulatoryRegime(), utiRequest.lei()))).build();
    }

}
