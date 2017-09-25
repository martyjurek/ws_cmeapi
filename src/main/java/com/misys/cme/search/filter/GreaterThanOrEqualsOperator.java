package com.misys.cme.search.filter;

import com.misys.cmeobject.search.Queries.BinaryConditionalOperator;
import com.misys.cmeobject.search.Queries.Selector;

/**
 * Represents the '>=' conditional operator in search filters.
 * This is specified by "fieldName": {"$gte": <some value>} in the filter JSON.
 */
public class GreaterThanOrEqualsOperator implements BinaryConditionalOperator {
	private ComparisonValue value;
	
	/**
	 * Constructs a GreaterThanOrEqualsOperator from the given ComparisonValue.
	 * @param value Value to compare against.
	 */
	public GreaterThanOrEqualsOperator(ComparisonValue value) {
		this.value = value;
	}
	
	/**
	 * Gets the value this compares against.
	 * @return The value this compares against.
	 */
	@Override
	public ComparisonValue getValueNode() {
		return value;
	}
	
	/**
	 * Creates and returns a string representation of this.
	 * @return A String representing this object.
	 */
	@Override
	public String toString() {
		return "GreaterThanOrEqualsOperator{value=" + value.toString() + "}";
	}

	/**
	 * Gets the type of this selector operator.
	 * @return The type of this selector operator.
	 */
	@Override
	public Selector getType() {
		return Selector.GTE;
	}
}
