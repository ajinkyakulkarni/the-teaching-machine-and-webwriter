package telford.common;

abstract public class FontMetrics {
	
	public abstract int getHeight() ;
	
	public abstract int stringWidth( String str ) ;

	public abstract int getAscent();
	
	public abstract int getDescent(); 

}
