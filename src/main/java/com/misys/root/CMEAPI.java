package com.misys.root;

import javax.inject.Singleton;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import com.misys.definitions.DefinitionLoader;
import com.misys.jdbc.DataSourceManager;

/**
 * The CME REST API.
 */
@Singleton
@ApplicationPath(value="")
@Path(value="/")
public class CMEAPI extends Application {
	/**
	 * Constructs a new CME REST API servlet.
	 * @param context The ServletContext to use for the servlet.
	 * @note This calls DefinitionLoader.initializeDefinitions(), which sets up CMEAPIUtils.classMap.
	 * TODO: Remove or use context.
	 */
	public CMEAPI(@Context ServletContext context) {
		
		try {
			InitialContext ic = new InitialContext();
			DataSource dataSource = (DataSource) ic.lookup("java:comp/env/jdbc/DB");
			DataSourceManager.setDataSource(dataSource);
			DefinitionLoader.initialzeDefinitions();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Handles GET requests to the root of the CME REST API.
	 * This does nothing but confirms that the service is running and responding.
	 * @return A String containing the Response body.
	 * TODO: Return a Response instead of a String.
	 */
	@GET
	@Produces(value={"text/plain"})
	public static String test() {
		return "The CME RESTful Service is running";
	}
}
