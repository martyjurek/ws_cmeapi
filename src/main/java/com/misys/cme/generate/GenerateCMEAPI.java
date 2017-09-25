package com.misys.cme.generate;

import javax.naming.Context;
import javax.naming.InitialContext;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.misys.cme.utils.CMEAPIGenerator;
import com.misys.definitions.DefinitionLoader;
import com.misys.jdbc.DataSourceManager;

public class GenerateCMEAPI {

	public static void main(String[] args) {
		try {
			System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
	        System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
			
			InitialContext ic = new InitialContext();
	        ic.createSubcontext("java:");
	        ic.createSubcontext("java:/comp");
	        ic.createSubcontext("java:/comp/env");
	        ic.createSubcontext("java:/comp/env/jdbc");
	        
	        SQLServerDataSource dataSource = new SQLServerDataSource();
	        
	        //dataSource.setServerName("TEXLCS6D2WN72");
	        //dataSource.setDatabaseName("MonDev_3");
	        dataSource.setServerName("TEXLDEFHK4HC2");
	        dataSource.setInstanceName("MSSQLSERVER2012");
	        dataSource.setDatabaseName("MonDev");
	        dataSource.setUser("mon_auth");
	        dataSource.setPassword("mon_auth");
	        
	        ic.bind("CF_SYS_DS", dataSource);
			
			DataSourceManager.setDataSource(dataSource);
			DefinitionLoader.initialzeDefinitions();
			CMEAPIGenerator.generateAPIs();
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
			
	}

}

