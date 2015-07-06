/*
 * Copyright 2000-2009 by BMI Asia, Inc.,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of BMI Asia, Inc.("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with BMI Asia.
 */

package org.shaolin.uimaster.page.ajax;

import java.io.Serializable;

abstract public class Container extends Widget implements Serializable
{
    private static final long serialVersionUID = -8001162150899252012L;

    public Container(String id, Layout layout)
    {
        super(id, layout);
    }

}
