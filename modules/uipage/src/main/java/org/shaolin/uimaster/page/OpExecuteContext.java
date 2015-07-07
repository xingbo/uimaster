/*
* Copyright 2015 The UIMaster Project
*
* The UIMaster Project licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/
package org.shaolin.uimaster.page;

import java.io.Serializable;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.shaolin.bmdp.exceptions.BusinessOperationException;
import org.shaolin.javacc.context.EvaluationContext;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.context.ParsingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class OpExecuteContext extends OOEEContext implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(OpExecuteContext.class);

	protected UserTransaction userTransaction = null;

	private EvaluationContext externalEvaluationContext = null;

	private ParsingContext externalParseContext = null;

	public OpExecuteContext() {
		super();
	}

	public void setExternalParseContext(ParsingContext context) {
		this.externalParseContext = context;
	}

	public void setExternalEvaluationContext(EvaluationContext context) {
		this.externalEvaluationContext = context;
	}

	public ParsingContext getExternalParseContext() {
		return externalParseContext;
	}

	public EvaluationContext getExternalEvaluationContext() {
		return externalEvaluationContext;
	}

	public static final String JNDI_USERTRANSACTION = "java:comp/UserTransaction";

	public void beginTransaction() throws BusinessOperationException {
		if (logger.isDebugEnabled())
			logger.debug("beginTransaction()");
		// STATUS_ACTIVE
		if (isInTransaction()) {
			throw new BusinessOperationException("It's already in the transaction.");
		}
		try {
			userTransaction = getUserTransaction();
			userTransaction.begin();
		} catch (Exception ex) {
			throw new BusinessOperationException("Failed to start the transaction", ex);
		}

	}

	public static UserTransaction getUserTransaction()
			throws NamingException {
		Hashtable<?, ?> prop = WebConfig.getInitialContext();
		Context context = new InitialContext(prop);
		return (UserTransaction) context.lookup(JNDI_USERTRANSACTION);
	}

	public void commitTransaction() throws BusinessOperationException {
		if (logger.isDebugEnabled())
			logger.debug("commitTransaction()");
		if (userTransaction == null) {
			throw new BusinessOperationException("The user transaction is not existed.");
		} else {
			try {
				userTransaction.commit();
			} catch (Exception ex) {
				throw new BusinessOperationException("Failed to commit the user transaction.", ex);
			}
		}

		// reset user transaction
		userTransaction = null;

	}

	public void rollbackTransaction() throws BusinessOperationException {
		if (logger.isDebugEnabled())
			logger.debug("rollbackTransaction()");
		if (userTransaction == null) {
			throw new BusinessOperationException("The user transaction is not existed.");
		} else {
			try {
				userTransaction.rollback();
			} catch (Exception ex) {
				userTransaction = null;
				throw new BusinessOperationException("Failed to rollback the user transaction.", ex);
			}
		}
		userTransaction = null;

	}

	public boolean isInTransaction() {
		if (userTransaction != null) {
			int utStatus = Status.STATUS_COMMITTED;
			try {
				utStatus = userTransaction.getStatus();
			} catch (Exception ex1) {
				logger.warn("Exception when userTransaction.getStatus", ex1);
			}
			if (Status.STATUS_ACTIVE == utStatus) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}

	}

}
