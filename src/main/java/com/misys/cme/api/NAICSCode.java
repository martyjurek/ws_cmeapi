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
@Path("/admin/naics-codes")
public class NAICSCode {
	private static Logger logger = LogManager.getLogger(NAICSCode.class.getName());

	@GET
	@Produces("application/json")
	public static Response searchNAICSCode(@Context UriInfo uriInfo) {
		logger.traceEntry();
		Response.ResponseBuilder rb = Response.status(Response.Status.OK);
		try {
			Object object = CMEAPIUtils.search("NAICSCode", uriInfo);
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
	@Path("/{naics_code}/{naics_div_id}/{naics_group_id}")
	@Produces("application/json")
	public static Response getNAICSCode(@Context UriInfo uriInfo, @PathParam("naics_code") final String naics_code, @PathParam("naics_div_id") final Integer naics_div_id, @PathParam("naics_group_id") final Integer naics_group_id) {
		logger.traceEntry();
		Response.ResponseBuilder rb = Response.status(Response.Status.OK);
		try {
			HashMap<String, Object> testMap = new HashMap<String, Object>();
			testMap.put("naics_code", naics_code);
			testMap.put("naics_group_id", naics_group_id);
			testMap.put("naics_div_id", naics_div_id);
			Object object = CMEAPIUtils.get("NAICSCode", uriInfo, testMap);
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
	@Path("/{naics_code}/{naics_div_id}/{naics_group_id}")
	@Consumes("application/json")
	@Produces("application/json")
	public static Response updateNAICSCode(@PathParam("naics_code") final String naics_code, @PathParam("naics_div_id") final Integer naics_div_id, @PathParam("naics_group_id") final Integer naics_group_id, @Context UriInfo uriInfo, JsonNode cmeObject) {
		logger.traceEntry();
		Response.ResponseBuilder rb = Response.status(Response.Status.OK);
		try {
			Object object = CMEAPIUtils.update("NAICSCode", uriInfo, cmeObject);
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
	public static Response insertNAICSCode(@Context UriInfo uriInfo, JsonNode cmeObject) {
		logger.traceEntry();
		Response.ResponseBuilder rb = Response.status(Response.Status.OK);
		try {
			Object object = CMEAPIUtils.insert("NAICSCode", uriInfo, cmeObject);
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
	@Path("/{naics_code}/{naics_div_id}/{naics_group_id}")
	@Produces("application/json")
	public static Response deleteNAICSCode(@PathParam("naics_code") final String naics_code, @PathParam("naics_div_id") final Integer naics_div_id, @PathParam("naics_group_id") final Integer naics_group_id) {
		logger.traceEntry();
		Response.ResponseBuilder rb = Response.status(Response.Status.OK);
		try {
			HashMap<String, Object> testMap = new HashMap<String, Object>();
			testMap.put("naics_code", naics_code);
			testMap.put("naics_group_id", naics_group_id);
			testMap.put("naics_div_id", naics_div_id);
			Object object = CMEAPIUtils.delete("NAICSCode", testMap);
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
