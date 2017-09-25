package com.misys.cme.utils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.misys.api.utilities.UriInfoUtil;
import com.misys.cmeobject.CMEObject;
import com.misys.cmeobject.CMEObjectController;
import com.misys.definitions.CMEChildRelKeysDef;
import com.misys.definitions.CMEObjectChildDef;
import com.misys.definitions.CMEObjectColumnDef;
import com.misys.definitions.CMEObjectDef;
import com.misys.enums.CMEObjectType;
import com.misys.enums.DataType;
import com.misys.root.CMEAPI;

public class CMEAPIUtils_back {

	private static Map<String, Class> classMap = new HashMap<String, Class>();
	
	static {
		for (String name : CMEObjectDef.getCMEObjectDefs().keySet()) {
			try {
				CMEAPIUtils_back.classMap.put(name, Class.forName("com.misys.cme.api." + name));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static ArrayNode search(String name, UriInfo uriInfo) {
		Map<String, Object> queryParams = 
				UriInfoUtil.normalizeMultivaluedMap(uriInfo.getQueryParameters());
		
		List<String> columns = CMEAPIUtils_back.getVisibleColumns(uriInfo);
		queryParams.remove("columns");
		
		ArrayNode array = new ObjectMapper().createArrayNode();
		
		String viewName = (String) queryParams.get("viewname");
		
		List<CMEObject> cmeObjects = CMEObjectController.search(name, viewName, columns, queryParams);
		for (CMEObject cmeObject : cmeObjects) {
			
			Map<String, Object> keyParams = new HashMap<String, Object>();
			CMEObjectDef def = CMEObjectDef.getCmeObjDef(name);
			for (CMEObjectColumnDef column : def.getKeyColumns()) {
				keyParams.put(column.getColumnName(), cmeObject.getValue(column.getColumnName()));
			}
			ObjectNode node = new ObjectMapper().valueToTree(cmeObject);
			node.put("href", CMEAPIUtils_back.getURL(uriInfo.getBaseUri().toASCIIString(),
					name, ("get" + name), keyParams));
			array.add(node);
		}
		return array;
	}
	
	public static ObjectNode get(String name, UriInfo uriInfo, Map<String, Object> params) {
		//Get query parameters
		Map<String, Object> queryParams = 
				UriInfoUtil.normalizeMultivaluedMap(uriInfo.getQueryParameters());
		
		//Get visible columns
		List<String> columns = CMEAPIUtils_back.getVisibleColumns(uriInfo);
		queryParams.remove("columns");
		
		//Get CMEObject
		CMEObject cmeObject = CMEObjectController.get(name, columns, params, false);
		
		ObjectNode node = new ObjectMapper().valueToTree(cmeObject);
		
		return node;
		
	}
	
	/*
	private String getURLFromCMEObject(CMEObject cmeObject) {
		
		String name = cmeObject.getType();
		
		UriBuilder uriBuilder = UriBuilder.fromResource(CMEAPIUtils.classMap.get(name))
				.path(CMEAPIUtils.classMap.get(name), "get" + name);
		
		CMEAPIUtils.getURL(uriInfo.getBaseUri().toASCIIString(),
				name, ("get" + name), params);
	}
	*/
	
	/*
	public static ObjectNode get(String name, UriInfo uriInfo, Map<String, Object> params) {
		
		Map<String, Object> queryParams = 
				UriInfoUtil.normalizeMultivaluedMap(uriInfo.getQueryParameters());
		
		List<String> columns = CMEAPIUtils.getVisibleColumns(uriInfo);
		//System.out.println("columns: " + columns);
		queryParams.remove("columns");

		CMEObject cmeObject = CMEObjectController.get(name, columns, params, false);
		if (cmeObject != null) {
			UriBuilder uriBuilder = UriBuilder.fromResource(CMEAPIUtils.classMap.get(name))
				.path(CMEAPIUtils.classMap.get(name), "get" + name);
			
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				uriBuilder.resolveTemplate(entry.getKey(), entry.getValue());
			}
	
			if (queryParams.get("references") != null
					&& (queryParams.get("references").equals("link") || queryParams.get("references").equals("expand"))) {
				CMEObjectDef def = CMEObjectDef.getCmeObjDef(name);
				try {
					System.out.println(new ObjectMapper().writeValueAsString(def));
				} catch (Exception e) {}
				for (CMEObjectChildDef child : def.getChildDefs()) {
					CMEObjectType type = child.getType();
					String childName = child.getChildName();
					String refColName = child.getRefColName();
					System.out.println("type: " + type + ", refColName: " + refColName + ", childName: " + childName);
					
					if (childName.equals("EntityAddress")
						|| childName.equals("EntityType")
						|| childName.equals("CountryName")
						|| childName.equals("EntAdrResType")
						|| childName.equals("State")
						|| childName.equals("CredBureau")
						|| childName.equals("ChkAcctYN")
						|| childName.equals("CustProspect")
						|| childName.equals("Gender")
						|| childName.equals("IDType")
						|| childName.equals("LLCClassType")
						|| childName.equals("MarriedStatus")
						|| childName.equals("NAICSCode")
						|| childName.equals("ParticipationMethod")
						|| childName.equals("PartnerType")
						|| childName.equals("Citizenship")
					) continue;
					
					Map<String, Object> childParams = new HashMap<String, Object>();
					boolean addChild = true;
					for (CMEChildRelKeysDef key : child.getKeys()) {
						String childColumnName = key.getColumnName();
						String parentColumnName = key.getParentColumnName();
						Object value = cmeObject.getValue(parentColumnName);
						
						DataType dataType = def.getColumnByName(parentColumnName).getDataType();
						
						if (value == null || (dataType == DataType.INTEGER && Integer.parseInt(value.toString()) == 0)) {
							addChild = false;
							break;
						}
						
						System.out.println(parentColumnName + ": " + childColumnName + ": " + value);
						childParams.put(childColumnName, value);
					}
					if (addChild) {
						if (queryParams.get("references").equals("link")) {
							cmeObject.setValue(refColName, CMEAPIUtils.getURL(uriInfo.getBaseUri().toASCIIString(),
									childName, ("get" + childName), childParams));
						} else if (queryParams.get("references").equals("expand")) {
							List<CMEObject> cmeObjects = CMEObjectController.search(childName, null, null, childParams);
							if (cmeObjects != null && cmeObjects.size() > 0) {
								if (type == CMEObjectType.GROUP) {
									cmeObject.setValue(refColName, cmeObjects);
								} else {
									CMEObject cmeObj = cmeObjects.get(0);
									//System.out.println(childName + ": " + cmeObj.getType());
									cmeObject.setValue(refColName, cmeObj);
								}
							}
							//cmeObject.setValue(refColName, CMEAPIUtils.get(childName, uriInfo, childParams));
						}
					}
				}
			}
			ObjectNode node = new ObjectMapper().valueToTree(cmeObject);
			node.put("href", CMEAPIUtils.getURL(uriInfo.getBaseUri().toASCIIString(),
					name, ("get" + name), params));
			return node;
		} else {
			return null;
		}
	}
	*/
	
	///*
	public static CMEObject update(String name, UriInfo uriInfo, JsonNode json) {
		try {
			Map<String, Object> map = new ObjectMapper().treeToValue(json.get("values"), Map.class);
			CMEObject cmeObject = new CMEObject(json.get("type").asText());
			cmeObject.setValues(map);
			/*
			Map<String, Object> keyParams = new HashMap<String, Object>();
			CMEObjectDef def = CMEObjectDef.getCmeObjDef(name);
			for (CMEObjectColumnDef column : def.getKeyColumns()) {
				String columnName = column.getColumnName();
				Object value = cmeObject.getValue(columnName);
				keyParams.put(columnName, value);
				if (cmeObject.getValue(columnName) == null) {
					cmeObject.setValue(columnName, value);
				}
			}
			*/
			CMEObjectController.update(cmeObject);
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}
	
	public static ObjectNode insert(String name, UriInfo uriInfo, JsonNode json) {
		try {
			Map<String, Object> map = new ObjectMapper().treeToValue(json.get("values"), Map.class);
			CMEObject cmeObject = new CMEObject(json.get("type").asText());
			cmeObject.setValues(map);
			
			cmeObject = CMEObjectController.insert(cmeObject);
			
			Map<String, Object> keyParams = new HashMap<String, Object>();
			CMEObjectDef def = CMEObjectDef.getCmeObjDef(name);
			for (CMEObjectColumnDef column : def.getKeyColumns()) {
				keyParams.put(column.getColumnName(), cmeObject.getValue(column.getColumnName()));
			}
			ObjectNode node = new ObjectMapper().valueToTree(cmeObject);
			node.put("href", CMEAPIUtils_back.getURL(uriInfo.getBaseUri().toASCIIString(),
					name, ("get" + name), keyParams));
			return node;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Object delete(String name, Map<String, Object> params) {
		CMEObjectController.delete(name, params);
		return null;
	}
	
	public static ObjectNode options(String name, UriInfo uriInfo) {
		ObjectNode node = new ObjectMapper().createObjectNode();
		return node;
	}
	
	public static ObjectNode getSchema(String name) {
		ObjectNode node = new ObjectMapper().createObjectNode();
		CMEObjectDef def = CMEObjectDef.getCmeObjDef(name);
		node.put("$schema", "http://json-schema.org/draft-04/schema#");
		ObjectNode properties = node.putObject("properties");
		properties.put("type", "object");
		ObjectNode href = properties.putObject("href");
		href.put("type", "string");
		ObjectNode type = properties.putObject("type");
		type.put("type", "string");
		ObjectNode values = properties.putObject("values");
		values.put("type", "object");
		ObjectNode valueProperties = node.putObject("properties");
		valueProperties.put("type", "object");
		ArrayNode required = valueProperties.putArray("required");
		for (CMEObjectColumnDef column : def.getColumns()) {
			if (column.isVisible()) {
				ObjectNode col = valueProperties.putObject(column.getColumnName());
				switch (column.getDataType()) {
					case INTEGER:
						col.put("type", "int");
						break;
					case DECIMAL:
						col.put("type", "number");
						break;
					case STRING:
						default:
						col.put("type", "string");
						break;
				}
				if (column.isRequired()) {
					required.add(column.getColumnName());
				}
			}
		}
		return node;
	}
	
	private static List<String> getVisibleColumns(UriInfo uriInfo) {
		Map<String, Object> queryParams = 
				UriInfoUtil.normalizeMultivaluedMap(uriInfo.getQueryParameters());
		List<String> columns = new ArrayList<String>();
		if (queryParams.get("columns") != null) {
			//columns.addAll((Arrays.asList(queryParams.get("columns").split(","))));
		}
		return columns;
	}
	
	private static String getURL(String basePath, String name, String methodName, Map<String, Object> params) {
		UriBuilder uriBuilder = UriBuilder.fromResource(CMEAPIUtils_back.classMap.get(name))
			.path(CMEAPIUtils_back.classMap.get(name), methodName);
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			uriBuilder.resolveTemplate(entry.getKey(), entry.getValue());
		}
		URI uri = uriBuilder.build();
		String extendedPath = uri.toASCIIString();
		if (extendedPath.charAt(0) == '/') {
			extendedPath = extendedPath.substring(1, extendedPath.length());
		}
		return basePath + extendedPath;
	}
	
}
