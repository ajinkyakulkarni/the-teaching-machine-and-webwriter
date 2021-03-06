package visreed.model.payload;

import higraph.view.HigraphView;
import tm.backtrack.BTTimeManager;
import visreed.model.VisreedEdge;
import visreed.model.VisreedEdgeLabel;
import visreed.model.VisreedHigraph;
import visreed.model.VisreedNode;
import visreed.model.VisreedPayload;
import visreed.model.VisreedSubgraph;
import visreed.model.VisreedTag;
import visreed.model.VisreedWholeGraph;
import visreed.view.TerminalNodeView;
import visreed.view.VisreedNodeView;

public class TerminalPayload extends VisreedPayload {
	protected String terminal;
	public String getTerminal(){
		return this.terminal;
	}
	
	public void setTerminal(String terminal){
		boolean changed = (this.terminal != terminal || !this.terminal.equals(terminal));
		this.terminal = terminal;
		if(changed && this.getNode() != null){
			this.getNode().notifyObservers();
		}
	}

    public TerminalPayload(VisreedTag tag){
        super(tag);
        this.setTerminal("");
    }
	public TerminalPayload(VisreedTag tag, String terminal) {
		super(tag);
		this.setTerminal(terminal);
	}
	
	public TerminalPayload(VisreedTag tag, char terminal){
		super(tag);
		this.setTerminal(terminal + "");
	}

	public String getEscapedTerminal(){
		return this.terminal.replaceAll("([\\(\\)\\.\\+\\*\\?])", "\\\\$1");
	}
	
    /* (non-Javadoc)
     * @see higraph.model.interfaces.Payload#copy()
     */
    @Override
    public TerminalPayload copy() {
        return new TerminalPayload(this.tag, this.terminal);
    }

	/* (non-Javadoc)
	 * @see visreed.model.VisreedPayload#format(visreed.model.RegexNode)
	 */
	@Override
	public String format(VisreedNode currentNode) {
		// no child for terminalNode
		return getEscapedTerminal();
	}
    
	/* (non-Javadoc)
	 * @see visreed.model.VisreedPayload#getDescription()
	 */
	@Override
	public String getDescription(){
	    return "\"" + this.terminal + "\"";
	}
	
    /* (non-Javadoc)
     * @see visreed.model.VisreedPayload#constructView(higraph.view.HigraphView, visreed.model.RegexNode, java.awt.Color, java.awt.Color, java.awt.Stroke, java.awt.geom.RectangularShape, boolean)
     */
    @Override
    public VisreedNodeView constructView(
        HigraphView<VisreedPayload, VisreedEdgeLabel, VisreedHigraph, VisreedWholeGraph, VisreedSubgraph, VisreedNode, VisreedEdge> sgv,
        VisreedNode node, 
        BTTimeManager timeman
    ) {
        return new TerminalNodeView(sgv, node, timeman);
    }
    
    /* (non-Javadoc)
     * @see visreed.model.payload.VisreedPayload#dump(java.lang.StringBuffer, int)
     */
    @Override
    public StringBuffer dump(StringBuffer sb, int indentLevel) {
    	sb = super.dump(sb, indentLevel);
    	sb.append("\"");
    	sb.append(this.terminal);
    	sb.append("\"");
    	return sb;
    }
}
