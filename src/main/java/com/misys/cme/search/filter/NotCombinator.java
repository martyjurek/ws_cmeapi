package com.misys.cme.search.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.misys.cmeobject.search.Queries.Combinator;
import com.misys.cmeobject.search.Queries.CombinatorNode;
import com.misys.cmeobject.search.Queries.Query;

/**
 * Represents the boolean NOT operator in search filters.
 * This is specified by a "$not" field in the filter JSON.
 */
public class NotCombinator implements CombinatorNode {
	private Filter filter;
	
	/**
	 * Constructs a NotCombinator from the given Filter.
	 * @param filter Filter the NotCombinator will operate on.
	 */
	public NotCombinator(Filter filter) {
		this.filter = filter;
	}
	
	/**
	 * Get the Filter this combinator will NOT.
	 * @return The Filter this will NOT.
	 */
	public Filter getFilter() {
		return filter;
	}
	
	/**
	 * Gets the List of Query that will be NOTed by this.
	 * The List will only ever have 1 element.
	 * @return List of Query this operates on.
	 * @note The returned list is unmodifiable.
	 */
	@Override
	public List<Query> getChildren() {
		List<Query> filters = new ArrayList<>();
		filters.add(filter);
		return Collections.unmodifiableList(filters);
	}
	
	/**
	 * Creates and returns a string representation of this.
	 * @return A String representing this object.
	 */
	@Override
	public String toString() {
		return "NotCombinator{filter=" + filter.toString() + "}";
	}

	/**
	 * Gets the type of this Combinator.
	 * @return The type of this Combinator.
	 */
	@Override
	public Combinator getCombinatorType() {
		return Combinator.NOT;
	}
}
