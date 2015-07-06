package org.shaolin.bmdp.persistence.hbm;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.shaolin.bmdp.runtime.ce.AbstractConstant;

public class JavaCETypeDescriptor extends AbstractTypeDescriptor<AbstractConstant> {
	
	public static final JavaCETypeDescriptor INSTANCE = new JavaCETypeDescriptor();

	public JavaCETypeDescriptor() {
		super(AbstractConstant.class);
	}

	public String toString(AbstractConstant value) {
		// -1 is unspecified.
		return value == null ? "-1" : value.toString();
	}

	public AbstractConstant fromString(String string) {
		// TODO: 
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	public <X> X unwrap(AbstractConstant value, Class<X> type, WrapperOptions options) {
		if (value == null) {
			return null;
		}
		if (Integer.class.isAssignableFrom(type)) {
			return (X) Integer.valueOf(value.getIntValue());
		}
		throw unknownUnwrap(type);
	}

	public <X> AbstractConstant wrap(X value, WrapperOptions options) {
		if (value == null) {
			return null;
		}
		if (Integer.class.isInstance(value)) {
			return (AbstractConstant) value;
		}
		throw unknownWrap(value.getClass());
	}
}
