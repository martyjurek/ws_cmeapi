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
@Path("/obligor-numbers")
public class ObligorNumber {
	private static Logger logger = LogManager.getLogger(ObligorNumber.class.getName());

	@GET
	@Produces("application/json")
	public static Response searchObligorNumber(@Context UriInfo uriInfo) {
		logger.traceEntry();
		Response.ResponseBuilder rb = Response.status(Response.Status.OK);
		try {
			Object object = CMEAPIUtils.search("ObligorNumber", uriInfo);
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
	@Path("/{ent_bsys_id}/{ent_id}/{lgl_ent_id}")
	@Produces("application/json")
	public static Response getObligorNumber(@Context UriInfo uriInfo, @PathParam("ent_bsys_id") final Integer ent_bsys_id, @PathParam("ent_id") final Integer ent_id, @PathParam("lgl_ent_id") final String lgl_ent_id) {
		logger.traceEntry();
		Response.ResponseBuilder rb = Response.status(Response.Status.OK);
		try {
			HashMap<String, Object> testMap = new HashMap<String, Object>();
			testMap.put("ent_bsys_id", ent_bsys_id);
			testMap.put("ent_id", ent_id);
			testMap.put("lgl_ent_id", lgl_ent_id);
			Object object = CMEAPIUtils.get("ObligorNumber", uriInfo, testMap);
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
	@Path("/{ent_bsys_id}/{ent_id}/{lgl_ent_id}")
	@Consumes("application/json")
	@Produces("application/json")
	public static Response updateObligorNumber(@PathParam("ent_bsys_id") final Integer ent_bsys_id, @PathParam("ent_id") final Integer ent_id, @PathParam("lgl_ent_id") final String lgl_ent_id, @Context UriInfo uriInfo, JsonNode cmeObject) {
		logger.traceEntry();
		Response.ResponseBuilder rb = Response.status(Response.Status.OK);
		try {
			Object object = CMEAPIUtils.update("ObligorNumber", uriInfo, cmeObject);
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
	public static Response insertObligorNumber(@Context UriInfo uriInfo, JsonNode cmeObject) {
		logger.traceEntry();
		Response.ResponseBuilder rb = Response.status(Response.Status.OK);
		try {
			Object object = CMEAPIUtils.insert("ObligorNumber", uriInfo, cmeObject);
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
	@Path("/{ent_bsys_id}/{ent_id}/{lgl_ent_id}")
	@Produces("application/json")
	public static Response deleteObligorNumber(@PathParam("ent_bsys_id") final Integer ent_bsys_id, @PathParam("ent_id") final Integer ent_id, @PathParam("lgl_ent_id") final String lgl_ent_id) {
		logger.traceEntry();
		Response.ResponseBuilder rb = Response.status(Response.Status.OK);
		try {
			HashMap<String, Object> testMap = new HashMap<String, Object>();
			testMap.put("ent_bsys_id", ent_bsys_id);
			testMap.put("ent_id", ent_id);
			testMap.put("lgl_ent_id", lgl_ent_id);
			Object object = CMEAPIUtils.delete("ObligorNumber", testMap);
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
