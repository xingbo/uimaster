package org.shaolin.bmdp.coordinator.dao;

import java.util.List;
import java.util.ArrayList;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import org.hibernate.criterion.Order;

import org.shaolin.bmdp.persistence.BEEntityDaoObject;
import org.shaolin.bmdp.persistence.HibernateUtil;
import org.shaolin.bmdp.persistence.query.operator.Operator;

/**
 * This code is generated automatically, any change will be replaced after rebuild.
 */
public class CoordinatorModel extends BEEntityDaoObject {

    public static final CoordinatorModel INSTANCE = new CoordinatorModel();

    private CoordinatorModel() {
    }

    public List<org.shaolin.bmdp.coordinator.be.TaskImpl> searchTasks(org.shaolin.bmdp.coordinator.be.TaskImpl scFlow,
           List<Order> orders, int offset, int count) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        try {
            Criteria inFlowCriteria = this._createCriteria(session, org.shaolin.bmdp.coordinator.be.TaskImpl.class, "inFlow");
            if (orders == null) {
            } else {
                this._addOrders(inFlowCriteria, orders);
            }

            if (scFlow.getId() > 0) {
                inFlowCriteria.add(createCriterion(Operator.EQUALS, "inFlow.id", scFlow.getId()));
            }
            if (scFlow.getSubject() != null && scFlow.getSubject().length() > 0) {
                inFlowCriteria.add(createCriterion(Operator.START_WITH_RIGHT, "inFlow.subject", scFlow.getSubject()));
            }
            if (scFlow.getTriggerTimeStart() != null) {
                inFlowCriteria.add(createCriterion(Operator.GREATER_THAN_OR_EQUALS, "inFlow.triggerTime", scFlow.getTriggerTimeStart()));
            }
            if (scFlow.getTriggerTimeEnd() != null) {
                inFlowCriteria.add(createCriterion(Operator.LESS_THAN_OR_EQUALS, "inFlow.triggerTime", scFlow.getTriggerTimeEnd()));
            }
            if (scFlow.getStatus() != null && scFlow.getStatus() != org.shaolin.bmdp.coordinator.ce.TaskStatusType.NOT_SPECIFIED) {
                inFlowCriteria.add(createCriterion(Operator.EQUALS, "inFlow.status", scFlow.getStatus().getIntValue()));
            }
            if (scFlow.getPriority() != null && scFlow.getPriority() != org.shaolin.bmdp.coordinator.ce.TaskPriorityType.NOT_SPECIFIED) {
                inFlowCriteria.add(createCriterion(Operator.EQUALS, "inFlow.priority", scFlow.getPriority().getIntValue()));
            }

            List result = this._list(offset, count, inFlowCriteria);
            return result;
        } finally {
            session.getTransaction().commit();
        }
    }

    public List<org.shaolin.bmdp.coordinator.be.TaskImpl> searchTasks(org.shaolin.bmdp.coordinator.be.TaskImpl scFlow,
           Session session, List<Order> orders, int offset, int count) {
        try {
            Criteria inFlowCriteria = this._createCriteria(session, org.shaolin.bmdp.coordinator.be.TaskImpl.class, "inFlow");
            if (orders == null) {
            } else {
                this._addOrders(inFlowCriteria, orders);
            }

            if (scFlow.getId() > 0) {
                inFlowCriteria.add(createCriterion(Operator.EQUALS, "inFlow.id", scFlow.getId()));
            }
            if (scFlow.getSubject() != null && scFlow.getSubject().length() > 0) {
                inFlowCriteria.add(createCriterion(Operator.START_WITH_RIGHT, "inFlow.subject", scFlow.getSubject()));
            }
            if (scFlow.getTriggerTimeStart() != null) {
                inFlowCriteria.add(createCriterion(Operator.GREATER_THAN_OR_EQUALS, "inFlow.triggerTime", scFlow.getTriggerTimeStart()));
            }
            if (scFlow.getTriggerTimeEnd() != null) {
                inFlowCriteria.add(createCriterion(Operator.LESS_THAN_OR_EQUALS, "inFlow.triggerTime", scFlow.getTriggerTimeEnd()));
            }
            if (scFlow.getStatus() != null && scFlow.getStatus() != org.shaolin.bmdp.coordinator.ce.TaskStatusType.NOT_SPECIFIED) {
                inFlowCriteria.add(createCriterion(Operator.EQUALS, "inFlow.status", scFlow.getStatus().getIntValue()));
            }
            if (scFlow.getPriority() != null && scFlow.getPriority() != org.shaolin.bmdp.coordinator.ce.TaskPriorityType.NOT_SPECIFIED) {
                inFlowCriteria.add(createCriterion(Operator.EQUALS, "inFlow.priority", scFlow.getPriority().getIntValue()));
            }

            List result = this._list(offset, count, inFlowCriteria);
            return result;
        } finally {
        }
    }

    public long searchTasksCount(org.shaolin.bmdp.coordinator.be.TaskImpl scFlow) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        try {
            Criteria inFlowCriteria = this._createCriteria(session, org.shaolin.bmdp.coordinator.be.TaskImpl.class, "inFlow");

            if (scFlow.getId() > 0) {
                inFlowCriteria.add(createCriterion(Operator.EQUALS, "inFlow.id", scFlow.getId()));
            }
            if (scFlow.getSubject() != null && scFlow.getSubject().length() > 0) {
                inFlowCriteria.add(createCriterion(Operator.START_WITH_RIGHT, "inFlow.subject", scFlow.getSubject()));
            }
            if (scFlow.getTriggerTimeStart() != null) {
                inFlowCriteria.add(createCriterion(Operator.GREATER_THAN_OR_EQUALS, "inFlow.triggerTime", scFlow.getTriggerTimeStart()));
            }
            if (scFlow.getTriggerTimeEnd() != null) {
                inFlowCriteria.add(createCriterion(Operator.LESS_THAN_OR_EQUALS, "inFlow.triggerTime", scFlow.getTriggerTimeEnd()));
            }
            if (scFlow.getStatus() != null && scFlow.getStatus() != org.shaolin.bmdp.coordinator.ce.TaskStatusType.NOT_SPECIFIED) {
                inFlowCriteria.add(createCriterion(Operator.EQUALS, "inFlow.status", scFlow.getStatus().getIntValue()));
            }
            if (scFlow.getPriority() != null && scFlow.getPriority() != org.shaolin.bmdp.coordinator.ce.TaskPriorityType.NOT_SPECIFIED) {
                inFlowCriteria.add(createCriterion(Operator.EQUALS, "inFlow.priority", scFlow.getPriority().getIntValue()));
            }

            return this._count(inFlowCriteria);
        } finally {
            session.getTransaction().commit();
        }
    }

    public long searchTasksCount(org.shaolin.bmdp.coordinator.be.TaskImpl scFlow, Session session) {
        try {
            Criteria inFlowCriteria = this._createCriteria(session, org.shaolin.bmdp.coordinator.be.TaskImpl.class, "inFlow");

            if (scFlow.getId() > 0) {
                inFlowCriteria.add(createCriterion(Operator.EQUALS, "inFlow.id", scFlow.getId()));
            }
            if (scFlow.getSubject() != null && scFlow.getSubject().length() > 0) {
                inFlowCriteria.add(createCriterion(Operator.START_WITH_RIGHT, "inFlow.subject", scFlow.getSubject()));
            }
            if (scFlow.getTriggerTimeStart() != null) {
                inFlowCriteria.add(createCriterion(Operator.GREATER_THAN_OR_EQUALS, "inFlow.triggerTime", scFlow.getTriggerTimeStart()));
            }
            if (scFlow.getTriggerTimeEnd() != null) {
                inFlowCriteria.add(createCriterion(Operator.LESS_THAN_OR_EQUALS, "inFlow.triggerTime", scFlow.getTriggerTimeEnd()));
            }
            if (scFlow.getStatus() != null && scFlow.getStatus() != org.shaolin.bmdp.coordinator.ce.TaskStatusType.NOT_SPECIFIED) {
                inFlowCriteria.add(createCriterion(Operator.EQUALS, "inFlow.status", scFlow.getStatus().getIntValue()));
            }
            if (scFlow.getPriority() != null && scFlow.getPriority() != org.shaolin.bmdp.coordinator.ce.TaskPriorityType.NOT_SPECIFIED) {
                inFlowCriteria.add(createCriterion(Operator.EQUALS, "inFlow.priority", scFlow.getPriority().getIntValue()));
            }

            return this._count(inFlowCriteria);
        } finally {
        }
    }

    public List<org.shaolin.bmdp.coordinator.be.TaskImpl> searchPendingTasks(org.shaolin.bmdp.coordinator.be.TaskImpl scFlow,
           List<Order> orders, int offset, int count) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        try {
            Criteria inFlowCriteria = this._createCriteria(session, org.shaolin.bmdp.coordinator.be.TaskImpl.class, "inFlow");
            if (orders == null) {
            } else {
                this._addOrders(inFlowCriteria, orders);
            }

            if (scFlow.getId() > 0) {
                inFlowCriteria.add(createCriterion(Operator.EQUALS, "inFlow.id", scFlow.getId()));
            }
            if (true) {
                inFlowCriteria.add(createCriterion(Operator.IN, "inFlow.statusInt", org.shaolin.bmdp.coordinator.ce.TaskStatusType.NOTSTARTED.getIntValue()));
            }
            if (scFlow.getPriority() != null && scFlow.getPriority() != org.shaolin.bmdp.coordinator.ce.TaskPriorityType.NOT_SPECIFIED) {
                inFlowCriteria.add(createCriterion(Operator.EQUALS, "inFlow.priorityInt", scFlow.getPriority().getIntValue()));
            }

            List result = this._list(offset, count, inFlowCriteria);
            return result;
        } finally {
            session.getTransaction().commit();
        }
    }

    public List<org.shaolin.bmdp.coordinator.be.TaskImpl> searchPendingTasks(org.shaolin.bmdp.coordinator.be.TaskImpl scFlow,
           Session session, List<Order> orders, int offset, int count) {
        try {
            Criteria inFlowCriteria = this._createCriteria(session, org.shaolin.bmdp.coordinator.be.TaskImpl.class, "inFlow");
            if (orders == null) {
            } else {
                this._addOrders(inFlowCriteria, orders);
            }

            if (scFlow.getId() > 0) {
                inFlowCriteria.add(createCriterion(Operator.EQUALS, "inFlow.id", scFlow.getId()));
            }
            if (true) {
                inFlowCriteria.add(createCriterion(Operator.IN, "inFlow.statusInt", org.shaolin.bmdp.coordinator.ce.TaskStatusType.NOTSTARTED.getIntValue()));
            }
            if (scFlow.getPriority() != null && scFlow.getPriority() != org.shaolin.bmdp.coordinator.ce.TaskPriorityType.NOT_SPECIFIED) {
                inFlowCriteria.add(createCriterion(Operator.EQUALS, "inFlow.priorityInt", scFlow.getPriority().getIntValue()));
            }

            List result = this._list(offset, count, inFlowCriteria);
            return result;
        } finally {
        }
    }

    public long searchPendingTasksCount(org.shaolin.bmdp.coordinator.be.TaskImpl scFlow) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        try {
            Criteria inFlowCriteria = this._createCriteria(session, org.shaolin.bmdp.coordinator.be.TaskImpl.class, "inFlow");

            if (scFlow.getId() > 0) {
                inFlowCriteria.add(createCriterion(Operator.EQUALS, "inFlow.id", scFlow.getId()));
            }
            if (true) {
                inFlowCriteria.add(createCriterion(Operator.IN, "inFlow.statusInt", org.shaolin.bmdp.coordinator.ce.TaskStatusType.NOTSTARTED.getIntValue()));
            }
            if (scFlow.getPriority() != null && scFlow.getPriority() != org.shaolin.bmdp.coordinator.ce.TaskPriorityType.NOT_SPECIFIED) {
                inFlowCriteria.add(createCriterion(Operator.EQUALS, "inFlow.priorityInt", scFlow.getPriority().getIntValue()));
            }

            return this._count(inFlowCriteria);
        } finally {
            session.getTransaction().commit();
        }
    }

    public long searchPendingTasksCount(org.shaolin.bmdp.coordinator.be.TaskImpl scFlow, Session session) {
        try {
            Criteria inFlowCriteria = this._createCriteria(session, org.shaolin.bmdp.coordinator.be.TaskImpl.class, "inFlow");

            if (scFlow.getId() > 0) {
                inFlowCriteria.add(createCriterion(Operator.EQUALS, "inFlow.id", scFlow.getId()));
            }
            if (true) {
                inFlowCriteria.add(createCriterion(Operator.IN, "inFlow.statusInt", org.shaolin.bmdp.coordinator.ce.TaskStatusType.NOTSTARTED.getIntValue()));
            }
            if (scFlow.getPriority() != null && scFlow.getPriority() != org.shaolin.bmdp.coordinator.ce.TaskPriorityType.NOT_SPECIFIED) {
                inFlowCriteria.add(createCriterion(Operator.EQUALS, "inFlow.priorityInt", scFlow.getPriority().getIntValue()));
            }

            return this._count(inFlowCriteria);
        } finally {
        }
    }

}

