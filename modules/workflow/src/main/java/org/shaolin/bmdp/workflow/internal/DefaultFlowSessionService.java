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
package org.shaolin.bmdp.workflow.internal;

import java.util.concurrent.atomic.AtomicLong;

import org.shaolin.bmdp.runtime.spi.Event;
import org.shaolin.bmdp.workflow.spi.SessionService;
import org.shaolin.bmdp.workflow.spi.TimeoutEvent;
import org.shaolin.bmdp.workflow.spi.WorkflowSession;

public class DefaultFlowSessionService implements SessionService {
	
	private final AtomicLong sessionIdSeq = new AtomicLong(0);
	
	private static final ThreadLocal<StringBuilder> idBuilder = new ThreadLocal<StringBuilder>() {
		@Override
		public StringBuilder initialValue() {
			return new StringBuilder();
		}
	};

	private static final class DefaultWorkflowSession implements WorkflowSession {
		private final String sessionId;

		public DefaultWorkflowSession(String id) {
			this.sessionId = id;
		}

		@Override
		public String getID() {
			return sessionId;
		}

		@Override
		public int getTXFlag() {
			return WorkflowSession.COMMIT;
		}
	}

	@Override
	public WorkflowSession createSession(Event evt, String id) {
		return new DefaultWorkflowSession(id);
	}

	@Override
	public WorkflowSession getSession(Event evt, String id) {
		if (evt instanceof TimeoutEvent && ((TimeoutEvent) evt).fromTimerNode()) {
			return ((TimeoutEvent) evt).getContext().getSession();
		}
		return null;
	}

	@Override
	public void commitSession(WorkflowSession session) {
		// do nothing
	}

	@Override
	public void rollbackSession(WorkflowSession session) {
		// do nothing
	}

	@Override
	public void destroySession(WorkflowSession session) {
		// do nothing
	}

	@Override
	public String getSessionId(Event evt) {
		StringBuilder stringBuilder = idBuilder.get();
		stringBuilder.setLength(0);
		stringBuilder.append(sessionIdSeq.getAndIncrement());
		return stringBuilder.toString();
	}

	@Override
	public void pasueSession(WorkflowSession session) {
		// do nothing
	}

	@Override
	public void resumeSession(WorkflowSession session) {
		// do nothing
	}

}
