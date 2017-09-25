package com.misys.cme.search.filter;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.misys.cmeobject.search.Queries.ConditionalOperator;
import com.misys.cmeobject.search.Queries.ValueType;

/**
 * Contains static functions for creating ConditionalOperators.
 */
public class ConditionalOperatorFactory {
	private static List<String> comparisonOperators = Arrays.asList("$lt", "$lte", "$gt", "$gte");
	private static List<String> equalityOperators = Arrays.asList("$eq", "$ne");
	private static List<String> inOperators = Arrays.asList("$in", "$nin");
	private static List<String> stringOperators = Arrays.asList("$begins", "$contains", "$ends");
	
	/**
	 * Creates a new ConditionalOperator from the given JsonNode.
	 * If the JsonNode is a value, this will create an EqualsOperator.
	 * @param jsonNode JsonNode specifying the ConditionalOperator to create.
	 * @return The new ConditionalOperator.
	 */
	public static ConditionalOperator create(JsonNode jsonNode) {
		// Get operator name and its JsonNode value
		String operatorName;
		JsonNode operatorValue;
		if (jsonNode.isObject()) {
			ObjectNode operatorObject = (ObjectNode) jsonNode;
			if (operatorObject.size() == 1) {
				operatorName = operatorObject.fieldNames().next();
				operatorValue = operatorObject.get(operatorName);
			} else {
				throw new RuntimeException("Only one conditional operator per selector can be specified at a time!");
			}
		} else if (jsonNode.isValueNode()) {
			operatorName = "$eq";
			operatorValue = jsonNode;
		} else {
			throw new RuntimeException("A field selector must be a literal value or an object!");
		}
		
		// Parse JsonNode value according to operator type; then create and return the operator
		if (isComparisonOperator(operatorName)) {
			try {
				ComparisonValue value = createComparisonValue(operatorValue);
				return createComparisonOperator(operatorName, value);
			} catch (Exception e) {
				throw new RuntimeException("The " + operatorName + " conditional operator must be provided a numeric, date, time, timestamp, or UUID value!", e);
			}
		} else if (isEqualityOperator(operatorName)) {
			try {
				EqualityValue value = createEqualityValue(operatorValue);
				return createEqualityOperator(operatorName, value);
			} catch (Exception e) {
				throw new RuntimeException("The " + operatorName + " conditional operator must be provided a value!", e);
			}
		} else if (isInOperator(operatorName)) {
			try {
				List<EqualityValue> values = createListOfEqualityValues(operatorValue);
				return createInOperator(operatorName, values);
			} catch (Exception e) {
				throw new RuntimeException("The " + operatorName + " conditional operator must be provided an array of values!", e);
			}
		} else if (isStringOperator(operatorName)) {
			if (operatorValue.isTextual()) {
				return createStringOperator(operatorName, ((TextNode)operatorValue).asText());
			} else {
				throw new RuntimeException("The " + operatorName + " string operator must be provided a text value!");
			}
		} else if (operatorName.equals("$mod")) {
			try {
				Number divisor = getDivisor(operatorValue);
				Number remainder = getRemainder(operatorValue);
				return new ModuloOperator(divisor, remainder);
			} catch (Exception e) {
				throw new RuntimeException("The $mod operator must be provided an array of 2 numbers!", e);
			}
		} else if (operatorName.equals("$elemMatch")) {
			if (operatorValue.isObject()) {
				Filter filter = new Filter((ObjectNode)operatorValue);
				return new ElemMatchOperator(filter);
			} else {
				throw new RuntimeException("The $elemMatch operator must be provided an object containing a search filter!");
			}
		} else {
			throw new RuntimeException("Cannot create conditional operator from field \"" + operatorName + "\"!");
		}
	}
	
	/**
	 * Creates a comparison operator from the given operator name and ComparisonValue.
	 * @param operatorName Name of the operator to create.
	 * @param value Value for the operator to compare against.
	 * @return A new comparison operator.
	 */
	private static ConditionalOperator createComparisonOperator(String operatorName, ComparisonValue value) {
		switch (operatorName) {
			case "$lt":
				return new LessThanOperator(value);
			case "$lte":
				return new LessThanOrEqualsOperator(value);
			case "$gt":
				return new GreaterThanOperator(value);
			case "$gte":
				return new GreaterThanOrEqualsOperator(value);
			default:
				throw new RuntimeException("Cannot create comparison operator from field \"" + operatorName + "\"!");
		}
	}
	
	/**
	 * Creates a ComparisonValue from the given JsonNode.
	 * @param jsonNode JsonNode to create the ComparisonValue from.
	 * @return A new ComparisonValue.
	 * @throws Exception if jsonNode doesn't contain a number, date, time, timestamp, or UUID value.
	 */
	private static ComparisonValue createComparisonValue(JsonNode jsonNode) throws Exception {
		if (jsonNode.isNumber()) {
			return new ComparisonValue(((NumericNode)jsonNode).numberValue());
		} else if (jsonNode.isTextual()) {
			String text = ((TextNode)jsonNode).textValue();
			if (isTimestamp(text)) {
				return new ComparisonValue(toTimestamp(text));
			} else if (isDate(text)) {
				return new ComparisonValue(toDate(text));
			} else if (isTime(text)) {
				return new ComparisonValue(toTime(text));
			} else if (isUUID(text)) {
				return new ComparisonValue(UUID.fromString(text));
			}
		}
		throw new Exception("Failed to create a ComparisonValue from the following JsonNode: " + jsonNode);
	}
	
	/**
	 * Creates an equality operator from the given operator name and EqualityValue
	 * @param operatorName Name of the operator to create.
	 * @param value Value for the operator to compare against.
	 * @return A new equality operator.
	 */
	private static ConditionalOperator createEqualityOperator(String operatorName, EqualityValue value) {
		switch (operatorName) {
			case "$eq":
				return new EqualsOperator(value);
			case "$ne":
				return new NotEqualsOperator(value);
			default:
				throw new RuntimeException("Cannot create equality operator from field \"" + operatorName + "\"!");
		}
	}
	
	/**
	 * Creates an EqualityValue from the given JsonNode.
	 * @param jsonNode JsonNode to create the EqualityValue from.
	 * @return A new EqualityValue.
	 * @throws Exception if jsonNode doesn't contain a boolean, null, number, date, text, time, timestamp, or UUID value.
	 */
	private static EqualityValue createEqualityValue(JsonNode jsonNode) throws Exception {
		if (jsonNode.isNumber()) {
			return new EqualityValue(((NumericNode)jsonNode).numberValue());
		} else if (jsonNode.isBoolean()) {
			return new EqualityValue(((BooleanNode)jsonNode).booleanValue());
		} else if (jsonNode.isNull()) {
			return new EqualityValue();
		} else if (jsonNode.isTextual()) {
			String text = ((TextNode)jsonNode).textValue();
			if (isTimestamp(text)) {
				return new EqualityValue(toTimestamp(text));
			} else if (isDate(text)) {
				return new EqualityValue(toDate(text));
			} else if (isTime(text)) {
				return new EqualityValue(toTime(text));
			} else if (isUUID(text)) {
				return new EqualityValue(UUID.fromString(text));
			} else {
				return new EqualityValue(text);
			}
		}
		throw new Exception("Failed to create an EqualityValue from the following JsonNode: " + jsonNode);
	}
	
	/**
	 * Creates an "in" operator from the given operator name and List of EqualityValues.
	 * @param operatorName Name of the operator to create.
	 * @param values Values for the operator to compare against.
	 * @return A new "in" operator.
	 */
	private static ConditionalOperator createInOperator(String operatorName, List<EqualityValue> values) {
		switch (operatorName) {
			case "$in":
				return new InOperator(values);
			case "$nin":
				return new NotInOperator(values);
			default:
				throw new RuntimeException("Cannot create \"in\" operator from field \"" + operatorName + "\"!");
		}
	}
	
	/**
	 * Creates a List of EqualityValues from the given JsonNode.
	 * @param jsonNode JsonNode to create the List from.
	 * @return A new List of EqualityValues.
	 * @throws Exception if jsonNode isn't an array, it contains an invalid value (see createEqualityValue), or it contains values of different types.
	 */
	private static List<EqualityValue> createListOfEqualityValues(JsonNode jsonNode) throws Exception {
		if (jsonNode.isArray()) {
			List<EqualityValue> values = new ArrayList<>();
			for (JsonNode value : (ArrayNode)jsonNode) {
				values.add(createEqualityValue(value));
			}
			
			ValueType valueType = null;
			for (EqualityValue value : values) {
				if (valueType == null) {
					valueType = value.getValueType();
				} else {
					if (value.getValueType() != valueType) {
						throw new Exception("Failed to create a List of EqualityValues from the following JsonNode: " + jsonNode);
					}
				}
			}
			
			return values;
		} else {
			throw new Exception("Failed to create a List of EqualityValues from the following JsonNode: " + jsonNode);
		}
	}
	
	/**
	 * Creates a string operator from the given operator name and String value.
	 * @param operatorName Name of the operator to create.
	 * @param value String value to compare against.
	 * @return A new string operator.
	 */
	private static ConditionalOperator createStringOperator(String operatorName, String value) {
		switch (operatorName) {
			case "$begins":
				return new BeginsOperator(value);
			case "$contains":
				return new ContainsOperator(value);
			case "$ends":
				return new EndsOperator(value);
			default:
				throw new RuntimeException("Cannot create string operator from field \"" + operatorName + "\"!");
		}
	}
	
	/**
	 * Gets the divisor for the $mod operator from the given JsonNode.
	 * @param jsonNode JsonNode to get the divisor from.
	 * @return The divisor as a Number.
	 * @throws Exception if jsonNode isn't an array, the array doesn't contain exactly 2 values, or the values aren't numbers.
	 */
	private static Number getDivisor(JsonNode jsonNode) throws Exception {
		if (jsonNode.isArray()) {
			ArrayNode arrayNode = (ArrayNode)jsonNode;
			if (arrayNode.size() == 2) {
				JsonNode divisorNode = arrayNode.get(0);
				if (divisorNode.isNumber()) {
					return ((NumericNode)divisorNode).numberValue();
				} else {
					throw new Exception("The array for the $mod conditional operator must only contain numbers!");
				}
			} else {
				throw new Exception("The array for the $mod conditional operator must have exactly 2 values!");
			}
		} else {
			throw new Exception("The value for the $mod conditional operator must be an array!");
		}
	}
	
	/**
	 * Gets the remainder for the $mod operator from the given JsonNode.
	 * @param jsonNode JsonNode to get the remainder from.
	 * @return The remainder as a Number.
	 * @throws Exception if jsonNode isn't an array, the array doesn't contain exactly 2 values, or the values aren't numbers.
	 */
	private static Number getRemainder(JsonNode jsonNode) throws Exception {
		if (jsonNode.isArray()) {
			ArrayNode arrayNode = (ArrayNode)jsonNode;
			if (arrayNode.size() == 2) {
				JsonNode remainderNode = arrayNode.get(1);
				if (remainderNode.isNumber()) {
					return ((NumericNode)remainderNode).numberValue();
				} else {
					throw new Exception("The array for the $mod conditional operator must only contain numbers!");
				}
			} else {
				throw new Exception("The array for the $mod conditional operator must have exactly 2 values!");
			}
		} else {
			throw new Exception("The value for the $mod conditional operator must be an array!");
		}
	}
	
	/**
	 * Checks if the given String is the name of a comparison operator.
	 * Comparison operator names: $lt, $lte, $gt, $gte
	 * @param s String to check.
	 * @return true if the String is the name of a comparison operator; false otherwise.
	 */
	private static boolean isComparisonOperator(String s) {
		return comparisonOperators.contains(s);
	}
	
	/**
	 * Checks whether the given String can be converted to a Date.
	 * @param s String to check.
	 * @return true if the String can be converted to a Date; false otherwise.
	 */
	private static boolean isDate(String s) {
		try {
			DatatypeConverter.parseDate(s);
			return true;
		} catch (Throwable t) {
			return false;
		}
	}
	
	/**
	 * Checks if the given String is the name of an equality operator.
	 * Equality operator names: $eq, $ne, $in, $nin
	 * @param s String to check.
	 * @return true if the String is the name of an equality operator; false otherwise.
	 */
	private static boolean isEqualityOperator(String s) {
		return equalityOperators.contains(s);
	}
	
	/**
	 * Checks if the given String is the name of an "in" operator.
	 * "In" operator names: $in, $nin
	 * @param s String to check.
	 * @return true if the String is the name of an "in" operator; false otherwise.
	 */
	private static boolean isInOperator(String s) {
		return inOperators.contains(s);
	}
	
	/**
	 * Checks if the given String is the name of a string operator.
	 * String operator names: $begins, $contains, $ends
	 * @param s String to check.
	 * @return true if the String is the name of a string operator; false otherwise.
	 */
	private static boolean isStringOperator(String s) {
		return stringOperators.contains(s);
	}
	
	/**
	 * Checks whether the given String can be converted to a Time.
	 * @param s String to check.
	 * @return true if the String can be converted to a Time; false otherwise.
	 */
	private static boolean isTime(String s) {
		try {
			DatatypeConverter.parseTime(s);
			return true;
		} catch (Throwable t) {
			return false;
		}
	}
	
	/**
	 * Checks whether the given String can be converted to a Timestamp.
	 * @param s String to check.
	 * @return true if the String can be converted to a Timestamp; false otherwise.
	 */
	private static boolean isTimestamp(String s) {
		try {
			DatatypeConverter.parseDateTime(s);
			return true;
		} catch (Throwable t) {
			return false;
		}
	}
	
	/**
	 * Checks whether the given String can be converted to a UUID.
	 * @param s String to check.
	 * @return true if the String can be converted to a UUID; false otherwise.
	 */
	private static boolean isUUID(String s) {
		try {
			UUID.fromString(s);
			return true;
		} catch (Throwable t) {
			return false;
		}
	}
	
	/**
	 * Converts the given String to a Date.
	 * @note The Date type here is java.sql.Date and not java.util.Date.
	 * @param s String to convert.
	 * @return A new Date.
	 */
	private static Date toDate(String s) {
		Calendar calendar = DatatypeConverter.parseDate(s);
		return new Date(calendar.getTimeInMillis());
	}
	
	/**
	 * Converts the given String to a Time.
	 * @param s String to convert.
	 * @return A new Time.
	 */
	private static Time toTime(String s) {
		Calendar calendar = DatatypeConverter.parseTime(s);
		return new Time(calendar.getTimeInMillis());
	}
	
	/**
	 * Converts the given String to a Timestamp.
	 * @param s String to convert.
	 * @return A new Timestamp.
	 */
	private static Timestamp toTimestamp(String s) {
		Calendar calendar = DatatypeConverter.parseDateTime(s);
		return new Timestamp(calendar.getTimeInMillis());
	}
}
