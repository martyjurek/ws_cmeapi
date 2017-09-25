package com.misys.cme.search.filter;

import com.misys.cmeobject.search.Queries.BinaryConditionalOperator;
import com.misys.cmeobject.search.Queries.Selector;

/**
 * Represents the NOT EQUALS conditional operator in search filters.
 * This is specified by "fieldName": {"$ne": <some value>} in the filter JSON.
 */
public class NotEqualsOperator implements BinaryConditionalOperator {
	private EqualityValue value;
	
	/**
	 * Constructs a NotEqualsOperator from the given EqualityValue.
	 * @param value Value to compare against.
	 */
	public NotEqualsOperator(EqualityValue value) {
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
		return "NotEqualsOperator{value=" + value.toString() + "}";
	}

	/**
	 * Gets the type of this selector operator.
	 * @return The type of this selector operator.
	 */
	@Override
	public Selector getType() {
		return Selector.NEQUALS;
	}
}
