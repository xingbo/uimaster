package org.shaolin.bmdp.persistence.hbm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.BasicBinder;
import org.hibernate.type.descriptor.sql.BasicExtractor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public class SQLCETypeDescriptor implements SqlTypeDescriptor {
	
	public static final SQLCETypeDescriptor INSTANCE = new SQLCETypeDescriptor();

	public int getSqlType() {
		return Types.INTEGER;
	}

	@Override
	public boolean canBeRemapped() {
		return true;
	}

	public <X> ValueBinder<X> getBinder(
			final JavaTypeDescriptor<X> javaTypeDescriptor) {
		return new BasicBinder<X>(javaTypeDescriptor, this) {
			@Override
			protected void doBind(PreparedStatement st, X value, int index,
					WrapperOptions options) throws SQLException {
				st.setInt(index, javaTypeDescriptor.unwrap(value,
						Integer.class, options));
			}
		};
	}

	public <X> ValueExtractor<X> getExtractor(
			final JavaTypeDescriptor<X> javaTypeDescriptor) {
		return new BasicExtractor<X>(javaTypeDescriptor, this) {
			@Override
			protected X doExtract(ResultSet rs, String name,
					WrapperOptions options) throws SQLException {
				return javaTypeDescriptor.wrap(rs.getInt(name), options);
			}
		};
	}

}
