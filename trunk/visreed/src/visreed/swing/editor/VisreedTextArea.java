/**
 * VisreedTextArea.java
 * 
 * @date: 2011-6-15
 * @author: Xiaoyu Guo
 * This file is part of the Teaching Machine project.
 */
package visreed.swing.editor;

import java.awt.HeadlessException;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.text.EditorKit;

import visreed.extension.regex.model.RegexNode;
import visreed.extension.regex.parser.ParseException;
import visreed.model.VisreedNode;
import visreed.model.VisreedSubgraph;
import visreed.model.VisreedWholeGraph;

/**
 * VisreedTextArea is designed as a rich-text viewer / editor of regular expressions. 
 * @author Xiaoyu Guo
 */
public class VisreedTextArea extends javax.swing.JEditorPane{

    /** defines the minimum visible font size */
    private static final int MIN_VISIBLE_FONT_SIZE = 12;

    private static final long serialVersionUID = 6615489588445662558L;
    
    protected VisreedSubgraph graph;
    protected VisreedWholeGraph wholeGraph;
    
    /**
     * Gets the {@link visreed.model.VisreedSubgraph} attached to the text area.
     * @return
     */
    public VisreedSubgraph getHigraph(){
        return this.graph;
    }
    
    /**
     * Sets the {@link visreed.model.VisreedSubgraph} attached to the text area.
     * @param graph
     */
    public void setSubHigraph(VisreedSubgraph graph){
        if(this.graph != null){
            // detach any events from previous graph
        }
        this.graph = graph;
        this.wholeGraph = graph.getWholeGraph();
        // attach some events to the new graph
        
        this.refreshFromModel();
    }

    /**
     * Refresh text from the (updated) model 
     */
    public void refreshFromModel() {
        if(this.graph == null || graph.getTops().size() == 0){
            this.setText("");
            return;
        }
        List<VisreedNode> tops = this.graph.getTops();
        VisreedNode top = tops.get(0);
        if(top.getPayload() == null){
            this.setText("");
            return;
        }
        
        String expression = top.getPayload().format(top);
        
        this.setText(expression);
        
        this.refreshStyle();
    }
    
    /**
     * Refresh the text highlight
     */
    protected void refreshStyle() {
        // zoom characters
        java.awt.Font f = this.getFont();
        
        java.awt.Graphics g = this.getGraphics();
        
        Rectangle2D bounds = g.getFontMetrics(f).getStringBounds(
            this.getText(), 
            g
        );
        
        double factor = this.getWidth() / bounds.getWidth();
        factor = Math.min(
            factor, 
            this.getHeight() / bounds.getHeight()
        );
        
        float newSize = (float) (f.getSize2D() * factor);
        
        if(newSize >= MIN_VISIBLE_FONT_SIZE){
            f = f.deriveFont(newSize);
        }
        this.setFont(f);
        
        this.repaint();
    }
    
    /**
     * Try to parse the regular expression
     * @return {@value true} if the parse succeeded, {@value false} otherwise
     */
    public boolean tryParseText(){
        try {
            VisreedNode root = RegexNode.construct(wholeGraph, null, this.getText());
            if(root == null){
                return false;
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(
                null,
                e,
                "Parse error",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
        return true;
    }

    /**
     * Refresh model from the (updated) text
     */
    public void refreshFromText(){
        // update the model from this.getText()
        VisreedNode newTop;
        try {
            newTop = RegexNode.construct(this.wholeGraph, null, this.getText());
            if(newTop != null){
                List<VisreedNode> tops = this.graph.getTops();
                for(VisreedNode n : tops){
                    this.graph.removeTop(n);
                }
                this.graph.addTop(newTop);
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                e,
                "Parse error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /* constructors */

    /**
     * @throws HeadlessException
     */
    public VisreedTextArea() throws HeadlessException {
        super();
    }

    /**
     * @param text
     * @throws HeadlessException
     * @throws IOException 
     */
    public VisreedTextArea(String text)
            throws HeadlessException, IOException {
        super(text);
    }
    
    @Override
    protected EditorKit createDefaultEditorKit() {
        return new SourceCodeEditorKit();
    } 

}
