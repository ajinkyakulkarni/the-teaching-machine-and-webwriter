package tm.gwt.state;

import java.util.HashMap ;

import tm.interfaces.RegionInterface ;
import tm.interfaces.StoreInterface ;
import tm.virtualMachine.MemRegion ;

public class MirrorStore implements StoreInterface {
    private static final long serialVersionUID = -833023456696499362L ;
    private HashMap<Integer,MirrorDatum> map = new HashMap<Integer,MirrorDatum>() ;
    MirrorDatum.MirrorRegion heapRegion ;
    MirrorDatum.MirrorRegion scratchRegion ;
    MirrorDatum.MirrorRegion stackRegion ;
    MirrorDatum.MirrorRegion staticRegion ;
    
    public MirrorStore( ) {
        this.heapRegion = new MirrorDatum.MirrorRegion(this) ;
        this.scratchRegion = new MirrorDatum.MirrorRegion(this) ;
        this.stackRegion = new MirrorDatum.MirrorRegion(this) ;
        this.staticRegion = new MirrorDatum.MirrorRegion(this) ;
    }
    
    void update( StoreInterface store) {
        com.google.gwt.core.client.GWT.log(">>> MirrorStore.update") ;
        this.heapRegion.update( store.getHeap(), this ) ;
        this.scratchRegion.update( store.getScratch(), this ) ;
        this.stackRegion.update( store.getStack(), this ) ;
        this.heapRegion.update( store.getHeap(), this ) ;
        com.google.gwt.core.client.GWT.log("<<< MirrorStore.update") ;
    }
    
    MirrorDatum get( int serialNumber ) { return map.get( serialNumber ) ; }
    
    void put( MirrorDatum d ) {
        if( d.store != this ) { throw new AssertionError() ; }
        map.put( d.getSerialNumber(), d ) ; }

    @Override
    public MirrorDatum.MirrorRegion getStack() {
        return this.stackRegion ;
    }

    @Override
    public MirrorDatum.MirrorRegion getHeap() {
        return this.heapRegion ;
    }

    @Override
    public MirrorDatum.MirrorRegion getStatic() {
        return this.staticRegion  ;
    }

    @Override
    public MirrorDatum.MirrorRegion getScratch() {
        return this.scratchRegion ;
    }
}