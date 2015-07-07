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
package org.shaolin.uimaster.page.ajax;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Order;
import org.shaolin.bmdp.runtime.be.IBusinessEntity;

public class TableConditions implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private IBusinessEntity condition;
	private Object additionalCondition;

	private List<Order> orders;
	private int offset;
	private int count = 10;//10 records per query by default.
	
	private int currentSelectedIndex = -1;
	
	private int[] selectedIndex;

	public TableConditions() {
	}
	
	public static final TableConditions createCondition() {
		return new TableConditions();
	}
	
	public static final TableConditions createCondition(IBusinessEntity be) {
		TableConditions condition = new TableConditions();
		condition.setBECondition(be);
		return condition;
	}
	
	public static final TableConditions createCondition(Object object) {
		TableConditions condition = new TableConditions();
		condition.setAdditionalCondition(object);
		return condition;
	}
	
	public Object getAdditionalCondition() {
		return additionalCondition;
	}

	public void setAdditionalCondition(Object additionalCondition) {
		this.additionalCondition = additionalCondition;
	}
	
	public List<Order> getOrders() {
		return orders;
	}

	public void addOrder(Order order) {
		if (this.orders == null) {
			this.orders = new ArrayList<Order>();
		}
		this.orders.add(order);
	}
	
	public void clearOrder() {
		if (this.orders != null) {
			this.orders.clear();
		}
	}
	
	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public IBusinessEntity getCondition() {
		return condition;
	}

	public void setBECondition(IBusinessEntity condition) {
		this.condition = condition;
	}

	public int getCurrentSelectedIndex() {
		return currentSelectedIndex;
	}

	public void setCurrentSelectedIndex(int currentSelectedIndex) {
		this.currentSelectedIndex = currentSelectedIndex;
	}

	public int[] getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int[] selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

}
