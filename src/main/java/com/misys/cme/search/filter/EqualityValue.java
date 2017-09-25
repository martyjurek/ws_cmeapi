package com.misys.cme.search.filter;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.UUID;

import com.misys.cmeobject.search.Queries.ValueType;
import com.misys.cmeobject.search.Queries.ValueNode;

/**
 * Represents a value that can have equality operators applied to it.
 * Equality operators: =, <>
 */
public class EqualityValue implements ValueNode {
	private boolean bool;
	private Date date;
	private Number number;
	private String text;
	private Time time;
	private Timestamp timestamp;
	private UUID uuid;
	private ValueType valueType;
	
	/**
	 * Constructs an EqualityValue representing a null value.
	 */
	public EqualityValue() {
		this.valueType = ValueType.NULL;
	}
	
	/**
	 * Constructs an EqualityValue from the given boolean.
	 * @param bool Boolean value to store.
	 */
	public EqualityValue(Boolean bool) {
		if (bool == null) {
			this.valueType = ValueType.NULL;
		} else {
			this.bool = bool;
			this.valueType = ValueType.BOOLEAN;
		}
	}
	
	/**
	 * Constructs a EqualityValue from the given Date.
	 * @param date Date value to store.
	 */
	public EqualityValue(Date date) {
		if (date == null) {
			this.valueType = ValueType.NULL;
		} else {
			this.date = date;
			this.valueType = ValueType.DATE;
		}
	}
	
	/**
	 * Constructs a EqualityValue from the given Number.
	 * @param number Number value to store.
	 */
	public EqualityValue(Number number) {
		if (number == null) {
			this.valueType = ValueType.NULL;
		} else {
			this.number = number;
			this.valueType = ValueType.NUMBER;
		}
	}
	
	/**
	 * Constructs an EqualityValue from the given text.
	 * @param text String containing the text to store.
	 */
	public EqualityValue(String text) {
		if (text == null) {
			this.valueType = ValueType.NULL;
		} else {
			this.text = text;
			this.valueType = ValueType.TEXT;
		}
	}
	
	/**
	 * Constructs a EqualityValue from the given Time.
	 * @param time Time value to store.
	 */
	public EqualityValue(Time time) {
		if (time == null) {
			this.valueType = ValueType.NULL;
		} else {
			this.time = time;
			this.valueType = ValueType.TIME;
		}
	}
	
	/**
	 * Constructs a EqualityValue from the given Timestamp.
	 * @param timestamp Timestamp value to store.
	 */
	public EqualityValue(Timestamp timestamp) {
		if (timestamp == null) {
			this.valueType = ValueType.NULL;
		} else {
			this.timestamp = timestamp;
			this.valueType = ValueType.TIMESTAMP;
		}
	}
	
	/**
	 * Constructs a EqualityValue from the given UUID.
	 * @param uuid UUID value to store.
	 */
	public EqualityValue(UUID uuid) {
		if (uuid == null) {
			this.valueType = ValueType.NULL;
		} else {
			this.uuid = uuid;
			this.valueType = ValueType.UUID;
		}
	}
	
	/**
	 * Gets the boolean this contains.
	 * @return The boolean this contains.
	 */
	public Boolean getBoolean() {
		if (valueType != ValueType.BOOLEAN) {
			throw new UnsupportedOperationException();
		}
		return bool;
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
	 * Gets the text this contains.
	 * @return The text this contains as a String.
	 */
	public String getText() {
		if (valueType != ValueType.TEXT) {
			throw new UnsupportedOperationException();
		}
		return text;
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
			case NULL:
				return "null";
			case BOOLEAN:
				return Boolean.toString(bool);
			case DATE:
				return date.toString();
			case NUMBER:
				return number.toString();
			case TEXT:
				return text;
			case TIME:
				return time.toString();
			case TIMESTAMP:
				return timestamp.toString();
			case UUID:
				return uuid.toString();
			default:
				throw new RuntimeException("Unknown valueType \"" + valueType + "\" encountered in EqualityValue.toString()!");
		}
	}

	/**
	 * Checks whether this contains a null value.
	 * @return true if this contains a null value; false otherwise.
	 */
	@Override
	public boolean isNull() {
		return ValueType.NULL.equals(valueType); 
	}
}
