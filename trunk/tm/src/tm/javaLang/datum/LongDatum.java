//     Copyright 1998--2010 Michael Bruce-Lockhart and Theodore S. Norvell
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
//
//     http://www.apache.org/licenses/LICENSE-2.0 
//
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific language 
// governing permissions and limitations under the License.

package tm.javaLang.datum;

import tm.backtrack.BTTimeManager;
import tm.clc.datum.AbstractIntDatum;
import tm.interfaces.Datum;
import tm.javaLang.ast.TyLong;
import tm.utilities.Assert;
import tm.virtualMachine.Memory;
import tm.virtualMachine.VMState;

/*******************************************************************************
Class: LongDatum

Overview:
This class represents a Java long integer (long) data item stored in memory.

Review:          2002 06 13     Jonathan Anderson and Michael Burton
*******************************************************************************/

public class LongDatum extends AbstractIntDatum {
    public static final int size = 8;
    
    public LongDatum(int add, Datum p, Memory m, String name, BTTimeManager timeMan) {
        super(add, size, p, m, name, TyLong.get(), timeMan);
    }

    public LongDatum(int add, Memory m, String name, BTTimeManager timeMan) { 
        this(add,null, m, name, timeMan);
    }
    
    protected LongDatum(LongDatum original){
    	super(original);
    }
    
    public Datum copy(){
    	return new LongDatum(this);
    }

    public void putNativeValue(Object obj) {
   		Assert.check(obj instanceof Long);
   		putValue(((Long)obj).longValue());
   }
   
    public Object getNativeValue() {
        return new Long(getValue());
    }

    public Class getNativeClass() {
        return long.class;
    }

    public boolean input(VMState vms) {
        boolean success = DatumUtilities.inputInt(vms, this, size, false);
        return success;
    }
}