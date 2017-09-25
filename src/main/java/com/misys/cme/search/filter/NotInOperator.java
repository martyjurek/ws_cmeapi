package com.misys.cme.search.filter;

import java.util.List;

import com.misys.cmeobject.search.Queries.NAryConditionalOperator;
import com.misys.cmeobject.search.Queries.Selector;

/**
 * Represents the NOT IN conditional operator in search filters.
 * This is specified by "fieldName": {"$nin": [<values>]} in the filter JSON.
 */
public class NotInOperator implements NAryConditionalOperator {
	private List<EqualityValue> values;
	
	/**
	 * Constructs a NotInOperator from the given ComparisonValue.
	 * @param value Value to compare against.
	 */
	public NotInOperator(List<EqualityValue> values) {
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
		String output = "NotInOperator{values=[";
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
		return Selector.NOT_IN;
	}
}