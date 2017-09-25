package com.misys.cme.utils;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.fasterxml.jackson.databind.JsonNode;
import com.misys.api.generator.ApiClassGenerator;
import com.misys.api.generator.ApiClassInfo;
import com.misys.api.generator.ApiMethodInfo;
import com.misys.api.generator.ApiMethodParameterInfo;
import com.misys.api.generator.ApiMethodType;
import com.misys.api.generator.ControllerMethodInfo;
import com.misys.definitions.CMEObjectColumnDef;
import com.misys.definitions.CMEObjectDef;
import com.misys.enums.ObjectAction;

/**
 * Contains functions for generating CME API classes.
 */
public class CMEAPIGenerator {
	/**
	 * Generates all CME API classes.
	 * @warning This will overwrite files in src\main\java\com\misys\cme\api!
	 */
	public static void generateAPIs() {
		Map<String, CMEObjectDef> defs = CMEObjectDef.getCMEObjectDefs();
		for (Map.Entry<String, CMEObjectDef> entry : defs.entrySet()) {
			String name = entry.getKey();
			CMEObjectDef def = entry.getValue();
			/*
			if (false
				//|| def.getName().equals("EntityType") 
				//|| def.getName().equals("NonIndividual")
				//|| def.getName().equals("EntityAddress")
			) continue;
			*/
			ApiClassInfo classInfo = new ApiClassInfo();
			classInfo.setPackageName("com.misys.cme.api");
			classInfo.setClassName(name);
			classInfo.setUrlPath("/" + def.getPath());
			
			if (def.getViews(ObjectAction.SEARCH).size() > 0) {
				ApiMethodInfo searchMethod = CMEAPIGenerator.generateSearch(name, def);
				classInfo.addApiMethod(searchMethod);
			}
			if (def.getViews(ObjectAction.LOAD).size() > 0) {
				ApiMethodInfo getMethod = CMEAPIGenerator.generateGet(name, def);
				classInfo.addApiMethod(getMethod);
			}
			///*
			if (def.getViews(ObjectAction.UPDATE).size() > 0) {
				ApiMethodInfo updateMethod = CMEAPIGenerator.generateUpdate(name, def);
				classInfo.addApiMethod(updateMethod);
			}
			if (def.getViews(ObjectAction.CREATE).size() > 0) {
				ApiMethodInfo insertMethod = CMEAPIGenerator.generateInsert(name, def);
				classInfo.addApiMethod(insertMethod);
			}
			if (def.getViews(ObjectAction.DELETE).size() > 0) {
				ApiMethodInfo deleteMethod = CMEAPIGenerator.generateDelete(name, def);
				classInfo.addApiMethod(deleteMethod);
			}
			//*/
			ApiClassGenerator.generateClass(classInfo);
			
		}
		
		generateSearchesAPI();
	}
	
	/**
	 * Creates the ApiMethodInfo for a search endpoint given the CMEObject name.
	 * @param name Name of the CMEObject that the API searches for.
	 * @param def This is not used.
	 * @return A new ApiMethodInfo.
	 * TODO: Remove name from parameters and get name from def.
	 */
	private static ApiMethodInfo generateSearch(String name, CMEObjectDef def) {
		ApiMethodInfo searchMethod = new ApiMethodInfo();
		searchMethod.setMethodName("search" + name);
		searchMethod.setUrlPath("/");
		searchMethod.setProducesType(MediaType.APPLICATION_JSON);
		searchMethod.setApiMethodType(ApiMethodType.GET);
		searchMethod.setSuccessMessage("This was successful");
		searchMethod.setErrorMessage("There was an error");
		
		ApiMethodParameterInfo uriInfo = ApiMethodParameterInfo.getUriInfoContextParameter();
		searchMethod.addApiMethodParameter(uriInfo);
		
		ControllerMethodInfo controllerMethod = new ControllerMethodInfo();
		controllerMethod.setClazz(CMEAPIUtils.class);
		controllerMethod.setMethodName("search");
		
		ApiMethodParameterInfo param1 = new ApiMethodParameterInfo();
		param1.setType(String.class);
		param1.setName(name);
		param1.setLiteralValue(name);
		controllerMethod.addParameter(param1);
		
		controllerMethod.addParameter(uriInfo);
		
		searchMethod.setControllerMethod(controllerMethod);
		
		return searchMethod;
	}
	
	/**
	 * Creates the ApiMethodInfo for a GET endpoint (i.e. read endpoint) given the CMEObject name and definition.
	 * @param name Name of the CMEObject that the API gets.
	 * @param def Definition of the CMEObject that the API gets.
	 * @return A new ApiMethodInfo.
	 * TODO: Remove name from parameters and get name from def.
	 */
	private static ApiMethodInfo generateGet(String name, CMEObjectDef def) {
		ApiMethodInfo getMethod = new ApiMethodInfo();
		getMethod.setMethodName("get" + name);
		getMethod.setUrlPath("/");
		getMethod.setProducesType(MediaType.APPLICATION_JSON);
		getMethod.setApiMethodType(ApiMethodType.GET);
		getMethod.setSuccessMessage("This was successful");
		getMethod.setErrorMessage("There was an error");
		
		ApiMethodParameterInfo uriInfo = ApiMethodParameterInfo.getUriInfoContextParameter();
		getMethod.addApiMethodParameter(uriInfo);
		
		Map<String, String> testMap = new HashMap<String, String>();
		
		for (CMEObjectColumnDef key : def.getKeyColumns()) {
			ApiMethodParameterInfo param = new ApiMethodParameterInfo();
			switch (key.getDataType()) {
				case INTEGER: 
					param.setType(Integer.class);
					break;
				case DECIMAL:
					param.setType(Double.class);
					break;
				case STRING:
					default:
					param.setType(String.class);
					break;
			}
			String path = getMethod.getUrlPath();
			if (path.charAt(path.length()-1) != '/') {
				path += "/";
			}
			getMethod.setUrlPath(path + "{" + key.getColumnName() + "}");
			
			param.setAnnotationClass(PathParam.class);
			param.setAnnotationValue(key.getColumnName());
			param.setName(key.getColumnName());
			param.setFinal(true);
			testMap.put(key.getColumnName(), key.getColumnName());
			getMethod.addApiMethodParameter(param);
		}
		
		ControllerMethodInfo controllerMethod = new ControllerMethodInfo();
		controllerMethod.setClazz(CMEAPIUtils.class);
		controllerMethod.setMethodName("get");
		
		ApiMethodParameterInfo param1 = new ApiMethodParameterInfo();
		param1.setType(String.class);
		param1.setName(name);
		param1.setLiteralValue(name);
		controllerMethod.addParameter(param1);

		controllerMethod.addParameter(uriInfo);
		
		ApiMethodParameterInfo paramMap = new ApiMethodParameterInfo();
		paramMap.setName("testMap");
		paramMap.setType(Map.class);
		paramMap.setMapValues(testMap);
		controllerMethod.addParameter(paramMap);

		getMethod.setControllerMethod(controllerMethod);
		
		return getMethod;
	}
	
	/**
	 * Creates the ApiMethodInfo for an update endpoint given the CMEObject name and definition.
	 * @param name Name of the CMEObject that the API updates.
	 * @param def Definition of the CMEObject that the API updates.
	 * @return A new ApiMethodInfo.
	 * TODO: Remove name from parameters and get name from def.
	 */
	private static ApiMethodInfo generateUpdate(String name, CMEObjectDef def) {
		ApiMethodInfo updateMethod = new ApiMethodInfo();
		updateMethod.setMethodName("update" + name);
		updateMethod.setUrlPath("/");
		updateMethod.setConsumesType(MediaType.APPLICATION_JSON);
		updateMethod.setProducesType(MediaType.APPLICATION_JSON);
		updateMethod.setApiMethodType(ApiMethodType.PUT);
		updateMethod.setSuccessMessage("This was successful");
		updateMethod.setErrorMessage("There was an error");
		
		for (CMEObjectColumnDef key : def.getKeyColumns()) {
			ApiMethodParameterInfo param = new ApiMethodParameterInfo();
			switch (key.getDataType()) {
				case INTEGER: 
					param.setType(Integer.class);
					break;
				case DECIMAL:
					param.setType(Double.class);
					break;
				case STRING:
					default:
					param.setType(String.class);
					break;
			}
			String path = updateMethod.getUrlPath();
			if (path.charAt(path.length()-1) != '/') {
				path += "/";
			}
			updateMethod.setUrlPath(path + "{" + key.getColumnName() + "}");
			
			param.setAnnotationClass(PathParam.class);
			param.setAnnotationValue(key.getColumnName());
			param.setName(key.getColumnName());
			param.setFinal(true);
			updateMethod.addApiMethodParameter(param);
		}
		
		ControllerMethodInfo controllerMethod = new ControllerMethodInfo();
		controllerMethod.setClazz(CMEAPIUtils.class);
		controllerMethod.setMethodName("update");
		
		ApiMethodParameterInfo param1 = new ApiMethodParameterInfo();
		param1.setType(String.class);
		param1.setName(name);
		param1.setLiteralValue(name);
		controllerMethod.addParameter(param1);
		
		ApiMethodParameterInfo uriInfo = ApiMethodParameterInfo.getUriInfoContextParameter();
		updateMethod.addApiMethodParameter(uriInfo);
		controllerMethod.addParameter(uriInfo);
		
		ApiMethodParameterInfo param = new ApiMethodParameterInfo();
		//param.setType(CMEObject.class);
		param.setType(JsonNode.class);
		param.setName("cmeObject");
		updateMethod.addApiMethodParameter(param);
		controllerMethod.addParameter(param);
		
		updateMethod.setControllerMethod(controllerMethod);
		
		return updateMethod;
	}
	
	/**
	 * Creates the ApiMethodInfo for an insert endpoint (i.e. create endpoint) given the CMEObject name and definition.
	 * @param name Name of the CMEObject that the API inserts.
	 * @param def Definition of the CMEObject that the API inserts.
	 * @return A new ApiMethodInfo.
	 * TODO: Remove name from parameters and get name from def.
	 */
	private static ApiMethodInfo generateInsert(String name, CMEObjectDef def) {
		ApiMethodInfo insertMethod = new ApiMethodInfo();
		insertMethod.setMethodName("insert" + name);
		insertMethod.setUrlPath("/");
		insertMethod.setConsumesType(MediaType.APPLICATION_JSON);
		insertMethod.setProducesType(MediaType.APPLICATION_JSON);
		insertMethod.setApiMethodType(ApiMethodType.POST);
		insertMethod.setSuccessMessage("This was successful");
		insertMethod.setErrorMessage("There was an error");

		ControllerMethodInfo controllerMethod = new ControllerMethodInfo();
		controllerMethod.setClazz(CMEAPIUtils.class);
		controllerMethod.setMethodName("insert");
		
		ApiMethodParameterInfo param1 = new ApiMethodParameterInfo();
		param1.setType(String.class);
		param1.setName(name);
		param1.setLiteralValue(name);
		controllerMethod.addParameter(param1);
		
		ApiMethodParameterInfo uriInfo = ApiMethodParameterInfo.getUriInfoContextParameter();
		insertMethod.addApiMethodParameter(uriInfo);
		controllerMethod.addParameter(uriInfo);
		
		ApiMethodParameterInfo param = new ApiMethodParameterInfo();
		param.setType(JsonNode.class);
		param.setName("cmeObject");
		insertMethod.addApiMethodParameter(param);
		controllerMethod.addParameter(param);
		
		insertMethod.setControllerMethod(controllerMethod);
		
		return insertMethod;
	}
	
	/**
	 * Creates the ApiMethodInfo for a delete endpoint given the CMEObject name and definition.
	 * @param name Name of the CMEObject that the API deletes.
	 * @param def Definition of the CMEObject that the API deletes.
	 * @return A new ApiMethodInfo.
	 * TODO: Remove name from parameters and get name from def.
	 */
	private static ApiMethodInfo generateDelete(String name, CMEObjectDef def) {
		ApiMethodInfo deleteMethod = new ApiMethodInfo();
		deleteMethod.setMethodName("delete" + name);
		deleteMethod.setUrlPath("/");
		deleteMethod.setProducesType(MediaType.APPLICATION_JSON);
		deleteMethod.setApiMethodType(ApiMethodType.DELETE);
		deleteMethod.setSuccessMessage("This was successful");
		deleteMethod.setErrorMessage("There was an error");
		
		Map<String, String> testMap = new HashMap<String, String>();
		
		for (CMEObjectColumnDef key : def.getKeyColumns()) {
			ApiMethodParameterInfo param = new ApiMethodParameterInfo();
			switch (key.getDataType()) {
				case INTEGER: 
					param.setType(Integer.class);
					break;
				case DECIMAL:
					param.setType(Double.class);
					break;
				case STRING:
					default:
					param.setType(String.class);
					break;
			}
			String path = deleteMethod.getUrlPath();
			if (path.charAt(path.length()-1) != '/') {
				path += "/";
			}
			deleteMethod.setUrlPath(path + "{" + key.getColumnName() + "}");
			
			param.setAnnotationClass(PathParam.class);
			param.setAnnotationValue(key.getColumnName());
			param.setName(key.getColumnName());
			param.setFinal(true);
			testMap.put(key.getColumnName(), key.getColumnName());
			deleteMethod.addApiMethodParameter(param);
		}
		
		ControllerMethodInfo controllerMethod = new ControllerMethodInfo();
		controllerMethod.setClazz(CMEAPIUtils.class);
		controllerMethod.setMethodName("delete");
		
		ApiMethodParameterInfo param1 = new ApiMethodParameterInfo();
		param1.setType(String.class);
		param1.setName(name);
		param1.setLiteralValue(name);
		controllerMethod.addParameter(param1);
		
		ApiMethodParameterInfo paramMap = new ApiMethodParameterInfo();
		paramMap.setName("testMap");
		paramMap.setType(Map.class);
		paramMap.setMapValues(testMap);
		controllerMethod.addParameter(paramMap);

		deleteMethod.setControllerMethod(controllerMethod);
		
		return deleteMethod;
	}
	
	/**
	 * Generates the API for the /searches endpoint.
	 * This creates Searches.java.
	 * @note This is a one-off function for a non-standard API. If we get more than one, we need to look at refactoring.
	 */
	private static void generateSearchesAPI() {
		ApiClassInfo classInfo = new ApiClassInfo();
		classInfo.setPackageName("com.misys.cme.api");
		classInfo.setClassName("Searches");
		classInfo.setUrlPath("/searches/{collection}");
		
		ApiMethodInfo getMethod = new ApiMethodInfo();
		getMethod.setMethodName("searchViaGET");
		getMethod.setUrlPath("/");
		getMethod.setApiMethodType(ApiMethodType.GET);
		getMethod.setProducesType(MediaType.APPLICATION_JSON);
		getMethod.addApiMethodParameter(ApiMethodParameterInfo.getUriInfoContextParameter());
		getMethod.addApiMethodParameter(ApiMethodParameterInfo.getPathParamParameter("collection"));
		classInfo.addApiMethod(getMethod);

		ApiMethodParameterInfo controllerMethodUriInfoParameter = new ApiMethodParameterInfo();
		controllerMethodUriInfoParameter.setType(UriInfo.class);
		controllerMethodUriInfoParameter.setName("uriInfo");
		
		ApiMethodParameterInfo controllerMethodCollectionParameter = new ApiMethodParameterInfo();
		controllerMethodCollectionParameter.setType(String.class);
		controllerMethodCollectionParameter.setName("collection");
		
		ApiMethodParameterInfo controllerMethodFilterJSONParameter = new ApiMethodParameterInfo();
		controllerMethodFilterJSONParameter.setType(JsonNode.class);
		controllerMethodFilterJSONParameter.setName("filterJSON");
		
		ControllerMethodInfo controllerMethodInfo = new ControllerMethodInfo();
		controllerMethodInfo.setClazz(CMEAPIUtils.class);
		controllerMethodInfo.setMethodName("searchViaPOST");
		controllerMethodInfo.addParameter(controllerMethodUriInfoParameter);
		controllerMethodInfo.addParameter(controllerMethodCollectionParameter);
		controllerMethodInfo.addParameter(controllerMethodFilterJSONParameter);
		
		ApiMethodParameterInfo filterJSONParameter = new ApiMethodParameterInfo();
		filterJSONParameter.setType(JsonNode.class);
		filterJSONParameter.setName("filterJSON");
		
		ApiMethodInfo postMethod = new ApiMethodInfo();
		postMethod.setMethodName("searchViaPOST");
		postMethod.setUrlPath("/");
		postMethod.setApiMethodType(ApiMethodType.POST);
		postMethod.setConsumesType(MediaType.APPLICATION_JSON);
		postMethod.setProducesType(MediaType.APPLICATION_JSON);
		postMethod.addApiMethodParameter(ApiMethodParameterInfo.getUriInfoContextParameter());
		postMethod.addApiMethodParameter(ApiMethodParameterInfo.getPathParamParameter("collection"));
		postMethod.addApiMethodParameter(filterJSONParameter);
		postMethod.setControllerMethod(controllerMethodInfo);
		classInfo.addApiMethod(postMethod);
		
		ApiClassGenerator.generateClass(classInfo);
	}
}
