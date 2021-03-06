/**
 * SEQNodeView.java - play.higraph.view - PLAY
 * 
 * Created on 2012-03-05 by Kai Zhu
 */
package play.higraph.view;

import higraph.view.HigraphView;
import higraph.view.ZoneView;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import play.executor.Environment;
import play.higraph.model.PLAYEdge;
import play.higraph.model.PLAYEdgeLabel;
import play.higraph.model.PLAYHigraph;
import play.higraph.model.PLAYNode;
import play.higraph.model.PLAYPayload;
import play.higraph.model.PLAYSubgraph;
import play.higraph.model.PLAYTag;
import play.higraph.model.PLAYWholeGraph;
import play.higraph.swing.PLAYHigraphJComponent;
import tm.backtrack.BTTimeManager;

/**
 * @author Kai Zhu
 * 
 */
public class SEQNodeView extends PLAYNodeView {
	
	private String s;
	private Environment e;
	private PLAYNode n;
	private PLAYSubgraph sg;
	private PLAYHigraphJComponent hj;

	/**
	 * @param v
	 * @param node
	 * @param timeMan
	 */
	protected SEQNodeView(
			HigraphView<PLAYPayload, PLAYEdgeLabel, PLAYHigraph, PLAYWholeGraph, PLAYSubgraph, PLAYNode, PLAYEdge> v,
			PLAYNode node, BTTimeManager timeMan) {
		super(v, node, timeMan);
		super.setNodeSize(200, 200);
		super.setColor(Color.BLUE);
		super.setFillColor(null);
		//System.out.println("sequence node view");
	}

	/**
	 * @see higraph.view.NodeView#drawSelf(java.awt.Graphics2D)
	 */
	@Override
	protected void drawSelf(Graphics2D screen) {
		//System.out.println("drawself seq node");
		double x = super.getNextX();
		double y = super.getNextY();
		int number = this.getNumChildren();
		//System.out.println(number);

		super.drawSelf(screen);

		// draw an arrow
		//screen.drawString("\u21D2", (float) (x + 2), (float) (y + 10));

		for (int i = 0; i < number - 1; i++) {
			x = super.getChild(i).getNextShapeExtent().getCenterX();
			y = super.getChild(i).getNextShapeExtent().getMaxY()
					+ (super.getChild(i + 1).getNextShapeExtent().getMinY() - super
							.getChild(i).getNextShapeExtent().getMaxY()) / 2;
			screen.drawString("\u21D3", (float) x, (float) (y + 5));
		}
	}

	/**
	 * @see higraph.view.ComponentView#moveZone(higraph.view.ZoneView)
	 */
	@Override
	public void moveZone(
			ZoneView<PLAYPayload, PLAYEdgeLabel, PLAYHigraph, PLAYWholeGraph, PLAYSubgraph, PLAYNode, PLAYEdge> zone) {
		PLAYNodeView nodeView = (PLAYNodeView) zone.getAssociatedComponent();
		int number = super.getNumChildren();
		double x = 0;
		double y = 0;
		double width = 0;
		double height = 0;
		if (number > 0) {
			// have a child or some children
			int index = super.indexOfZones(zone);
			if (index == 0) {
				PLAYNodeView childNodeView = (PLAYNodeView) nodeView
						.getChild(0);
				x = childNodeView.getNextShapeExtent().getMinX();
				y = nodeView.getNextShapeExtent().getMinY();
				width = childNodeView.getNextShapeExtent().getWidth();
				height = childNodeView.getNextShapeExtent().getMinY() - y;
			} else if ((index != 0) && (index < number)) {
				PLAYNodeView leftChildNodeView = (PLAYNodeView) nodeView
						.getChild(index - 1);
				PLAYNodeView rightChildNodeView = (PLAYNodeView) nodeView
						.getChild(index);
				x = leftChildNodeView.getNextShapeExtent().getMinX();
				y = leftChildNodeView.getNextShapeExtent().getMaxY();
				width = rightChildNodeView.getNextShapeExtent().getWidth();
				height = rightChildNodeView.getNextShapeExtent().getMinY() - y;
			} else if (index == number) {
				PLAYNodeView childNodeView = (PLAYNodeView) nodeView
						.getChild(number - 1);
				x = childNodeView.getNextShapeExtent().getMinX();
				y = childNodeView.getNextShapeExtent().getMaxY();
				width = childNodeView.getNextShapeExtent().getWidth();
				height = nodeView.getNextShapeExtent().getMaxY() - y;
			}

		} else {
			// no children
			x = nodeView.getNextShapeExtent().getMinX()
					+ nodeView.getNextShapeExtent().getWidth() / 10;
			y = nodeView.getNextShapeExtent().getMinY()
					+ nodeView.getNextShapeExtent().getHeight() / 10;
			width = nodeView.getNextShapeExtent().getWidth() * 4 / 5;
			height = nodeView.getNextShapeExtent().getHeight() * 4 / 5;
		}
		zone.setNextShape(new Rectangle2D.Double(x, y, width, height));
		zone.placeNext(x, y);
		super.moveZone(zone);
	}
	
	 //added by ravneet
	public String execute(Environment env,PLAYNode node,PLAYSubgraph sgraph){
		e = env;
		s = null;
		sg = sgraph;
		n = node;
		
		highlight(n);
		
		System.out.println("inside seq execute");
		int mChildren = n.getNumberOfChildren();
		System.out.println("children = "+mChildren);
		
		for(int k=0;k<mChildren;k++){
			System.out.println(n.getChild(k).getTag());
			
			s= n.getChild(k).getView().execute(env, n.getChild(k), sg);
			
		}
		
		return s;
		
	}

}
