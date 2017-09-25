package com.misys.cme.search.filter;

import com.misys.cmeobject.search.Queries.ConditionalOperator;
import com.misys.cmeobject.search.Queries.GroupMatchOperator;
import com.misys.cmeobject.search.Queries.Selector;

/**
 * Represents the operator to check if "at least one object in an array matches a filter" in search filters.
 * This is specified by "arrayFieldName": {"$elemMatch": {<some filter>}} in the filter JSON.
 */
public class ElemMatchOperator implements GroupMatchOperator {
	private Filter filter;
	
	/**
	 * Constructs an ElemMatchOperator from the given Filter.
	 * @param filter Filter to apply to the array field.
	 */
	public ElemMatchOperator(Filter filter) {
		this.filter = filter;
	}
	
	/**
	 * Gets the Filter this operator will apply to the array.
	 * @return The Filter this operator will apply to the array.
	 */
	@Override
	public Filter getFilter() {
		return filter;
	}
	
	/**
	 * Gets the type of this selector operator.
	 * @return The type of this selector operator.
	 */
	@Override
	public Selector getType() {
		return Selector.ELEMMATCH;
	}
	
	/**
	 * Creates and returns a string representation of this.
	 * @return A String representing this object.
	 */
	@Override
	public String toString() {
		return "ElemMatchOperator{filter=" + filter.toString() + "}";
	}
}
