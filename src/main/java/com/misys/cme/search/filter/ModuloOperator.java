package com.misys.cme.search.filter;

import com.misys.cmeobject.search.Queries.ConditionalOperator;
import com.misys.cmeobject.search.Queries.Selector;

/**
 * Represents the MODULO conditional operator in search filters.
 * The MODULO operator divides a field and checks the remainder.
 * This is specified by "fieldName": {"$mod": [<divisor>, <remainder>]} in the filter JSON.
 */
public class ModuloOperator implements ConditionalOperator {
	private Number divisor;
	private Number remainder;
	
	/**
	 * Constructs a ModuloOperator from the given divisor and remainder.
	 * @param divisor Number to divide the field by.
	 * @param remainder Number to compare the remainder to.
	 */
	public ModuloOperator(Number divisor, Number remainder) {
		this.divisor = divisor;
		this.remainder = remainder;
	}
	
	/**
	 * Gets the value this will divide by.
	 * @return Value this will divide by.
	 */
	public Number getDivisor() {
		return divisor;
	}
	
	/**
	 * Gets the value this will check the remainder of the division against.
	 * @return Value the remainder of the division will be checked against.
	 */
	public Number getRemainder() {
		return remainder;
	}
	
	/**
	 * Creates and returns a string representation of this.
	 * @return A String representing this object.
	 */
	@Override
	public String toString() {
		return "ModuloOperator{divisor=" + divisor.toString() + ", remainder=" + remainder.toString() + "}";
	}

	/**
	 * Gets the type of this selector operator.
	 * @return The type of this selector operator.
	 */
	@Override
	public Selector getType() {
		return Selector.MODULUS;
	}
}
