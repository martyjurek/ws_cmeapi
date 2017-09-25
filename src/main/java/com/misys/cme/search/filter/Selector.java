package com.misys.cme.search.filter;

import com.misys.cmeobject.search.Queries.ConditionalOperator;
import com.misys.cmeobject.search.Queries.SelectorNode;

/**
 * Represents a record selector in search filters.
 * Each selector applies a ConditionalOperator to a field.
 */
public class Selector implements SelectorNode {
	private String fieldName;
	private ConditionalOperator operator;
	
	/**
	 * Constructs a Selector from the given field name and JsonNode.
	 * @param fieldName Name of the field the Selector applies to.
	 * @param operator ConditionalOperator that applies to the field.
	 */
	public Selector(String fieldName, ConditionalOperator operator) {
		this.fieldName = fieldName;
		this.operator = operator;
	}
	
	/**
	 * Gets the name of the field this applies to.
	 * @return The name of the field this Selector applies to.
	 */
	public String getFieldName() {
		return fieldName;
	}
	
	/**
	 * Gets the ConditionalOperator that will be applied to the field.
	 * @return The ConditionalOperator that will be applied to the field.
	 */
	public ConditionalOperator getOperator() {
		return operator;
	}
	
	/**
	 * Creates and returns a string representation of this.
	 * @return A String representing this object.
	 */
	@Override
	public String toString() {
		return "Selector{fieldName=" + fieldName + ", operator=" + operator.toString() + "}";
	}
}
