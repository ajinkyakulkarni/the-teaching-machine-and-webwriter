/*
	An applet to read a URL
 */
import java.applet.*;
import java.net.URL ;

import java.io.FileInputStream;
import java.io.InputStream ;

public class URL2String extends Applet
{
    // DATA
    private boolean success_flag ;
    private boolean verbose = false;

    // PUBLIC METHODS
	public URL2String() {
	    success_flag = false ;
	}

	public boolean success() { return success_flag ; } 

	public String read_relative_URL( String url_string ) 
	{
	    success_flag = false ;

	    try {
	    	URL base = getDocumentBase();
	    	if (verbose){
	    		System.out.println("documentBase is " + base);
	    		System.out.println("read relative " + url_string);
	    	}
	    	
	        /* This was an attempt to switch to relative file protocol on MAC
	         * to try to get around the failure of the Applet to read
	         * local files on a MAC. Didn't even work on a windows machine.
	         * (read_url was changed to readStream)
	    	InputStream stream;
	    	
	    	String protocol = base.getProtocol();
	    	if (verbose) System.out.println("protocol is " + protocol);
	        URL url = new URL( base, url_string ) ;
	        if (verbose) System.out.println("read url is " + url.toString());	        
	    	
	        
	        stream = (protocol.equalsIgnoreCase("file")) ?
	        		new FileInputStream("./" + url_string) :  
	        			url.openStream();
	        
	    	String result = readStream(stream);*/
	    	
	        URL url = new URL( base, url_string ) ;
	        if (verbose) System.out.println("read url is " + url.toString());
	        
	    	String result = read_url(url);


	        if (verbose) System.out.println("URL2String result is " + result);
	        return  result;}
        catch( Exception e ) {
            System.err.println( "URL2String: Exception when reading from "+url_string) ;
            System.err.println( "            Message is : "+e.getMessage() ) ;
            System.err.println( "            Stack trace is" ) ;
            e.printStackTrace() ;
            return "" ; }
    }
	
	public void setVerbose(boolean v){verbose = v;}

	public String read_absolute_URL( String url_string ) 
	{
	    success_flag = false ;

	    try {
	        URL url = new URL( url_string ) ;
	        return read_url( url ) ; }
        catch( Exception e ) {
            System.err.println( "URL2String: Exception when reading from "+url_string) ;
            System.err.println( "            Message is : "+e.getMessage() ) ;
            System.err.println( "            Stack trace is" ) ;
            e.printStackTrace() ;
            return "" ; }
    }



    // PRIVATE METHODS
   
	private String read_url( URL url ) 
	{
	    try {
	    	InputStream inStream = url.openStream();
	        byte[] byte_buff = new byte[256] ;
	        char[] char_buff = new char[256] ;
            StringBuffer str_buff = new StringBuffer() ;
            while(true) {
                int bytes_read = inStream.read( byte_buff ) ;
                if( bytes_read == -1 ) break ;
                for( int i=0 ; i < bytes_read ; ++i ) {
                    char_buff[i] = (char) byte_buff[i] ; }
                str_buff.append( char_buff, 0, bytes_read ) ; }
            success_flag = true ;
            return str_buff.toString() ; }
        catch( Exception e ) {
            System.err.println( "URL2String: Exception when reading from "+url.toString()) ;
            System.err.println( "            Message is : "+e.getMessage() ) ;
            System.err.println( "            Stack trace is" ) ;
            e.printStackTrace() ;
            return "" ; }
    }
	
	private String readFile( String relativePath ){
	    try {
	        FileInputStream stream = new FileInputStream(relativePath);
	        byte[] byte_buff = new byte[256] ;
	        char[] char_buff = new char[256] ;
            StringBuffer str_buff = new StringBuffer() ;
            while(true) {
                int bytes_read = stream.read( byte_buff ) ;
                if( bytes_read == -1 ) break ;
                for( int i=0 ; i < bytes_read ; ++i ) {
                    char_buff[i] = (char) byte_buff[i] ; }
                str_buff.append( char_buff, 0, bytes_read ) ; }
            success_flag = true ;
            return str_buff.toString() ; }
        catch( Exception e ) {
            System.err.println( "URL2String: Exception when reading from "+relativePath) ;
            System.err.println( "            Message is : "+e.getMessage() ) ;
            System.err.println( "            Stack trace is" ) ;
            e.printStackTrace() ;
            return "" ; }
	}


	public void init()
	{
		// This code is automatically generated by Visual Cafe when you add
		// components to the visual environment. It instantiates and initializes
		// the components. To modify the code, only use code syntax that matches
		// what Visual Cafe can generate, or Visual Cafe may be unable to back
		// parse your Java file into its visual environment.
		//{{INIT_CONTROLS
		setLayout(null);
		setSize(1,1);
		//}}
	}


	//{{DECLARE_CONTROLS
	//}}
}

