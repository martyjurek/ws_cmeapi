package com.misys.cme.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.misys.cme.utils.CMEAPIUtils;
import java.lang.Integer;
import java.lang.Object;
import java.lang.String;
import java.util.HashMap;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
@Path("/apps")
public class App {
	private static Logger logger = LogManager.getLogger(App.class.getName());

	@GET
	@Produces("application/json")
	public static Response searchApp(@Context UriInfo uriInfo) {
		logger.traceEntry();
		Response.ResponseBuilder rb = Response.status(Response.Status.OK);
		try {
			Object object = CMEAPIUtils.search("App", uriInfo);
			rb.entity(object);
			rb.header("X-Messages", "This was successful");
		} catch(Exception e) {
			rb.status(Response.Status.INTERNAL_SERVER_ERROR);
			rb.header("X-Errors", "There was an error");
			logger.log(Level.ERROR, e.getMessage());
		}
		return logger.traceExit(rb.build());
	}

	@GET
	@Path("/{app_id}")
	@Produces("application/json")
	public static Response getApp(@Context UriInfo uriInfo, @PathParam("app_id") final Integer app_id) {
		logger.traceEntry();
		Response.ResponseBuilder rb = Response.status(Response.Status.OK);
		try {
			HashMap<String, Object> testMap = new HashMap<String, Object>();
			testMap.put("app_id", app_id);
			Object object = CMEAPIUtils.get("App", uriInfo, testMap);
			rb.entity(object);
			rb.header("X-Messages", "This was successful");
		} catch(Exception e) {
			rb.status(Response.Status.INTERNAL_SERVER_ERROR);
			rb.header("X-Errors", "There was an error");
			logger.log(Level.ERROR, e.getMessage());
		}
		return logger.traceExit(rb.build());
	}

	@PUT
	@Path("/{app_id}")
	@Consumes("application/json")
	@Produces("application/json")
	public static Response updateApp(@PathParam("app_id") final Integer app_id, @Context UriInfo uriInfo, JsonNode cmeObject) {
		logger.traceEntry();
		Response.ResponseBuilder rb = Response.status(Response.Status.OK);
		try {
			Object object = CMEAPIUtils.update("App", uriInfo, cmeObject);
			rb.entity(object);
			rb.header("X-Messages", "This was successful");
		} catch(Exception e) {
			rb.status(Response.Status.INTERNAL_SERVER_ERROR);
			rb.header("X-Errors", "There was an error");
			logger.log(Level.ERROR, e.getMessage());
		}
		return logger.traceExit(rb.build());
	}

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public static Response insertApp(@Context UriInfo uriInfo, JsonNode cmeObject) {
		logger.traceEntry();
		Response.ResponseBuilder rb = Response.status(Response.Status.OK);
		try {
			Object object = CMEAPIUtils.insert("App", uriInfo, cmeObject);
			rb.entity(object);
			rb.header("X-Messages", "This was successful");
		} catch(Exception e) {
			rb.status(Response.Status.INTERNAL_SERVER_ERROR);
			rb.header("X-Errors", "There was an error");
			logger.log(Level.ERROR, e.getMessage());
		}
		return logger.traceExit(rb.build());
	}

	@DELETE
	@Path("/{app_id}")
	@Produces("application/json")
	public static Response deleteApp(@PathParam("app_id") final Integer app_id) {
		logger.traceEntry();
		Response.ResponseBuilder rb = Response.status(Response.Status.OK);
		try {
			HashMap<String, Object> testMap = new HashMap<String, Object>();
			testMap.put("app_id", app_id);
			Object object = CMEAPIUtils.delete("App", testMap);
			rb.entity(object);
			rb.header("X-Messages", "This was successful");
		} catch(Exception e) {
			rb.status(Response.Status.INTERNAL_SERVER_ERROR);
			rb.header("X-Errors", "There was an error");
			logger.log(Level.ERROR, e.getMessage());
		}
		return logger.traceExit(rb.build());
	}
}
