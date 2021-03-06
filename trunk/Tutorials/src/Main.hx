package ;

import js.Browser;
import js.html.Node ;
import js.html.Document ;
import js.html.Element ;
import js.html.Event;
import haxe.Log ;


/** Tutorials
 * @author Theodore Norvell
 */

 class EdgeFunc
	{
		public var edgeId: String;
		public var type: Int;
		//funcList: List<String>;
		
		public function new()
		{
			edgeId = "";
			type = 0;
		}
	}

@:expose class Main {
	
	

	static var tmProxy : TMProxy ;
	static var vertexStack : List<TutorialVertex> = new List<TutorialVertex>() ;
	static var edgeFunctionStack : List <EdgeFunc> = new List <EdgeFunc>();
	//public static var _flag = 0;
	// The following declaration is a trick to ensure that the EdgeFunctions class
	// is linked in.
	static var neverUsed : EdgeFunctions ;
	
	static var tracingEnabled : Bool = false ;
	
	public static function enableTracing() { tracingEnabled = true ; }
	
	public static function disableTracing() { tracingEnabled = false ; }
	
	public static function print( mess : String ) {
		 if( tracingEnabled ) Log.trace( mess ) ;
	 }

	static function main() {
	}
	
	private static function buildGraph( doc : Document ) : TutorialGraph {
		var pageno: Int = 1;
		var graph = new TutorialGraph() ;
		var graphBuilt = true ;
		var graphDomNode : Element ;
		graphDomNode = doc.getElementById("graph") ;
		if (graphDomNode == null ) {
			//print("No element in the html file has id 'graph'") ;
			return null ; }
		var startFunctionName = graphDomNode.getAttribute("data-function") ;
		if ( startFunctionName != null ) graph.setStartFunctionName( startFunctionName ) ;
		else { //print("Graph node has no 'data-function' attribute") ;
			return null ; }
		var child = graphDomNode.firstChild ;
		while ( child != null ) {
			//print( "Current child is  " + child ) ;
			//print( "Node name is " + child.nodeName ) ;
			if ( child.nodeType == NodeTypes.ELEMENT_NODE && child.nodeName == "DIV" ) {
				var childAsElement : Element = cast(child, Element) ;
				var klass = childAsElement.getAttribute("class")  ;
				// Collect all vertices
				if ( klass == null ) {
					//print("A child of the 'graph' element has no class" ) ;
					graphBuilt = false ;  }
				else if ( klass == "vertex" ) {
					var id : String = childAsElement.getAttribute("id") ;
					if ( id == null ) {
						//print("There is a vertex with no id" ) ;
						graphBuilt = false ;}
					else if ( graph.vertices.exists( id ) ) {
						//print( "Duplicate vertex with id '" +id + "'" ) ;
						 graphBuilt = false ; }
					else {
						//print( "Building vertex " + id) ;
						var vertex = new TutorialVertex( id, childAsElement, pageno++ ) ;
						//print(vertex.pageno);
						graph.vertices.set( id, vertex ) ; 
						if ( graph.startVertex == null ) {
							graph.setStartVertex( vertex ) ; } } }
				// Collect all edges
				else if ( klass == "edge" ) {
					var id : String = childAsElement.getAttribute("id") ;
					if ( id == null ) {
						//print("There is an edge with no id" ) ; 
						graphBuilt = false ; } 
					else if ( graph.edges.exists( id ) ) {
						//print( "Duplicate edge with id '" +id + "'" ) ;
						graphBuilt = false ; }
					else {
						//print( "Building edge " + id) ;
						var edge = new TutorialEdge( id, childAsElement ) ;
						graph.edges.set( id, edge ) ; } }
				else {
					print("A child of the 'graph' node has a class '"
						+ klass
						+ "' that is neither 'vertex' nor 'edge'" ) ;
				}
			}
			child = child.nextSibling ;
		}
			
		// Now link the vertices and edges together
		for ( edge in graph.edges ) {
			
			var edgeLabel = edge.htmlNode.getAttribute("data-label") ;
			if ( edgeLabel == null ) {
				//print("Edge '" + edge.id + "' is missing its 'data-label' attribute." ) ;
				graphBuilt = false ; }
			else {
				edge.label = edgeLabel ; }
			
			var functionName = edge.htmlNode.getAttribute("data-function") ;
			if ( functionName == null ) {
				//print("Edge '" + edge.id + "' is missing its 'data-function' attribute." ) ;
				graphBuilt = false ; }
			else { 
				edge.functionName = functionName ;
			}
				
			var sourceId = edge.htmlNode.getAttribute("data-source") ;
			if ( sourceId == null ) {
				//print("Edge '" + edge.id + "' is missing its 'data-source' attribute." ) ;
				 graphBuilt = false ; }
			else if( ! graph.vertices.exists( sourceId ) ) {
				//print("Edge '" + edge.id + "' has 'data-source' attribute of '" + sourceId + "'. But there is no such node.");
				graphBuilt = false ; }
			else {
				edge.source = graph.vertices.get( sourceId ) ;
				if( edgeLabel != null )
					graph.vertices.get( sourceId ).outGoingEdges.set( edge.id, edge ) ;  }
			
			var targetId = edge.htmlNode.getAttribute("data-target") ;
			if ( targetId == null ) {
				print("Edge '" + edge.id + "' is missing its 'data-target' attribute." ) ;
				 graphBuilt = false ; }
			else if( ! graph.vertices.exists( targetId ) ) {
				print("Edge '" + edge.id + "' has 'data-target' attribute of '" + targetId + "'. But there is no such node.");
				graphBuilt = false ; }
			else {
				edge.target = graph.vertices.get( targetId ) ; }
		}
		if ( graph.startVertex == null ) { graphBuilt = false ; }
		if ( graphBuilt )  return graph ; else return null ;
		
	}
	static function onLoad() {
		print( "onLoad starts") ;
		var graph = buildGraph( Browser.document ) ;
		if ( graph == null ) { print("Failed to build graph"); return ; }
		var applet : TMExternalCommandInterface ;
		untyped { applet = Browser.document.applets["tm_applet"]  ;  }
		if ( applet == null ) {
			print( "Applet not found" ) ;
			return ; }
		print( "Applet is " + applet ) ;
		tmProxy = new TMProxy( applet ) ;
		var temp : EdgeFunc = new EdgeFunc();
		temp.edgeId = graph.startFunctionName;
		edgeFunctionStack.push(temp);
		print("At the beginning of executeFunction, startFunctionName: " + graph.startFunctionName);
		executeFunction( graph.startFunctionName ) ;
		print( "Back from execute function" );
		edgeFunctionStack.first().type = tmProxy.count;
		tmProxy.count = 0;
		switchToVertex( graph.startVertex ) ;
		print( "onLoad ends" ) ;
	}
	
	static function executeFunction( name : String ) {
		print("At the beginning of executeFunction");
		var klass = Type.resolveClass("EdgeFunctions") ;
		if ( klass == null ) {
			print("No class 'EdgeFunctions' found.") ; }
		else {
			var fun = Reflect.field( klass, name ) ;
			if ( fun == null ) {
				print( "Function named '" +name + "' not found." ) ; 
				}
			else {
				Reflect.callMethod( klass, fun, [tmProxy] ) ; } }
		print("At the end of executeFunction");
	}
	
	static function switchToVertex( vertex : TutorialVertex ) {
		print("Switching to vertex " + vertex.id);
		var instructionNode = Browser.document.getElementById("instructions") ;
		while ( instructionNode.firstChild != null )
			instructionNode.removeChild( instructionNode.firstChild ) ;
		instructionNode.insertBefore( vertex.htmlNode, null ) ;
		
		var buttonsNode = Browser.document.getElementById("buttons") ;
		while ( buttonsNode.firstChild != null )
			buttonsNode.removeChild( buttonsNode.firstChild ) ;
			
		var pageNumberNode = Browser.document.getElementById("pageno") ;
		pageNumberNode.removeChild( pageNumberNode.firstChild ) ;
		var label = Browser.document.createTextNode("Page: " + vertex.pageno);
		//print("Page: " + vertex.pageno);
		pageNumberNode.insertBefore(label, null);
		
		if (!vertexStack.isEmpty())
		{
			var undoList = edgeFunctionStack.first();
			
			
			//BACK TO FIRST
			var labelNode = Browser.document.createTextNode("back to first") ;
			var button = Browser.document.createElement("button") ;
			button.insertBefore( labelNode, null ) ;
			button.onclick = function(event : Event ) {
				var temp = vertexStack.last();
				while (!vertexStack.isEmpty())
				{
					temp = vertexStack.pop();
					edgeFunctionStack.pop();
				}
				print(" Executing:  " + edgeFunctionStack.first().edgeId + " and vertex: " + temp.id);
				tmProxy.replaying = true;
				executeFunction(edgeFunctionStack.first().edgeId);
				edgeFunctionStack.first().type = tmProxy.count;
				tmProxy.count = 0;
				tmProxy.replaying = false;
				switchToVertex(temp);
			}
			buttonsNode.insertBefore( button, null ) ;
			
			
			//BACK
			labelNode = Browser.document.createTextNode("back") ;
			button = Browser.document.createElement("button") ;
			button.insertBefore( labelNode, null ) ;
			button.onclick = function(event : Event ) {
				if (undoList.type < 0)
				{
					//print("Count: " + undoList.type);
					print(vertexStack.first().id + "popped from stack\n Going to switch to :" + vertexStack.first().id);
					var temp = vertexStack.pop(); 
					edgeFunctionStack.pop();
					var forward_stack: List<String> = new List<String>();
					for (edge in edgeFunctionStack)
					{
						forward_stack.push(edge.edgeId);
					}
					tmProxy.replaying = true;
					for (id in forward_stack)
					{
						//print("Executing: " + id);
						executeFunction(id);
					}
					tmProxy.replaying = false;
					switchToVertex(temp);
				}
				else
				{
					var count = undoList.type;
					//print("Count: " + count);
					tmProxy.replaying = true;
					while(count--!=0)
					{
						tmProxy.goBack();
					}
					tmProxy.replaying = false;
					//print(vertexStack.first().id + " popped from stack \n" +  edgeFunctionStack.first().edgeId + " popped from stack\n" +  " Going to switch to : " + vertexStack.first().id);
					edgeFunctionStack.pop();
					var temp = vertexStack.pop();
					switchToVertex(temp);
				}
			}
			
			buttonsNode.insertBefore( button, null ) ;
			
			
		}
		else
		{
			var labelNode = Browser.document.createTextNode("back to first") ;
			var button = Browser.document.createElement("button") ;
			button.insertBefore( labelNode, null ) ;
			button.setAttribute('disabled','true');
			buttonsNode.insertBefore( button, null ) ;
			labelNode = Browser.document.createTextNode("back") ;
			button = Browser.document.createElement("button") ;
			button.insertBefore( labelNode, null ) ;
			button.setAttribute('disabled','true');
			buttonsNode.insertBefore( button, null ) ;
		}
		//////////////////////////////////////////////////////////////////////////	
		for ( id in vertex.outGoingEdges.keys()  ) {
			var edge = vertex.outGoingEdges.get( id ) ;
			var temp : EdgeFunc = new EdgeFunc();		
			temp.edgeId = edge.functionName;
			var target = edge.target ;
			var functionName = edge.functionName ;
			var labelNode = Browser.document.createTextNode(edge.label) ;
			var button = Browser.document.createElement("button") ;
			button.insertBefore( labelNode, null ) ;
			button.onclick = function(event : Event ) {
				//print("Pushing edge:  " + temp.edgeId + ", executing: " + functionName + ", pushing vertex: " + vertex.id);
				edgeFunctionStack.push(temp);
				executeFunction( functionName ) ;
				//print("tmProxy.count" + tmProxy.count);
				edgeFunctionStack.first().type = tmProxy.count;
				tmProxy.count = 0;
				vertexStack.push(vertex); 
				//print(vertex.id + "pushed  on stack\nAbout to switch to target: "+ target.id);
				switchToVertex( target ) ; }
			buttonsNode.insertBefore( button, null ) ; 
		}
	}
}
	
