package tm.gwt.display;

import tm.gwt.jsInterface.MirrorState;
import tm.portableDisplays.ExpressionDisplayer;
import tm.portableDisplays.PortableContextInterface;

public class ExpressionDisplayCanvas extends ExpressionDisplayer {
	MirrorState evaluator;

	public ExpressionDisplayCanvas(PortableContextInterface context) {
		super(null, context);
	}

	public void updateExpDisplay(MirrorState e) {
		theExpression = e.getExpression();
	}
	
	public void setEvaluator(MirrorState e) {
		evaluator = e;
	}
	
	

	@Override
	public void refresh() {
		if (evaluator == null)
			theExpression = "";
		else
			theExpression = evaluator.getExpression();
	}
}