package com.misys.cme.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.misys.api.utilities.UriInfoUtil;
import com.misys.cme.search.filter.Filter;
import com.misys.cmeobject.CMEObject;
import com.misys.cmeobject.CMEObjectController;
import com.misys.definitions.CMEChildRelKeysDef;
import com.misys.definitions.CMEObjectChildDef;
import com.misys.definitions.CMEObjectColumnDef;
import com.misys.definitions.CMEObjectDef;
import com.misys.enums.CMEObjectType;

/**
 * Contains various static utility functions for interfacing between HTTP request handlers and CMEObjectController
 * functions.
 */
public class CMEAPIUtils {
	private static Logger logger = LogManager.getLogger(CMEAPIUtils.class);
	
	/**
	 * Map of CMEObject names to the classes that handle API requests for the CMEObjects.
	 */
	private static Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
	
	static {
		for (String cmeObjectName : CMEObjectDef.getCMEObjectDefs().keySet()) {
			try {
				CMEAPIUtils.classMap.put(cmeObjectName, Class.forName("com.misys.cme.api." + cmeObjectName));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Performs a search for CMEObjects of the given type (name) with the given UriInfo.
	 * @param name Name of the CMEObject to search for.
	 * @param uriInfo UriInfo used to request the search. This will contain the search parameters.
	 * @return JSON ArrayNode containing the search results.
	 */
	public static ArrayNode search(String name, UriInfo uriInfo) {
		Map<String, Object> queryParams = 
				UriInfoUtil.normalizeMultivaluedMap(uriInfo.getQueryParameters());
		
		ArrayNode array = new ObjectMapper().createArrayNode();
		
		//Get viewname if applicable
		String viewName = (String) queryParams.get("viewname");
		
		//Get visible columns
		List<String> columns = CMEAPIUtils.getVisibleColumns(uriInfo);
		queryParams.remove("columns");
		
		String baseUrl = uriInfo.getBaseUri().toASCIIString();
		
		//Loop through the CMEObjects and add
		List<CMEObject> cmeObjects = CMEObjectController.search(name, viewName, columns, queryParams);
		for (CMEObject cmeObject : cmeObjects) {
			ObjectNode node = new ObjectMapper().valueToTree(cmeObject);
			String url = CMEAPIUtils.getURLFromCMEObject(cmeObject, baseUrl);
			node.put("href", url);
			array.add(node);
		}
		return array;		
	}
	
	/**
	 * Performs a GET (i.e. read) for the JSON representation of a CMEObject of the given type using the given UriInfo
	 * and URL path parameters.
	 * @param name Name of the CMEObject type to get.
	 * @param uriInfo UriInfo used to request the GET. This contains configurations for the response.
	 * @param params Parameters for the GET that were passed in the URL path.
	 * @return JSON ObjectNode containing a representation of the CMEObject.
	 */
	public static ObjectNode get(String name, UriInfo uriInfo, Map<String, Object> params) {
		Map<String, Object> queryParams = 
				UriInfoUtil.normalizeMultivaluedMap(uriInfo.getQueryParameters());
		
		//Get visible columns
		List<String> columns = CMEAPIUtils.getVisibleColumns(uriInfo);
		queryParams.remove("columns");
		
		String references = (String) queryParams.get("references");
		
		String baseUrl = uriInfo.getBaseUri().toASCIIString();
		
		/*
		CMEObject cmeObject = CMEObjectController.get(name, columns, params, false);
		ObjectNode node = new ObjectMapper().valueToTree(cmeObject);
		
		CMEAPIUtils.addChildren(references, uriInfo, node, cmeObject);
		String url = CMEAPIUtils.getURLFromCMEObject(cmeObject, uriInfo);
		node.put("href", url);
		*/
		return CMEAPIUtils.get(name, params, columns, references, baseUrl);
	}
	
	/**
	 * Performs a GET (i.e. read) for the JSON representation of a CMEObject of the given type using the given URL path
	 * parameters and the given output configuration.
	 * @note This shouldn't be called directly.
	 * @param name Name of the CMEObject type to get.
	 * @param params Parameters for the GET that were passed in the URL path.
	 * @param columns List of column names to return in the output. If the List is empty or null, all columns are returned.
	 * @param references Configuration for child objects in the output. "link" outputs them as links. "expand" outputs
	 * them inline. "" or null doesn't output them.
	 * @param baseUrl Base URL the client is using to interact with the API.
	 * @return JSON ObjectNode containing a representation of the CMEObject.
	 */
	private static ObjectNode get(String name, Map<String, Object> params, List<String> columns, String references, String baseUrl) {
		List<CMEObject> cmeObjects = CMEObjectController.search(name, null, columns, params);
		if (cmeObjects != null && cmeObjects.size() > 0) {
			CMEObject cmeObject = cmeObjects.get(0);
			ObjectNode node = new ObjectMapper().valueToTree(cmeObject);
			CMEAPIUtils.addChildren(references, baseUrl, node, cmeObject);
			String url = CMEAPIUtils.getURLFromCMEObject(cmeObject, baseUrl);
			node.put("href", url);
			return node;
		}
		return null;
	}
	
	/**
	 * Updates a CMEObject of the given type using the given UriInfo and JSON representation.
	 * @param name Name of the CMEObject type to update.
	 * @param uriInfo UriInfo used to request the update.
	 * @param json JSON representation of the updated state of the CMEObject?
	 * @return The CMEObject after the update.
	 * TODO: Make this return an ObjectNode.
	 */
	public static CMEObject update(String name, UriInfo uriInfo, JsonNode json) {
		try {
			Map<String, Object> map = new ObjectMapper().convertValue(json.get("values"), new TypeReference<Map<String,Object>>(){});
			CMEObject cmeObject = new CMEObject(json.get("type").asText());
			cmeObject.setValues(map);
			CMEObjectController.update(cmeObject);
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}
	
	/**
	 * Inserts a CMEObject of the given type using the given UriInfo and JSON data.
	 * @param name Name of the CMEObject type to insert an instance of.
	 * @param uriInfo UriInfo used to request the insert.
	 * @param json JSON data for creating the CMEObject.
	 * @return JSON ObjectNode containing a representation of the created CMEObject.
	 */
	public static ObjectNode insert(String name, UriInfo uriInfo, JsonNode json) {
		try {
			Map<String, Object> map = new ObjectMapper().convertValue(json.get("values"), new TypeReference<Map<String,Object>>(){});
			CMEObject cmeObject = new CMEObject(json.get("type").asText());
			cmeObject.setValues(map);
			
			cmeObject = CMEObjectController.insert(cmeObject);
			
			Map<String, Object> keyParams = new HashMap<String, Object>();
			CMEObjectDef def = CMEObjectDef.getCmeObjDef(name);
			for (CMEObjectColumnDef column : def.getKeyColumns()) {
				keyParams.put(column.getColumnName(), cmeObject.getValue(column.getColumnName()));
			}
			ObjectNode node = new ObjectMapper().valueToTree(cmeObject);
			String baseUrl = uriInfo.getBaseUri().toASCIIString();
			String url = CMEAPIUtils.getURLFromCMEObject(cmeObject, baseUrl);
			node.put("href", url);
			return node;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Deletes a CMEObject of the given type using the given URL path parameters.
	 * @param name Name of the CMEObject type to delete.
	 * @param params Parameters for the delete that were passed in the URL path.
	 * @return Nothing?
	 * TODO: Change return type to void or return something meaningful.
	 */
	public static Object delete(String name, Map<String, Object> params) {
		CMEObjectController.delete(name, params);
		return null;
	}
	
	/**
	 * Adds child references to the given ObjectNode for the given references format, base URL, and CMEObject.
	 * @param references Value of the references URL query parameter. This should either be "link", "expand", "", or null.
	 * @param baseUrl Base URL the client is using to interact with the API.
	 * @param node[in,out] JSON Object to add child references to.
	 * @param cmeObject CMEObject that we are generating the JSON for.
	 * TODO: Refactor this into 3 functions: addChildren, addChildrenAsLinks, and addChildrenAsExpanded.
	 */
	private static void addChildren(String references, String baseUrl, ObjectNode node, CMEObject cmeObject) {
		if (isValidReferencesOption(references)) {
			CMEObjectDef def = CMEObjectDef.getCmeObjDef(cmeObject.getType());
			for (CMEObjectChildDef child : def.getChildDefs()) {
				CMEObjectType childType = child.getType();
				String childName = child.getChildName();
				String refColName = child.getRefColName();
				Map<String, Object> childParams = CMEAPIUtils.getChildParams(child, cmeObject);
				if (childParams.size() > 0) {
					ObjectNode values = (ObjectNode) node.get("values");
					if (references.equals("link")) {
						String url = CMEAPIUtils.getURLFromTypeParams(childName, baseUrl, childParams);
						logger.trace(refColName + ": " + url);
						values.put(refColName, url);
					} else if (references.equals("expand")) {
						if (childType.equals(CMEObjectType.SINGLE)) {
							logger.trace(childName + ", " + childParams);
							ObjectNode childNode = CMEAPIUtils.get(childName, childParams, null, null, baseUrl);
							if (childNode != null) {
								Map<String, Object> map = new ObjectMapper().convertValue(childNode.get("values"), new TypeReference<Map<String,Object>>(){});
								CMEObject childObject = new CMEObject(childNode.get("type").asText());
								childObject.setValues(map);
								CMEAPIUtils.addChildren(references, baseUrl, childNode, childObject);
								if (child.isHideParent()) {
									node.removeAll();
									node.setAll(childNode);
								} else {
									values.set(refColName, childNode);
								}
							}
						} else if (childType.equals(CMEObjectType.GROUP)) {
							ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
							
							List<CMEObject> childObjects = CMEObjectController.search(childName, null, null, childParams);
							for (CMEObject childObject : childObjects) {
								ObjectNode childObjectNode = new ObjectMapper().valueToTree(childObject);
								String url = CMEAPIUtils.getURLFromCMEObject(childObject, baseUrl);
								childObjectNode.put("href", url);
								CMEAPIUtils.addChildren(references, baseUrl, childObjectNode, childObject);
								arrayNode.add(childObjectNode);
							}
							
							values.set(refColName, arrayNode);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Checks whether the given String is a valid option for the "references" query parameter.
	 * @param references String to check.
	 * @return true if the String has a valid value; false otherwise.
	 */
	private static boolean isValidReferencesOption(String references) {
		if (
			references != null &&
			references.length() > 0 && (
				references.equals("link") ||
				references.equals("expand")
			)
		) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Gets the key-value pairs for the parameters for looking up the given CMEObject child of the given CMEObject.
	 * @param child Definition of the CMEObject child to get the parameter key-values for.
	 * @param cmeObject CMEObject to get the values for the keys from.
	 * @return Map parameters and their values for looking up the CMEObject child.
	 */
	private static Map<String, Object> getChildParams(CMEObjectChildDef child, CMEObject cmeObject) {
		Map<String, Object> childParams = new HashMap<String, Object>();
		for (CMEChildRelKeysDef key : child.getKeys()) {
			String childColumnName = key.getColumnName();
			String parentColumnName = key.getParentColumnName();
			Object value = cmeObject.getValue(parentColumnName);
			if (value == null) {
				childParams.clear();
				break;
			}
			childParams.put(childColumnName, value);
		}
		return childParams;
	}
	
	/**
	 * Gets the URL for getting (i.e. reading) the given CMEObject with the given base URL.
	 * @param cmeObject CMEObject to get the URL for.
	 * @param baseUrl Base URL the client is using to interact with the API.
	 * @return The URL as a String.
	 */
	private static String getURLFromCMEObject(CMEObject cmeObject, String baseUrl) {
		String name = cmeObject.getType();
		CMEObjectDef def = CMEObjectDef.getCmeObjDef(name);
		
		UriBuilder uriBuilder = UriBuilder.fromResource(CMEAPIUtils.classMap.get(name));
		addMethodPathToUriBuilder(uriBuilder, name, "get" + name);
		
		for (CMEObjectColumnDef column : def.getKeyColumns()) {
			String columnName = column.getColumnName();
			uriBuilder.resolveTemplate(columnName, cmeObject.getValue(columnName));
		}
		String url = uriBuilder.build().toASCIIString();
		if (url.charAt(0) == '/') {
			url = url.substring(1, url.length());
		}
		return baseUrl + url;
	}
	
	/**
	 * Creates a URL from the given base URL, CMEObject name, and parsed query params.
	 * @param name Name of the CMEObject to use the base path from.
	 * @param baseURL Base URL the client is using to interact with the API.
	 * @param params Parsed parameters from the query params?
	 * @return The URL as a String.
	 */
	private static String getURLFromTypeParams(String name, String baseURL, Map<String, Object> params) {
		UriBuilder uriBuilder =  UriBuilder.fromResource(CMEAPIUtils.classMap.get(name));
		CMEObjectDef objectDefinition = CMEObjectDef.getCmeObjDef(name);
		if (objectDefinition.containsAllKeyColumns(params.keySet())) {
			addMethodPathToUriBuilder(uriBuilder, name, "get" + name);
			uriBuilder.resolveTemplates(params);
		} else {
			addMethodPathToUriBuilder(uriBuilder, name, "search" + name);
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				uriBuilder.queryParam(entry.getKey(), entry.getValue());
			}
		}
		
		String url = uriBuilder.build().toASCIIString();

		if (url.charAt(0) == '/') {
			url = url.substring(1, url.length());
		}
		
		return baseURL + url;
	}
	
	/**
	 * Adds the path, if any, from the method for the given CMEObject type with the given name to the given UriBuilder.
	 * @param uriBuilder UriBuilder to add the path to.
	 * @param cmeObjectType CMEObject type the method is for.
	 * @param methodName The name of the method to get the path from.
	 */
	private static void addMethodPathToUriBuilder(UriBuilder uriBuilder, String cmeObjectType, String methodName) {
		Method method = getFirstMethodWithName(CMEAPIUtils.classMap.get(cmeObjectType), methodName);
		if (method.isAnnotationPresent(javax.ws.rs.Path.class)) {
			uriBuilder.path(CMEAPIUtils.classMap.get(cmeObjectType), methodName);
		}
	}
	
	/**
	 * Gets the first Method with the given name from the given Class.
	 * @param clazz Class to get the Method from.
	 * @param methodName Name of the Method to get.
	 * @return The first Method found with the given name.
	 */
	private static Method getFirstMethodWithName(Class<?> clazz, String methodName) {
		List<Method> matchingMethods = Arrays.asList(clazz.getDeclaredMethods())
			.stream()
			.filter(method -> method.getName().equals(methodName))
			.collect(Collectors.toList());
		if (matchingMethods.size() != 0) {
			return matchingMethods.get(0);
		} else {
			throw new RuntimeException("Class \"" + clazz.toString() + "\" has no method \"" + methodName + "\"!");
		}
	}
	
	/**
	 * Gets the List of column names that the client requested be visible from the given UriInfo.
	 * @param uriInfo UriInfo to get the column names from.
	 * @return The List of column names.
	 */
	private static List<String> getVisibleColumns(UriInfo uriInfo) {
		Map<String, Object> queryParams = 
				UriInfoUtil.normalizeMultivaluedMap(uriInfo.getQueryParameters());
		List<String> columns = new ArrayList<String>();
		if (queryParams.get("columns") != null) {
			for (String column : queryParams.get("columns").toString().split(",")) {
				columns.add(column);
			}
		}
		return columns;
	}
	
	private static List<String> getVisibleColumns(JsonNode filterJSON) {
		List<String> columns = new ArrayList<>();
		if (filterJSON.has("columns")) {
			JsonNode columnsNode = filterJSON.get("columns");
			if (columnsNode.isArray()) {
				columnsNode.forEach((node) -> {
					columns.add(node.asText());
				});
			}
		}
		return columns;
	}
	
	/**
	 * Searches the given collection with the given filter (as JSON).
	 * @param collection Collection name to search.
	 * @param filterJSON Filter to use for the search, as JSON.
	 * @return The results of the search as a JSON object.
	 */
	public static ObjectNode searchViaPOST( UriInfo uriInfo, String collection, JsonNode filterJSON) {
		String name = CMEObjectController.getObjectNameForPath(collection);
		if (name == null) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode wrapper = mapper.createObjectNode();
		String viewName = null;
		final String references;
		if (filterJSON.has("references")) {
			references = filterJSON.get("references").asText();
		} else {
			references = "";
		}
		wrapper.put("references", references);
		String baseUrl = uriInfo.getBaseUri().toASCIIString();
		if (filterJSON.isObject()) {
			JsonNode filterNode = filterJSON.get("filter");
			wrapper.set("filter", filterNode);
			if (filterNode != null && filterNode.isObject()) {
				Filter filter = new Filter((ObjectNode)filterNode);
				List<String> columns = getVisibleColumns(filterJSON);
				if (filterJSON.has("viewName")) {
					viewName = filterJSON.get("viewName").asText();
					wrapper.put("viewName", viewName);
				}

				List<CMEObject> objects = CMEObjectController.search(name, viewName, columns, filter);
				List<ObjectNode> jsonObjects = objects.stream()
						.map(cmeObject -> {
							ObjectNode objNode = mapper.valueToTree(cmeObject);
							CMEAPIUtils.addChildren(references, baseUrl, objNode, cmeObject);
							String url = CMEAPIUtils.getURLFromCMEObject(cmeObject, baseUrl);
							objNode.put("href", url);
							return objNode;
						})
						.collect(Collectors.toList());
				wrapper.putPOJO("results", jsonObjects);
			} else {
				throw new RuntimeException("Search filter JSON must have a field named \"filter\" with an object for its value!");
			}
		} else {
			throw new RuntimeException("Request body must be a JSON Object!");
		}
		return wrapper;
	}
}
