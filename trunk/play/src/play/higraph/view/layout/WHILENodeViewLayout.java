/**
 * WHILENodeViewLayout.java - play.higraph.view.layout - PLAY
 * 
 * Created on 2012-03-12 by Kai Zhu
 */
package play.higraph.view.layout;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import play.higraph.view.PLAYHigraphView;
import play.higraph.view.PLAYNodeView;
import play.higraph.view.WHILENodeView;

;

/**
 * @author Kai Zhu
 * 
 */
public class WHILENodeViewLayout extends PLAYLayoutManager {

    /**
     * @see play.higraph.view.layout.PLAYLayoutManager#layoutLocal(play.higraph.view.PLAYHigraphView)
     */
    @Override
    public void layoutLocal(PLAYHigraphView higraphView) {
    }

    /**
     * @see play.higraph.view.layout.PLAYLayoutManager#layoutLocal(play.higraph.view.PLAYNodeView)
     */
    @Override
    public void layoutLocal(PLAYNodeView nodeView) {
	if (nodeView instanceof WHILENodeView) {
	    WHILENodeView whileNodeView = (WHILENodeView) nodeView;
	    Rectangle2D rectangle = new Rectangle2D.Double(0, 0, 20, 20);
	    if (whileNodeView.getNumChildren() == 2) {
		double x = 30;
		double y = 0;
		for (int i = 0; i < 2; i++) {
		    PLAYNodeView childNodeView = (PLAYNodeView) whileNodeView
			    .getChild(i);
		    childNodeView.doLayout();
		    childNodeView.placeNextHierarchy(x, y);
		    Rectangle2D childNextExtent = childNodeView.getNextExtent();
		    y += 10 + childNextExtent.getHeight();
		    Rectangle2D.union(rectangle, childNextExtent, rectangle);
		    childNodeView.getBranch().setVisibility(false);
		}
		rectangle.add(new Point2D.Double(rectangle.getMaxX() + 10,
			rectangle.getMaxY() + 10));
		whileNodeView.setNextShape(rectangle);
		whileNodeView.translateNextHierarchy(0, 0);
	    } else {
		whileNodeView.placeNext(0, 0);
	    }
	}
    }

}
