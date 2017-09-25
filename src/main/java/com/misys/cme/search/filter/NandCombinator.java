package com.misys.cme.search.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.misys.cmeobject.search.Queries.Combinator;
import com.misys.cmeobject.search.Queries.CombinatorNode;
import com.misys.cmeobject.search.Queries.Query;

/**
 * Represents the boolean NAND operator in search filters.
 * This is specified by a "$nand" field in the filter JSON.
 */
public class NandCombinator implements CombinatorNode {
	private List<Query> filters;

	/**
	 * Constructs a NandCombinator from the given List of Query.
	 * @param filters List of Query to be NANDed together.
	 */
	public NandCombinator(List<? extends Query> filters) {
		this.filters = new ArrayList<>();
		this.filters.addAll(filters);
	}

	/**
	 * Gets the List of Query that will be NANDed together by this.
	 * @return List of Query this NandCombinator is combining.
	 * @note The returned list is unmodifiable.
	 */
	@Override
	public List<Query> getChildren() {
		return Collections.unmodifiableList(filters);
	}

	/**
	 * Creates and returns a string representation of this.
	 * @return A String representing this object.
	 */
	@Override
	public String toString() {
		String output = "NandCombinator{filters=[";
		boolean haveAddedFilter = false;
		for (Query filter : filters) {
			if (!haveAddedFilter) {
				output += filter.toString();
				haveAddedFilter = true;
			} else {
				output += ", " + filter.toString();
			}
		}
		output += "]}";
		return output;
	}

	/**
	 * Gets the type of this Combinator.
	 * @return The type of this Combinator.
	 */
	@Override
	public Combinator getCombinatorType() {
		return Combinator.NAND;
	}
}
