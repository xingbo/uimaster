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
package org.shaolin.bmdp.persistence.query.mapping;

//imports
import java.util.LinkedList;
import java.util.List;

public class JavaToSqlCommand
{
    public JavaToSqlCommand()
    {
    }
    
    public ILobWriter getLobWriter()
    {
    	throw new UnsupportedOperationException("No lob writer specified for this command");
    }

    public void bind(Object value, int columnType)
    {
        values.add(value);
    }
    
    public void bindLong(long l)
    {
        values.add(new Long(l));
    }
    
    public Object[] toArray()
    {
        return values.toArray();
    }
    
    private List values = new LinkedList();

}
