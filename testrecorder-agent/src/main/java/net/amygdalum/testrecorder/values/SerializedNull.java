package net.amygdalum.testrecorder.values;

import static java.util.Collections.emptyList;

import java.util.Arrays;
import java.util.List;

import net.amygdalum.testrecorder.types.RoleVisitor;
import net.amygdalum.testrecorder.types.ReferenceTypeVisitor;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedValue;

/**
 * Serializing to SerializedNull is only valid and strongly recommended for any value that is null. Use the factory method  
 * {@link #nullInstance()}
 */
public class SerializedNull extends AbstractSerializedReferenceType implements SerializedImmutableType {

	public static final SerializedValue VOID = new SerializedNull(void.class);

	private SerializedNull(Class<?> clazz) {
		super(clazz);
	}
	
	private SerializedNull() {
		super(null);
	}
	
	@Override
	public List<SerializedValue> referencedValues() {
		return emptyList();
	}

	@Override
	public <T> T accept(RoleVisitor<T> visitor) {
		return visitor.visitReferenceType(this);
	}

	@Override
	public <T> T accept(ReferenceTypeVisitor<T> visitor) {
		return visitor.visitImmutableType(this);
	}

	public static SerializedNull nullInstance() {
		return new SerializedNull();
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(getUsedTypes());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SerializedNull that = (SerializedNull) obj;
		return Arrays.equals(this.getUsedTypes(), that.getUsedTypes());
	}

	@Override
	public String toString() {
		return ValuePrinter.print(this);
	}

}
