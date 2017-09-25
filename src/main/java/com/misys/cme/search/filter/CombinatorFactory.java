package com.misys.cme.search.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.misys.cmeobject.search.Queries.CombinatorNode;
import com.misys.cmeobject.search.Queries.Query;

/**
 * Contains static functions for creating Combinators.
 */
public class CombinatorFactory {
	private static List<String> combinatorFieldNames = Arrays.asList("$and", "$or", "$nand", "$nor", "$not");
	
	/**
	 * Creates a new Combinator from the given field name and JsonNode.
	 * @param fieldName Name of the field in the filter JSON that specifies the type of Combinator.
	 * @param fieldValue JsonNode that is associated with fieldName in the filter JSON.
	 * @return The new Combinator.
	 */
	public static CombinatorNode create(String fieldName, JsonNode fieldValue) {
		switch (fieldName) {
			case "$and":
				List<Query> andFilters = createListOfFilters(fieldValue);
				return new AndCombinator(andFilters);
			case "$or":
				List<Query> orFilters = createListOfFilters(fieldValue);
				return new OrCombinator(orFilters);
			case "$nand":
			    List<Query> nandFilters = createListOfFilters(fieldValue);
			    return new NandCombinator(nandFilters);
			case "$nor":
				List<Query> norFilters = createListOfFilters(fieldValue);
				return new NorCombinator(norFilters);
			case "$not":
				return createNotCombinator(fieldValue);
			default:
				throw new RuntimeException("Cannot create combinator from field \"" + fieldName + "\"!");
		}
	}
	
	/**
	 * Checks whether the given field name is a valid Combinator field name.
	 * @param fieldName Field name to check.
	 * @return true if fieldName is a valid Combinator field name; false otherwise.
	 */
	public static boolean isCombinatorFieldName(String fieldName) {
		return combinatorFieldNames.contains(fieldName);
	}
	
	/**
	 * Creates a List of Filters from the given JsonNode.
	 * @param jsonNode JsonNode to create the List from.
	 * @return A new List of Filters.
	 */
	private static List<Query> createListOfFilters(JsonNode jsonNode) {
		if (jsonNode.isArray()) {
			List<Query> filters = new ArrayList<>();
			for (JsonNode filterNode : (ArrayNode)jsonNode) {
				if (filterNode.isObject()) {
					filters.add(new Filter((ObjectNode)filterNode));
				} else {
					throw new RuntimeException("Every filter in a combinator must be an object!");
				}
			}
			return filters;
		} else {
			throw new RuntimeException("Can only create a combinator from an array!");
		}
	}
	
	/**
	 * Creates a new NotCombinator from the given JsonNode.
	 * @param jsonNode JsonNode to create the NotCombinator with.
	 * @return A new NotCombinator.
	 */
	private static NotCombinator createNotCombinator(JsonNode jsonNode) {
		if (jsonNode.isObject()) {
			return new NotCombinator(new Filter((ObjectNode)jsonNode));
		} else {
			throw new RuntimeException("The filter for a $not combinator must be an object!");
		}
	}
}
