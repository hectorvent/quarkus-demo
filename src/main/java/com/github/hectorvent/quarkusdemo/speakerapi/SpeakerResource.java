package com.github.hectorvent.quarkusdemo.speakerapi;

import java.util.List;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.jboss.logging.Logger;

/**
 *
 * @author Hector Ventura <hectorvent@gmail.com>
 */
@Path("/speaker")
@Produces("application/json")
@Consumes("application/json")
public class SpeakerResource {

    private static final Logger LOGGER = Logger.getLogger(SpeakerResource.class);

    @GET
    @Counted(name = "getSpeakers.Count", description = "How many request has get Speaker.")
    @Timed(name = "getSpeakers.Time", description = "A measure of how long it takes to perform Speaker full list", unit = MetricUnits.MILLISECONDS)
    public List<Speaker> getSpeakers() {
        return Speaker.findAll().list();
    }

    @GET
    @Path("/{id}")
    public Speaker getSpeaker(@PathParam("id") Long id) {

        Speaker entity = Speaker.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Speaker with id of " + id + " does not exist.", 404);
        }

        return entity;
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Speaker update(@PathParam("id") Long id, Speaker speaker) {
        if (speaker.name == null) {
            throw new WebApplicationException("Speaker Name was not set on request.", 422);
        }

        Speaker entity = Speaker.findById(id);

        if (entity == null) {
            throw new WebApplicationException("Speaker with id of " + id + " does not exist.", 404);
        }

        entity.name = speaker.name;
        entity.persist();

        return entity;
    }

    @POST
    @Transactional
    public Response create(Speaker speaker) {
        if (speaker.id != null) {
            throw new WebApplicationException("Id can't specify", 422);
        }

        speaker.persist();
        return Response.ok(speaker).status(201).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        Speaker entity = Speaker.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Speaker with id of " + id + " does not exist.", 404);
        }
        entity.delete();
        return Response.status(204).build();
    }

}
