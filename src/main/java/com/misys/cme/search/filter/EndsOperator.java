package com.misys.cme.search.filter;

import com.misys.cmeobject.search.Queries.MatchType;
import com.misys.cmeobject.search.Queries.Selector;
import com.misys.cmeobject.search.Queries.WildcardConditionalOperator;

/**
 * Represents the ENDS WITH conditional operator in search filters.
 * This is specified by "fieldName": {"$ends": "<some text>"} in the filter JSON.
 */
public class EndsOperator implements WildcardConditionalOperator {
	private String value;
	
	/**
	 * Constructs a EndsOperator from the given String.
	 * @param value Value to compare against.
	 */
	public EndsOperator(String value) {
		this.value = value;
	}
	
	/**
	 * Gets the value this compares against.
	 * @return The value this compares against.
	 */
	@Override
	public StringValue getValueNode() {
		return new StringValue(value);
	}
	
	/**
	 * Creates and returns a string representation of this.
	 * @return A String representing this object.
	 */
	@Override
	public String toString() {
		return "EndsOperator{value=" + value + "}";
	}

	/**
	 * Gets the type of this selector operator.
	 * @return The type of this selector operator.
	 */
	@Override
	public Selector getType() {
		return Selector.LIKE;
	}

	/**
	 * Gets the MatchType this operator uses.
	 * @return The MatchType this operator uses.
	 */
	@Override
	public MatchType getMatchType() {
		return MatchType.ENDS;
	}
}
