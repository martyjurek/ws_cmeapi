package com.misys.cme.search.filter;

import java.util.List;

import com.misys.cmeobject.search.Queries.NAryConditionalOperator;
import com.misys.cmeobject.search.Queries.Selector;

/**
 * Represents the IN conditional operator in search filters.
 * This is specified by "fieldName": {"$in": [<values>]} in the filter JSON.
 */
public class InOperator implements NAryConditionalOperator {
	private List<EqualityValue> values;
	
	/**
	 * Constructs a InOperator from the given ComparisonValue.
	 * @param value Value to compare against.
	 */
	public InOperator(List<EqualityValue> values) {
		this.values = values;
	}
	
	/**
	 * Gets the values this compares against.
	 * @return The values this compares against.
	 */
	public List<EqualityValue> getValues() {
		return values;
	}
	
	/**
	 * Creates and returns a string representation of this.
	 * @return A String representing this object.
	 */
	@Override
	public String toString() {
		String output = "InOperator{values=[";
		boolean haveAddedValue = false;
		for (EqualityValue value : values) {
			if (!haveAddedValue) {
				output += value.toString();
				haveAddedValue = true;
			} else {
				output += ", " + value.toString();
			}
		}
		return output += "]}";
	}

	/**
	 * Gets the type of this selector operator.
	 * @return The type of this selector operator.
	 */
	@Override
	public Selector getType() {
		return Selector.IN;
	}
}
