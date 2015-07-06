package org.shaolin.bmdp.persistence.hbm;

import java.io.Serializable;
import java.util.Comparator;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.PrimitiveType;
import org.hibernate.type.VersionType;
import org.shaolin.bmdp.runtime.ce.AbstractConstant;

public class CEType extends AbstractSingleColumnStandardBasicType<AbstractConstant>
		implements PrimitiveType<AbstractConstant>, DiscriminatorType<AbstractConstant>,
		VersionType<AbstractConstant> {

	public static final CEType INSTANCE = new CEType();

	public CEType() {
		super(SQLCETypeDescriptor.INSTANCE, JavaCETypeDescriptor.INSTANCE);
	}

	public String getName() {
		return "ce";
	}

	@Override
	public String[] getRegistrationKeys() {
		return new String[] { getName(), int.class.getName(),
				Integer.class.getName() };
	}

	public Serializable getDefaultValue() {
		return "-1"; // unspecified.
	}

	public Class<AbstractConstant> getPrimitiveClass() {
		return AbstractConstant.class;
	}

	public String objectToSQLString(AbstractConstant value, Dialect dialect)
			throws Exception {
		return toString(value);
	}

	public AbstractConstant stringToObject(String xml) {
		return fromString(xml);
	}

	public AbstractConstant seed(SessionImplementor session) {
		return null;
	}

	public AbstractConstant next(AbstractConstant current, SessionImplementor session) {
		// unsupported.
		return current;
	}

	public Comparator<AbstractConstant> getComparator() {
		return getJavaTypeDescriptor().getComparator();
	}

}
