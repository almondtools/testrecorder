package net.amygdalum.testrecorder.util;

public final class Literals {

	private Literals() {
	}

	public static String asLiteral(Character c) {
		StringBuilder buffer = new StringBuilder();
		buffer.append('\'');
		if (c == '\n') {
			buffer.append("\\n");
		} else if (c == '\r') {
			buffer.append("\\r");
		} else if (c == '\\') {
			buffer.append("\\\\");
		} else if (c == '\'') {
			buffer.append("\\'");
		} else if (c < 0x20 || c >= 0x7f) {
			buffer.append("\\u");
			if (c < 0x10) {
				buffer.append("000");
			} else if (c < 0x100) {
				buffer.append("00");
			} else if (c < 0x1000) {
				buffer.append('0');
			}
			buffer.append(Integer.toString(c, 16));
		} else {
			buffer.append(c);
		}
		buffer.append('\'');
		return buffer.toString();
	}

	public static String asLiteral(String rawString) {
	    if (rawString == null) {
	        return "null";
	    }
		StringBuilder buffer = new StringBuilder();
		buffer.append('\"');
		for (int i = 0; i < rawString.length(); ++i) {
			char c = rawString.charAt(i);
			if (c == '\n') {
				buffer.append("\\n");
			} else if (c == '\r') {
				buffer.append("\\r");
			} else if (c == '\\') {
				buffer.append("\\\\");
			} else if (c == '"') {
				buffer.append("\\\"");
			} else if (c < 0x20 || c >= 0x7f) {
				buffer.append("\\u");
				if (c < 0x10) {
					buffer.append("000");
				} else if (c < 0x100) {
					buffer.append("00");
				} else if (c < 0x1000) {
					buffer.append('0');
				}
				buffer.append(Integer.toString(c, 16));
			} else {
				buffer.append(c);
			}
		}
		buffer.append('\"');
		return buffer.toString();
	}

	public static String asLiteral(Float f) {
		if (f.isNaN()) {
			return "Float.NaN";
		} else if (f.floatValue() == Float.POSITIVE_INFINITY) {
			return "Float.POSITIVE_INFINITY";
		} else if (f.floatValue() == Float.NEGATIVE_INFINITY) {
			return "Float.NEGATIVE_INFINITY";
		} else {
			return f.toString() + "f";
		}
	}

	public static String asLiteral(Double d) {
		if (d.isNaN()) {
			return "Double.NaN";
		} else if (d.doubleValue() == Double.POSITIVE_INFINITY) {
			return "Double.POSITIVE_INFINITY";
		} else if (d.doubleValue() == Double.NEGATIVE_INFINITY) {
			return "Double.NEGATIVE_INFINITY";
		} else {
			return d.toString();
		}
	}

	public static String asLiteral(Object value) {
		if (value instanceof String) {
			return asLiteral((String) value);
		} else if (value instanceof Character) {
			return asLiteral((Character) value);
		} else if (value instanceof Byte) {
			return "(byte) " + value.toString();
		} else if (value instanceof Short) {
			return "(short) " + value.toString();
		} else if (value instanceof Integer) {
			return value.toString();
		} else if (value instanceof Float) {
			return asLiteral((Float) value);
		} else if (value instanceof Long) {
			return value.toString() + "l";
		} else if (value instanceof Double) {
			return asLiteral((Double) value);
        } else if (value == null){
            return "null";
		} else {
			return value.toString();
		}
	}

	public static String classOf(String name) {
		return name + ".class";
	}

}
