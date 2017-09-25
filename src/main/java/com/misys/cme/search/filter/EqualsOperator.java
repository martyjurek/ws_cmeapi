package com.misys.cme.search.filter;

import com.misys.cmeobject.search.Queries.BinaryConditionalOperator;
import com.misys.cmeobject.search.Queries.Selector;

/**
 * Represents the equality conditional operator in search filters.
 * This is specified by "fieldName": {"$eq": <some value>} or "fieldName": <some value> in the filter JSON.
 */
public class EqualsOperator implements BinaryConditionalOperator {
	private EqualityValue value;
	
	/**
	 * Constructs an EqualsOperator from the given EqualityValue.
	 * @param value Value to compare against.
	 */
	public EqualsOperator(EqualityValue value) {
		this.value = value;
	}
	
	/**
	 * Gets the value this operator will compare against.
	 * @return The value this operator will compare against.
	 */
	@Override
	public EqualityValue getValueNode() {
		return value;
	}
	
	/**
	 * Creates and returns a string representation of this.
	 * @return A String representing this object.
	 */
	@Override
	public String toString() {
		return "EqualsOperator{value=" + value.toString() + "}";
	}

	/**
	 * Gets the type of this selector operator.
	 * @return The type of this selector operator.
	 */
	@Override
	public Selector getType() {
		return Selector.EQUALS;
	}
}
