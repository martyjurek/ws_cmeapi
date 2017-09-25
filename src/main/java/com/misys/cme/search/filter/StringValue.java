package com.misys.cme.search.filter;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.UUID;

import com.misys.cmeobject.search.Queries.ValueNode;
import com.misys.cmeobject.search.Queries.ValueType;

/**
 * Represents a string value.
 */
public class StringValue implements ValueNode {
	private String value;
	private ValueType type;
	
	/**
	 * Constructs a StringValue representing a null value.
	 */
	public StringValue() {
		type = ValueType.NULL;
	}

	/**
	 * Constructs a StringValue from the given String.
	 * @param value String value to store.
	 */
	public StringValue(String value) {
		if (value != null) {
			this.value = value;
			type = ValueType.TEXT;
		} else {
			type = ValueType.NULL;
		}
	}

	/**
	 * Gets the type of the value this contains.
	 * @return The type of the value this contains.
	 */
	@Override
	public ValueType getValueType() {
		return type;
	}

	/**
	 * Checks whether this contains a null value.
	 * @return true if this contains a null value; false otherwise.
	 */
	@Override
	public boolean isNull() {
		return ValueType.NULL.equals(type);
	}
	
	/**
	 * Gets the boolean value this contains.
	 * @return The boolean value this contains.
	 * @warning StringValues don't support this operation!
	 */
	@Override
	public Boolean getBoolean() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Gets the Date this contains.
	 * @return The Date this contains.
	 * @warning StringValues don't support this operation!
	 */
	@Override
	public Date getDate() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets the Number this contains.
	 * @return The Number this contains.
	 * @warning StringValues don't support this operation!
	 */
	@Override
	public Number getNumber() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets the text this contains.
	 * @return The text this contains as a String.
	 */
	@Override
	public String getText() {
		return value;
	}
	
	/**
	 * Gets the Time this contains.
	 * @return The Time this contains.
	 * @warning StringValues don't support this operation!
	 */
	@Override
	public Time getTime() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets the Timestamp this contains.
	 * @return The Timestamp this contains.
	 * @warning StringValues don't support this operation!
	 */
	@Override
	public Timestamp getTimestamp() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets the UUID this contains.
	 * @return The UUID this contains.
	 * @warning StringValues don't support this operation!
	 */
	@Override
	public UUID getUUID() {
		throw new UnsupportedOperationException();
	}
}
