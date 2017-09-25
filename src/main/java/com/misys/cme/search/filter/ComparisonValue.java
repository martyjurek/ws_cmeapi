package com.misys.cme.search.filter;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.UUID;

import com.misys.cmeobject.search.Queries.ValueNode;
import com.misys.cmeobject.search.Queries.ValueType;

/**
 * Represents a value that can have comparison operators applied to it.
 * Comparison operators: <, <=, >, >=
 */
public class ComparisonValue implements ValueNode {
	private Date date;
	private Number number;
	private Time time;
	private Timestamp timestamp;
	private UUID uuid;
	private ValueType valueType;
	
	/**
	 * Constructs a ComparisonValue from the given Date.
	 * @param date Date value to store.
	 */
	public ComparisonValue(Date date) {
		this.date = date;
		this.valueType = ValueType.DATE;
	}
	
	/**
	 * Constructs a ComparisonValue from the given Number.
	 * @param number Number value to store.
	 */
	public ComparisonValue(Number number) {
		this.number = number;
		this.valueType = ValueType.NUMBER;
	}
	
	/**
	 * Constructs a ComparisonValue from the given Time.
	 * @param time Time value to store.
	 */
	public ComparisonValue(Time time) {
		this.time = time;
		this.valueType = ValueType.TIME;
	}
	
	/**
	 * Constructs a ComparisonValue from the given Timestamp.
	 * @param timestamp Timestamp value to store.
	 */
	public ComparisonValue(Timestamp timestamp) {
		this.timestamp = timestamp;
		this.valueType = ValueType.TIMESTAMP;
	}
	
	/**
	 * Constructs a ComparisonValue from the given UUID.
	 * @param uuid UUID value to store.
	 */
	public ComparisonValue(UUID uuid) {
		this.uuid = uuid;
		this.valueType = ValueType.UUID;
	}
	
	/**
	 * Gets the Date this contains.
	 * @return The Date this contains.
	 */
	public Date getDate() {
		if (valueType != ValueType.DATE) {
			throw new UnsupportedOperationException();
		}
		return date;
	}
	
	/**
	 * Gets the Number this contains.
	 * @return The Number this contains.
	 */
	public Number getNumber() {
		if (valueType != ValueType.NUMBER) {
			throw new UnsupportedOperationException();
		}
		return number;
	}
	
	/**
	 * Gets the Time this contains.
	 * @return The Time this contains.
	 */
	public Time getTime() {
		if (valueType != ValueType.TIME) {
			throw new UnsupportedOperationException();
		}
		return time;
	}
	
	/**
	 * Gets the Timestamp this contains.
	 * @return The Timestamp this contains.
	 */
	public Timestamp getTimestamp() {
		if (valueType != ValueType.TIMESTAMP) {
			throw new UnsupportedOperationException();
		}
		return timestamp;
	}
	
	/**
	 * Gets the UUID this contains.
	 * @return The UUID this contains.
	 */
	public UUID getUUID() {
		if (valueType != ValueType.UUID) {
			throw new UnsupportedOperationException();
		}
		return uuid;
	}
	
	/**
	 * Gets the type of the value this contains.
	 * @return The type of the value this contains.
	 */
	public ValueType getValueType() {
		return valueType;
	}
	
	/**
	 * Creates and returns a string representation of this.
	 * @return A String representing this object.
	 */
	@Override
	public String toString() {
		switch (valueType) {
			case DATE:
				return date.toString();
			case NUMBER:
				return number.toString();
			case TIME:
				return time.toString();
			case TIMESTAMP:
				return timestamp.toString();
			case UUID:
				return uuid.toString();
			default:
				throw new RuntimeException("Unknown valueType \"" + valueType + "\" encountered in ComparisonValue.toString()!");
		}
	}

	/**
	 * Checks whether this contains a null value.
	 * @return true if this contains a null value; false otherwise.
	 */
	@Override
	public boolean isNull() {
		return false;
	}

	/**
	 * Gets the boolean value this contains.
	 * @return The boolean value this contains.
	 * @warning ComparisonValues don't support this operation!
	 */
	@Override
	public Boolean getBoolean() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets the text value this contains.
	 * @return The text value this contains.
	 * @warning ComparisonValues don't support this operation!
	 */
	@Override
	public String getText() {
		throw new UnsupportedOperationException();
	}
}
