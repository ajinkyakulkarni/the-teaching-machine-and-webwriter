package tm.gwt.test;

import java.util.ArrayList ;
import java.util.Set ;
import java.util.TreeSet ;
import java.util.Vector ;

import tm.gwt.client.StateCommander ;
import tm.gwt.shared.state.MirrorCodeLine ;
import tm.gwt.shared.state.MirrorCoords ;
import tm.gwt.shared.state.MirrorDatum ;
import tm.gwt.shared.state.MirrorMarkUp ;
import tm.gwt.shared.state.MirrorState ;
import tm.gwt.shared.state.MirrorStore ;
import tm.gwt.shared.state.MirrorTMFile ;
import tm.gwt.shared.state.MirrorTagSet ;
import tm.interfaces.CodeLine ;
import tm.interfaces.CodeLineI ;
import tm.interfaces.MarkUp ;
import tm.interfaces.MarkUpI;
import tm.interfaces.SourceCoordsI ;
import tm.interfaces.StateInterface ;
import tm.interfaces.TMFileI ;
import tm.interfaces.TagSet ;
import tm.interfaces.TagSetInterface ;

public class TestController implements StateCommander {
    final MirrorState state ;
    int count = 0 ;
    MirrorTMFile file = new MirrorTMFile( "fred.cpp" ) ;
    ArrayList<MirrorCodeLine> lines = new ArrayList<MirrorCodeLine>() ;
    ArrayList<MirrorCoords> foci = new ArrayList<MirrorCoords>() ;
    {
        String b ;
        MirrorMarkUp[] markup = new MirrorMarkUp[0] ;
        TreeSet<MirrorTagSet> tagSets = new TreeSet<MirrorTagSet>()  ;
        

        MirrorCoords coords ;
        MirrorCodeLine line ;
        
        int lineNumber = 1 ;

        b = "void main( ) {" ;
        coords = new MirrorCoords(file, lineNumber)  ;
        line = new MirrorCodeLine(b, markup, coords, tagSets ) ;
        lines.add(  line  ) ;
        foci.add( coords ) ;
        ++lineNumber ;

        b = "    int first, second;" ;
        coords = new MirrorCoords(file, lineNumber)  ;
        line = new MirrorCodeLine(b, markup, coords, tagSets ) ;
        lines.add(  line  ) ;
        foci.add( coords ) ;
        ++lineNumber ;

        b =  "    cout << \"Input the first number: \";" ;
        coords = new MirrorCoords(file, lineNumber)  ;
        line = new MirrorCodeLine(b, markup, coords, tagSets ) ;
        lines.add(  line  ) ;
        foci.add( coords ) ;
        ++lineNumber ;

        b =  "     cin >> first;" ;
        coords = new MirrorCoords(file, lineNumber)  ;
        line = new MirrorCodeLine(b, markup, coords, tagSets ) ;
        lines.add(  line  ) ;
        foci.add( coords ) ;
        ++lineNumber ;

        b =  "}" ;
        coords = new MirrorCoords(file, lineNumber)  ;
        line = new MirrorCodeLine(b, markup, coords, tagSets ) ;
        lines.add(  line  ) ;
        foci.add( coords ) ;
        ++lineNumber ;
        
        for( ; lineNumber < 52 ; ) {
            coords = new MirrorCoords(file, lineNumber)  ;
            b =  "// Line " + lineNumber ;
            line = new MirrorCodeLine(b, markup, coords, tagSets ) ;
            lines.add(  line  ) ;
            ++lineNumber ;
        }
    }

    public TestController(MirrorState state) {
        this.state = state ;
        state.getStore().update( makeStore0() ) ;
    }
    
    void next() {
        switch( count ) {
        case 0 : {
            /*DBG*/ {
                int k = this.state.getStackRegion().getNumChildren() ;
                com.google.gwt.core.client.GWT.log("Stack datums:"+k ) ;
            }
            state.setExpression( StateInterface.EXP_START_SELECTED+ "tempF" +StateInterface.EXP_END+ " = (tempC * 5 / 9) + 32" );
            state.putSelectedCodeLines( file, lines );
            state.setCodeFocus( foci.get(  count % foci.size() ) ) ;
            state.getStore().update( makeStore1() ) ;
            for( int i = 0 ; i < 5 ; ++i)  state.addConsoleLine( "Console line " + i );
            state.addConsoleLine( "Hello world"  );
            count = 1 ;
        } break ;
        case 1 : {
            state.setExpression( StateInterface.EXP_START_LVALUE+ "tempF" +StateInterface.EXP_END+ " = (" +StateInterface.EXP_START_SELECTED+ "tempC" +StateInterface.EXP_END+ " * 5 / 9) + 32" );
            state.setCodeFocus( foci.get(  count % foci.size() ) );
            state.getStore().update( makeStore2() ) ;
            state.addConsoleLine( "What is your name?"  );
            count = 2 ;
        } break ;
        case 2 : {
            state.setExpression( StateInterface.EXP_START_LVALUE+ "tempF" +StateInterface.EXP_END+ " = (" +StateInterface.EXP_START_SELECTED+ StateInterface.EXP_START_LVALUE+ "tempC" +StateInterface.EXP_END+ StateInterface.EXP_END+ " * 5 / 9) + 32" );
            state.setCodeFocus( foci.get(  count % foci.size() ) );
            state.getStore().update( makeStore3() ) ;
            state.addConsoleLine( StateInterface.INPUT_MARK + "Zhaoyan" + StateInterface.NORMAL_MARK  );
            count = 3 ;
        } break ;
        case 3 : {
            state.setExpression( StateInterface.EXP_START_LVALUE+ "tempF" +StateInterface.EXP_END+ " = (" +StateInterface.EXP_START_VALUE+ "10" +StateInterface.EXP_END + " * " +StateInterface.EXP_START_SELECTED+ "5" +StateInterface.EXP_END+ " / 9) + 32" );
            state.setCodeFocus( foci.get(  count % foci.size() ) );
            state.getStore().update( makeStore4() ) ;
            state.addConsoleLine(  "Hello Zhaoyan");
            count = 4 ;
        } break ;
        case 4 : {
            state.setExpression( StateInterface.EXP_START_LVALUE+ "tempF" +StateInterface.EXP_END+ " = (" +StateInterface.EXP_START_SELECTED+ StateInterface.EXP_START_VALUE+ "10" +StateInterface.EXP_END+ " * " +StateInterface.EXP_START_VALUE+ "5.0" +StateInterface.EXP_END+ StateInterface.EXP_END+ " / 9) + 32" );
            state.setCodeFocus( foci.get(  count % foci.size() ) );
            state.getStore().update( makeStore5() ) ;
            count = 5 ;
        } break ;
        case 5 : {
            state.setExpression( "" +StateInterface.EXP_START_SELECTED+ "" +StateInterface.EXP_START_LVALUE+ "tempF" +StateInterface.EXP_END+ " = " +StateInterface.EXP_START_VALUE+ "50.0" +StateInterface.EXP_END+ StateInterface.EXP_END );
            state.setCodeFocus( foci.get(  count % foci.size() ) );
            state.getStore().update( makeStore6() ) ;
            state.addConsoleLine(  "Testing console 1");
            count = 6 ;
        } break ;
        case 6 : {
            state.setExpression( StateInterface.EXP_START_VALUE+ "50.0" +StateInterface.EXP_END );
            state.setCodeFocus( foci.get(  count % foci.size() ) );
            state.getStore().update( makeStore0() ) ;
            state.addConsoleLine(  "Testing console 2");
            count = 7 ;
        } break ;
        case 7 : {
            state.setExpression( "" );
            state.setCodeFocus( foci.get(  count % foci.size() ) );
            state.addConsoleLine(  "Testing console 3");
            count = 0 ;
        } break ;
        }
    }

    @Override
    public void goForward() {
        next() ;
    }

    @Override
    public void goBack() {
        int temp = ((count + 7) - 1) % 7;
        while( count != temp ) next() ;
    }

    @Override
    public void intoExp() {
        next() ;
    }

    @Override
    public void overAll() {
        next() ;
    }

    @Override
    public void intoSub() {
        next() ;
    }
    
    MirrorStore makeStore0() {
        // Make an empty store.
        com.google.gwt.core.client.GWT.log(">>> makeStore0") ;
        MirrorStore store0 = new MirrorStore() ;
        
        com.google.gwt.core.client.GWT.log("<<< makeStore0") ;
        return store0 ;
    }
    
    MirrorStore makeStore1() {
        com.google.gwt.core.client.GWT.log(">>> makeStore1") ;
        MirrorStore store1 = new MirrorStore() ;
        MirrorDatum.MirrorRegion stack = store1.getStack() ;
        
        MirrorDatum d0 = new MirrorDatum( 100, null, "bob", "int", "42", new byte[]{0, 0, 0, 42}, 0, 0, 13, store1 ) ;
        stack.addChild( "Bob", d0, 0 );
        
        MirrorDatum d1 = new MirrorDatum( 100, null, "fred", "short", "137", new byte[]{0, 128-137}, 1, 1, 14, store1 ) ;
        stack.addChild( "Fred", d1, 1 );
        
        
        com.google.gwt.core.client.GWT.log("<<< makeStore1") ;
        return store1 ;
    }
    
    MirrorStore makeStore2() {
        // Make a store with 2 variables on the stack.
        MirrorStore store2 =  makeStore1() ;
        
        // Add structure with 2 int fields.
        MirrorDatum d3 = new MirrorDatum( 100, null, "s", "struct", "{...}", new byte[]{0, 0, 0, 20, 0, 0, 0, 21}, 0, 2, 14, store2 ) ;
            MirrorDatum d3a = new MirrorDatum( 100, d3, "a", "int", "20", new byte[]{0, 0, 0, 20}, 0, 0, 15, store2 ) ;
            d3.addChild( ".a", d3a, 0 );
            MirrorDatum d3b = new MirrorDatum( 100, d3, "b", "int", "21", new byte[]{0, 0, 0, 21}, 0, 1, 16, store2 ) ;
            d3.addChild( ".b", d3b, 1 );
            

        MirrorDatum.MirrorRegion stack = store2.getStack() ;
        stack.addChild( "s", d3, 2 );
        return store2 ;
    }
    
    MirrorStore makeStore3() {
        // Make a store similar to store 1 but with bob and fred having different values ;
        com.google.gwt.core.client.GWT.log(">>> makeStore3") ;
        MirrorStore store3 = new MirrorStore() ;
        MirrorDatum.MirrorRegion stack = store3.getStack() ;
        
        MirrorDatum d0 = new MirrorDatum( 100, null, "bob", "int", "0", new byte[]{0, 0, 0, 0}, 0, 0, 13, store3 ) ;
        stack.addChild( "Bob", d0, 0 );
        
        MirrorDatum d1 = new MirrorDatum( 100, null, "fred", "short", "57", new byte[]{0, 57}, 1, 1, 14, store3 ) ;
        stack.addChild( "Fred", d1, 1 );
        
        
        com.google.gwt.core.client.GWT.log("<<< makeStore3") ;
        return store3 ;
    }
    
    MirrorStore makeStore4() {
        return makeStore1() ;
    }
    
    MirrorStore makeStore5() {
        return makeStore1() ;
    }
    
    MirrorStore makeStore6() {
        return makeStore1() ;
    }

    @Override
    public void go(String commandString) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void toCursor(String fileName, int cursor) {
        // TODO Auto-generated method stub
        
    }
    
    
}
