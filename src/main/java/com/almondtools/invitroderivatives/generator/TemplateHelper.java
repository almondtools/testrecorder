package com.almondtools.invitroderivatives.generator;

public final class TemplateHelper {

	private TemplateHelper() {
	}

	public static String asLiteral(String rawString) {
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
			} else if (c < 0x20 || c > 0x7f) {
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
		String string = buffer.toString();
		return string;
	}
}
