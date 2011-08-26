package tm.clc.ast;

import java.util.Vector ;

import tm.clc.analysis.* ;
import tm.clc.ast.*;
import tm.clc.datum.* ;
import tm.interfaces.SourceCoords;
import tm.cpp.analysis.* ;
import tm.cpp.ast.* ;
import tm.cpp.datum.* ;
import tm.virtualMachine.* ;

public class OpMemberCallTest extends Cpp_AbstractAstTest {

    public OpMemberCallTest() { this ("OpMemberCallTest"); }
    public OpMemberCallTest (String name) { super (name); print=true;}


    private ScopedName name_A, name_A_x, name_A_y ;
    private ScopedName name_B0, name_B0_x, name_B0_y ;
    private ScopedName name_B1, name_B1_x, name_B1_y ;
    private ScopedName name_C, name_C_x, name_C_y ;
    private TyClass class_A, class_B0, class_B1, class_C ;
    private TyRef tyRefClass_C ;
    private ScopedName name_q ;

    ObjectDatum theObject ;
    FunctionDefnCompiled defn ;
    ScopedName fred, i, m ;
    TypeNode tyInt ;
    TyAbstractRef tyRefToInt ;
    TypeNode fredsType ;
    ExpressionNode call ;

    protected void makeObject() {
        /* We emulate the following diamond structure
             class A { int x, y ; }
             class B0 : A { int x, y ; }
             class B1 : A { int x, y ; }
             class C : B0, B1 { int x, y ; }
        */
        tyInt = TyInt.get() ;

        // class A { int x, y ; }
        name_A = new Cpp_ScopedName("A") ;
        name_A.set_absolute() ;

        name_A_x = new Cpp_ScopedName( name_A ) ;
        name_A_x.append("x") ;

        name_A_y = new Cpp_ScopedName( name_A ) ;
        name_A_y.append("y") ;

        class_A = new TyClass("A", name_A, null ) ;
        class_A.addField(new VarNode(name_A_x, tyInt)) ;
        class_A.addField(new VarNode(name_A_y, tyInt)) ;
        class_A.setDefined() ;


        // class B0 : A { int x, y ; }
        name_B0 = new Cpp_ScopedName("B0") ;
        name_B0.set_absolute() ;

        ScopedName name_B0_x = new Cpp_ScopedName( name_B0 ) ;
        name_B0_x.append("x") ;

        name_B0_y = new Cpp_ScopedName( name_B0 ) ;
        name_B0_y.append("y") ;

        class_B0 = new TyClass("B0", name_B0, null) ;
        class_B0.addSuperClass(class_A) ;
        class_B0.addField(new VarNode(name_B0_x, tyInt)) ;
        class_B0.addField(new VarNode(name_B0_y, tyInt)) ;
        class_B0.setDefined() ;

        // class B1 : A { int x, y ; }
        name_B1 = new Cpp_ScopedName("B1") ;
        name_B1.set_absolute() ;

        name_B1_x = new Cpp_ScopedName( name_B1 ) ;
        name_B1_x.append("x") ;

        name_B1_y = new Cpp_ScopedName( name_B1 ) ;
        name_B1_y.append("y") ;

        class_B1 = new TyClass("B1", name_B1, null) ;
        class_B1.addSuperClass(class_A) ;
        class_B1.addField(new VarNode(name_B1_x, tyInt)) ;
        class_B1.addField(new VarNode(name_B1_y, tyInt)) ;
        class_B1.setDefined() ;


        // class C : B0, B1 { int x, y ; }
        name_C = new Cpp_ScopedName("C") ;
        name_C.set_absolute() ;

        name_C_x = new Cpp_ScopedName( name_C ) ;
        name_C_x.append("x") ;

        name_C_y = new Cpp_ScopedName( name_C ) ;
        name_C_y.append("y") ;

        class_C = new TyClass("C", name_C, null) ;
        class_C.addSuperClass(class_B0) ;
        class_C.addSuperClass(class_B1) ;
        class_C.addField(new VarNode(name_C_x, tyInt)) ;
        class_C.addField(new VarNode(name_C_y, tyInt)) ;
        tyRefClass_C = new TyRef( class_C ) ;
        class_C.setDefined() ;

        // Make a datum of type C
        theObject = (ObjectDatum)
            class_C.makeDatum(vms, vms.getStore().getStack(), "q");

        name_q = new Cpp_ScopedName("q") ;
        symtab.newVar( name_q, "q", theObject ) ; }

    protected void makeDefn() {
        /* Definition of:
             int fred( int i ) {
                return i-x ; }
           where m is a member of the recipient object
        */
        // Some types
        tyInt = TyInt.get() ;
        tyRefToInt = new TyRef( tyInt ) ;
        // Some names
        fred = new Cpp_ScopedName("fred");
        i = new Cpp_ScopedName("fred") ; i.set_absolute() ; i.append("i") ;
        // Some var nodes
        VarNode i_vn = new VarNode( i, tyInt ) ;
        // Fred's type.
        Vector params = new Vector() ;
        params.addElement( tyInt ) ;
        fredsType = new TyFun( params, false ) ;
        fredsType.addToEnd( tyInt ) ;
        // DECL for i
        StatDecl i_decl = new StatDecl(SourceCoords.UNKNOWN, 0, i_vn ) ;
        // Init of i
        ExpressionNode i_assign =
            new OpAssign( tyRefToInt,
                           "=",
                           new ExpId( tyRefToInt,
                                       "i",
                                       i ),
                           new ExpArgument( tyInt,
                                             0 ) ) ;

        StatDo i_init = new StatDo(SourceCoords.UNKNOWN, 1, i_assign ) ;

        // result <- i - x
        int[] p = new int[0];
        ExpressionNode result_assign =
            new OpAssign( tyRefToInt,
                           "=",
                           new ExpResult( tyRefToInt ),
                           new OpInt( tyInt,
                                       Arithmetic.SUBTRACT,
                                       "+",
                                       new ExpFetch( tyInt,
                                                      new ExpId( tyRefToInt,
                                                                 "i",
                                                                 i ) ),
                                       new ExpFetch( tyInt,
                                                      new ExpThisMember( tyRefToInt,
                                                                 "x",
                                                                 p,
                                                                 name_C_x ) ) ) ) ;
       StatDo result = new StatDo( SourceCoords.UNKNOWN, 2, result_assign ) ;
       // RETURN
       StatReturn rtn = new StatReturn(SourceCoords.UNKNOWN, 2 ) ;
       // Put the body together.
       i_decl.next().set( i_init ) ;
       i_init.next().set( result ) ;
       result.next().set( rtn ) ;
       // Build the definition
       defn = new FunctionDefnCompiled( OverloadResolver.munge (fred, fredsType) ) ;
       defn.getBodyLink().set( i_decl ) ;

       // Put it in the symbol table
       this.symtab.addFunctionDefinition( defn ) ;
    }

    protected void makeCall() {
        // q.fred( 42 )
        NodeList args = new NodeList() ;
        args.addLastChild( new ConstInt(tyInt, "42", 42 ) ) ;
        Object key = defn.getKey() ;
        int[] p = new int[0] ;
        ExpressionNode recipExp = new ExpId( tyRefClass_C, "q", name_q ) ;
        call = new OpMemberCall( tyInt, "fred", ".", false, false, key, false, recipExp, p, args ) ; }

    public void testFunctionCall() {
        makeObject() ;
        makeDefn() ;
        makeCall() ;

        // Set the x field to 39
        AbstractIntDatum xDatum = (AbstractIntDatum) theObject.getFieldByName( name_C_x ) ;
        xDatum.putValue( 39 ) ;

        ExpressionEvaluation ee = new ExpressionEvaluation( this.vms, call ) ;
        this.vms.push( ee ) ;

        advance( 1000 ) ;

        Object result0 = ee.at( call ) ;
        assertTrue( result0 instanceof AbstractIntDatum ) ;
        AbstractIntDatum result1 = (AbstractIntDatum) result0 ;
        long v = result1.getValue() ;
        assertTrue( v == 3 ) ;
    }
}