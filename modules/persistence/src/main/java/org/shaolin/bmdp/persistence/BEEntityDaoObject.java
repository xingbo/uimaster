package org.shaolin.bmdp.persistence;

import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.shaolin.bmdp.persistence.query.operator.Operator;
import org.shaolin.bmdp.runtime.be.IPersistentEntity;

public class BEEntityDaoObject {

	public void addResource(String hbmMapping) {
		HibernateUtil.getConfiguration().addResource(hbmMapping);
	}
	
	/**
	 * Insert a business entity record to database.
	 * 
	 * @param entity
	 */
	public void create(IPersistentEntity entity) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		session.save(entity);

//		session.flush();
//	    session.clear();
		session.getTransaction().commit();
	}

	/**
	 * Delete a business entity record to database.
	 * 
	 * @param entity
	 */
	public void delete(IPersistentEntity entity) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		session.delete(entity);

		session.getTransaction().commit();
	}

	/**
	 * Update a business entity record to database.
	 * 
	 * @param entity
	 */
	public void update(IPersistentEntity entity) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		session.update(entity);

		session.getTransaction().commit();
	}
	
	/**
	 * Batch insert for multiple entities.
	 * 
	 * @param entities
	 */
	public void batchInsert(List<IPersistentEntity> entities) {
		if (entities == null || entities.size() == 0) {
			return;
		}
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		for (IPersistentEntity entity: entities) {
			session.save(entity);
		}

		session.getTransaction().commit();
	}
	
	/**
	 * Batch update for multiple entities.
	 * 
	 * @param entities
	 */
	public void batchUpdate(List<IPersistentEntity> entities) {
		if (entities == null || entities.size() == 0) {
			return;
		}
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		for (IPersistentEntity entity: entities) {
			session.update(entity);
		}

		session.getTransaction().commit();
	}

	/**
	 * Batch delete for multiple entities.
	 * 
	 * @param entities
	 */
	public void batchDelete(List<IPersistentEntity> entities) {
		if (entities == null || entities.size() == 0) {
			return;
		}
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		for (IPersistentEntity entity: entities) {
			session.delete(entity);
		}

		session.getTransaction().commit();
	}
	
	/**
	 * Delete a business entity record to database.
	 * 
	 * @param entity
	 */
	public void disable(IPersistentEntity entity) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		entity.setEnabled(false);
		session.update(entity);

		session.getTransaction().commit();
	}

	/**
	 * Enable a business entity record to database.
	 * 
	 * @param entity
	 */
	public void enable(IPersistentEntity entity) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		entity.setEnabled(true);
		session.update(entity);

		session.flush();
	    session.clear();
		session.getTransaction().commit();
	}
	
	public <T> List<T> list(int offset, int count, Class<T> elementType, 
			Class<?> persistentClass) {
		return list( offset, count, elementType, persistentClass, null, null);
	}
	
	public <T> List<T> list(int offset, int count, Class<T> elementType, 
			String alias) {
		return list(offset, count, elementType, elementType, alias, null);
	}
	
	public <T> List<T> list(int offset, int count, Class<T> elementType, 
			Class<?> persistentType, String alias) {
		return list(offset, count, elementType, persistentType, alias, null, null);
	}

	public <T> List<T> list(int offset, int count, Class<T> elementType, 
			Class<?> persistentType, String alias, List<Criterion> criterions) {
		return list(offset, count, elementType, persistentType, alias, criterions, null);
	}
	
	/**
	 * Single table query with returning a list of the business entity recorded by conditions.
	 * 
	 * @param offset
	 * @param count
	 * @param elementType
	 * @param tableInfo
	 * @param criterions
	 * @param orders
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> list(int offset, int count, Class<T> elementType, 
			Class<?> persistentType, String alias, List<Criterion> criterions, List<Order> orders) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try {
			Criteria criteria = null;
			if (alias != null) {
				criteria = session.createCriteria(persistentType, alias);
			} else {
				criteria = session.createCriteria(persistentType);
			}
			if (count > 0) {
				criteria.setFirstResult(offset);
				criteria.setMaxResults(count);
			}
			
			if (criterions != null) {
				for (Criterion c : criterions) {
					criteria.add(c);
				}
				// Restrictions
			}
			if (orders != null) {
				for (Order c : orders) {
					criteria.addOrder(c);
				}
			}
			
			return criteria.list();
		} finally {
			session.getTransaction().commit();
		}
	}
	
	/**
	 * 
	 * Create Criteria. internal only.
	 * 
	 * @param session
	 * @param persistentType
	 * @param alias
	 * @return
	 */
	protected Criteria _createCriteria(Session session, Class<?> persistentType, String alias) {
		return session.createCriteria(persistentType, alias);
	}

	/**
	 * Join table query support. internal only.
	 * 
	 * @param criteria
	 * @param refField
	 * @param alias
	 * @return
	 */
	protected Criteria _createJoinAlias(Criteria criteria, String refField, String alias) {
		return criteria.createAlias(refField, alias);
	}
	
	protected void _addOrders(Criteria criteria, List<Order> orders) {
		if (orders != null && !orders.isEmpty()) {
			for (Order r: orders) {
				criteria.addOrder(r);
			}
		}
	}
	
	/**
	 * Does query. internal only.
	 * 
	 * @param offset
	 * @param count
	 * @param criteria
	 * @param criterions
	 * @param orders
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> List<T> _list(int offset, int count, Criteria criteria) {
		if (count > 0) {
			criteria.setFirstResult(offset);
			criteria.setMaxResults(count);
		}
		return criteria.list();
	}
	
	/**
	 * Does count query. internal only.
	 * 
	 * 
	 * @param criteria
	 * @param criterions
	 * @return
	 */
	protected long _count(Criteria criteria) {
		criteria.setProjection(Projections.rowCount());
		return (Long)criteria.uniqueResult();
	}
	
	public long count(Class<?> persistentClass) {
		return count(persistentClass, null, null);
	}
	
	public long count(Class<?> persistentClass, String alias) {
		return count(persistentClass, alias, null);
	}
	
	/**
	 * Single table query with returning the total count of current business entities.
	 * 
	 * @param queryStr
	 * @return
	 */
	public long count(Class<?> persistentType, String alias, 
			List<Criterion> criterions) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		try {
			Criteria criteria = null;
			if (alias != null) {
				criteria = session.createCriteria(persistentType, alias);
			} else {
				criteria = session.createCriteria(alias);
			}
			if (criterions != null && !criterions.isEmpty()) {
				for (Criterion c : criterions) {
					criteria.add(c);
				}
			}
			
			criteria.setProjection(Projections.rowCount());
			return (Long)criteria.uniqueResult();
		} finally {
			session.getTransaction().commit();
		}
	}

	/**
	 * 
	 * @param offset
	 * @param count
	 * @param sql
	 * @param condition
	 * @return
	 */
	public <T> List<T> sqlList(int offset, int count, StringBuffer sql, List<Object> condition, List<SQLTableInfo> tableInfo) {
 		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
 		Transaction tx = session.beginTransaction();
 		try {
	 		SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
	 		sqlQuery.setFirstResult(offset);
	 		sqlQuery.setMaxResults(count);
	 		
			for (int i = 0; i < condition.size(); i++) {
				Object value = condition.get(i);
				if (value instanceof String) {
					sqlQuery.setString(i, value.toString());
				} else if (value instanceof Integer) {
					sqlQuery.setInteger(i, (Integer)value);
				} else if (value instanceof Long) {
					sqlQuery.setLong(i, (Long)value);
				} else if (value instanceof Boolean) {
					sqlQuery.setBoolean(i, (Boolean)value);
				} else if (value instanceof Date) {
					sqlQuery.setDate(i, (Date)value);
				} else if (value instanceof Calendar) {
					sqlQuery.setCalendar(i, (Calendar)value);
				} else {
					sqlQuery.setString(i, value.toString());
				}
			}
			for (SQLTableInfo info : tableInfo) {
				sqlQuery.addEntity(info.tableAlias, info.pElementType);
			}
	 		
	 		return sqlQuery.list();
 		} finally {
 			tx.commit();
		}
	}
	
	/**
	 * 
	 * @param sql
	 * @param condition
	 * @return
	 */
	public long sqlCount(StringBuffer sql, List<Object> condition) {
 		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
 		Transaction tx = session.beginTransaction();
 		try {
	 		SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
	 		
	 		for (int i = 0; i < condition.size(); i++) {
	 			Object value = condition.get(i);
				if (value instanceof String) {
					sqlQuery.setString(i, value.toString());
				} else if (value instanceof Integer) {
					sqlQuery.setInteger(i, (Integer)value);
				} else if (value instanceof Long) {
					sqlQuery.setLong(i, (Long)value);
				} else if (value instanceof Boolean) {
					sqlQuery.setBoolean(i, (Boolean)value);
				} else if (value instanceof Date) {
					sqlQuery.setDate(i, (Date)value);
				} else if (value instanceof Calendar) {
					sqlQuery.setCalendar(i, (Calendar)value);
				} else {
					sqlQuery.setString(i, value.toString());
				}
			}
	 		
			BigInteger result = (BigInteger)sqlQuery.uniqueResult();
			return result.longValue();
 		} finally {
 			tx.commit();
		}
	}
	
	protected class SQLTableInfo {
		String tableAlias;
		Class<?> pElementType;

		public SQLTableInfo(String tableAlias, Class<?> elementType) {
			this.tableAlias = tableAlias;
			this.pElementType = elementType;
		}
	}

	protected void endParameters(StringBuffer sb) {
		int sbLength = sb.length();
		if (sbLength > 0) {
			sb.delete(sbLength - " AND ".length(), sbLength);
			sb.insert(0, '(');
			sb.append(')');
		}
	}
	
	protected void addTableInfo(ArrayList<SQLTableInfo> tableInfo,
			String tableAlias, Class<?> elementType) {
		SQLTableInfo inFileInfo = new SQLTableInfo(tableAlias, elementType);
		tableInfo.add(inFileInfo);
	}
	
	/**
	 * Create criterions.
	 * 
	 * @param type
	 * @param propertyName
	 * @param value
	 * @return
	 */
	protected Criterion createCriterion(final Operator operator, String propertyName, Object... value) {
		if (Operator.IS_NULL == operator && value == null) {
			throw new IllegalStateException("PropertyName [" + propertyName + "], OperatorType[" 
					+ operator + "] the value can't be null.");
		}
		
		if (Operator.EQUALS == operator) {
			return Restrictions.eq(propertyName, value[0]);
		} else if (Operator.EQUALS_IGNORE_CASE == operator) {
			return Restrictions.eq(propertyName, value[0]);
		} else if (Operator.IS_NULL == operator) {
			return Restrictions.isNull(propertyName);
		} else if (Operator.GREATER_THAN == operator) {
			return Restrictions.gt(propertyName, value[0]);
		} else if (Operator.GREATER_THAN_OR_EQUALS == operator) {
			return Restrictions.ge(propertyName, value[0]);
		} else if (Operator.LESS_THAN == operator) {
			return Restrictions.lt(propertyName, value[0]);
		} else if (Operator.LESS_THAN_OR_EQUALS == operator) {
			return Restrictions.le(propertyName, value[0]);
		} else if (Operator.IN == operator) {
			return Restrictions.in(propertyName, value);
		} else if (Operator.BETWEEN == operator) {
			if (value.length < 2) {
				throw new IllegalStateException("Two value required for SQL Between Operator.");
			}
			return Restrictions.between(propertyName, value[0], value[1]);
		} else if (Operator.EXISTS == operator) {
			//TODO:
			
		} else if (Operator.START_WITH == operator || Operator.START_WITH_LEFT == operator) {
			return Restrictions.ilike(propertyName, value[0].toString(), MatchMode.START);
		} else if (Operator.START_WITH_IGNORE_CASE == operator) {
			return Restrictions.like(propertyName, value[0].toString(), MatchMode.START);
		} else if (Operator.START_WITH_RIGHT == operator) {
			return Restrictions.ilike(propertyName, value[0].toString(), MatchMode.END);
		} else if (Operator.CONTAINS_PARTIAL == operator) {
			return Restrictions.ilike(propertyName, value[0].toString(), MatchMode.ANYWHERE);
		} else if (Operator.CONTAINS_PARTIAL_IGNORE_CASE == operator) {
			return Restrictions.like(propertyName, value[0].toString(), MatchMode.ANYWHERE);
		} else if (Operator.CONTAINS_WORD == operator) {
			return Restrictions.ilike(propertyName, value[0].toString(), MatchMode.EXACT);
		} else if (Operator.CONTAINS_WORD_IGNORE_CASE == operator) {
			return Restrictions.like(propertyName, value[0].toString(), MatchMode.EXACT);
		} 
		
		throw new IllegalStateException("Unsupported SQL OperatorType : " + operator);
	}
	
}
