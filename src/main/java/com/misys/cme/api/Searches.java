package com.misys.cme.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.misys.cme.utils.CMEAPIUtils;
import java.lang.String;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Singleton
@Path("/searches/{collection}")
public class Searches {
	private static Logger logger = LogManager.getLogger(Searches.class.getName());

	@GET
	@Produces("application/json")
	public static Response searchViaGET(@Context UriInfo uriInfo, @PathParam("collection") String collection) {
		logger.traceEntry();
		Response.ResponseBuilder rb = Response.status(Response.Status.OK);
		try {
		} catch(Exception e) {
			rb.status(Response.Status.INTERNAL_SERVER_ERROR);
			logger.log(Level.ERROR, e.getMessage());
		}
		return logger.traceExit(rb.build());
	}

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public static Response searchViaPOST(@Context UriInfo uriInfo, @PathParam("collection") String collection, JsonNode filterJSON) {
		logger.traceEntry();
		Response.ResponseBuilder rb = Response.status(Response.Status.OK);
		try {
			Object object = CMEAPIUtils.searchViaPOST(uriInfo, collection, filterJSON);
			rb.entity(object);
		} catch(Exception e) {
			rb.status(Response.Status.INTERNAL_SERVER_ERROR);
			logger.log(Level.ERROR, e.getMessage());
		}
		return logger.traceExit(rb.build());
	}
}
