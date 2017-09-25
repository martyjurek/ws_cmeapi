package com.misys.cme.search.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.misys.cmeobject.search.Queries.ConditionalOperator;
import com.misys.cmeobject.search.Queries.Query;
import com.misys.cmeobject.search.Queries.QueryNode;

/**
 * Represents a search filter.
 */
public class Filter implements Query {
	private QueryNode operator;
	
	/**
	 * Constructs a new filter from the given ObjectNode.
	 * @param filterJSON ObjectNode containing the data for the new Filter.
	 */
	public Filter(ObjectNode filterJSON) {
		if (filterJSON.size() == 1) {
			String fieldName = filterJSON.fieldNames().next();
			if (CombinatorFactory.isCombinatorFieldName(fieldName)) {
				operator = CombinatorFactory.create(fieldName, filterJSON.get(fieldName));
			} else {
				ConditionalOperator selectorOperator = ConditionalOperatorFactory.create(filterJSON.get(fieldName));
				operator = new Selector(fieldName, selectorOperator);
			}
		} else {
			List<Filter> topLevelFilters = new ArrayList<>();
			Iterator<Map.Entry<String, JsonNode>> entryIterator = filterJSON.fields();
			while (entryIterator.hasNext()) {
				Map.Entry<String, JsonNode> entry = entryIterator.next();
				ObjectNode newFilterObject = JsonNodeFactory.instance.objectNode();
				newFilterObject.set(entry.getKey(), entry.getValue());
				topLevelFilters.add(new Filter(newFilterObject));
			}
			operator = new AndCombinator(topLevelFilters);
		}
	}
	
	/**
	 * Gets the operator this Filter contains.
	 * @return The QueryNode this Filter contains.
	 */
	public QueryNode getOperator() {
		return operator;
	}

	/**
	 * Creates and returns a string representation of this.
	 * @return A String representing this object.
	 */
	@Override
	public String toString() {
		return "Filter{operator=" + operator.toString() + "}";
	}
}
