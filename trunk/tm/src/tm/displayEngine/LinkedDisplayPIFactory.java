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

package tm.displayEngine;

import tm.interfaces.DisplayContextInterface;
import tm.plugins.Requirement;
import tm.subWindowPkg.SubWindow;

/**
 * 
 * A description of the Type and its responsibilities
 *
 * @author mpbl
 */
public class LinkedDisplayPIFactory implements DisplayPIFactoryIntf{
	
	private String configId;
	
	private LinkedDisplayPIFactory(String parameter){
		configId = parameter;
	}

	
    static public LinkedDisplayPIFactory createInstance( String parameter ) {
    	return new LinkedDisplayPIFactory(parameter) ;
    }
	public DisplayInterface createPlugin(DisplayManager dm) {		
		return new LinkedDisplay(dm,configId);
	}

	public Requirement[] getRequirements() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getParameter() {
		return configId;
	}

	public String toString(){return "LinkedDisplayPiFactory("+configId+")";}


}
