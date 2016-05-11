//     Copyright 1998--2010 Michael Bruce-Lockhart and Theodore S. Norvell
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
//
//     http://www.apache.org/licenses/LICENSE-2.0 
//
// Unless required by applicable law or agreed to in writing, 
// software distributed under the License is distributed on an 
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific language 
// governing permissions and limitations under the License.

package tm;
import java.applet.AppletStub;

import javax.swing.* ;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import tm.configuration.Configuration;
import tm.configuration.ConfigurationServer;
import tm.interfaces.CommandInterface;
import tm.interfaces.Configurable;
import tm.interfaces.ExternalCommandInterface;
import tm.interfaces.ImageSourceInterface;
import tm.interfaces.PlatformServicesInterface;
import tm.interfaces.SelectionInterface;
import tm.plugins.PlugInManager;
import tm.plugins.PlugInManagerDialog;
import tm.scripting.ScriptManager;
import tm.utilities.ApologyException;
import tm.virtualMachine.SelectionParser;


// TMMainFrame
// ------------
//   Contains the main program for the application.
//   Builds a frame containing a menu and a TMBigApplet.
/////////////////////////////////////////////////////////

public class TMMainFrame extends JFrame implements Configurable, ExternalCommandInterface 
{
    private JWindow splash ;
    private static final int SPLASH_NOT_SHOWING = 0; 
    private static final int SPLASH_SHOWING = 1; 
    private static final int SPLASH_SHOWN = 2; 
    private static int splashState = SPLASH_NOT_SHOWING ;
    private PlatformServicesInterface platform;

    private TMMainPanel tmMainPanel ;


//===================================================================================
// Constructor
//===================================================================================
    public TMMainFrame( ArgPackage argPackage, PlatformServicesInterface platform  ){

        super();
        this.platform = platform ;

        // Need a window listener.
            addWindowListener( new TMMainFrameWindowListener( ) ) ;

        // We'll use a simple border layout and add a menu at the top

        setBackground(Color.BLACK) ;
        getContentPane().setBackground(Color.YELLOW) ;
        tmMainPanel = new TMMainPanel( argPackage, platform );
        TMHeavyMenuBar menuBar = new TMHeavyMenuBar( tmMainPanel, platform, this );
        tmMainPanel.addMenuBar( menuBar ) ;
        

        getContentPane().add( tmMainPanel.getComponent(), BorderLayout.CENTER );

        pack();
        setSize(800,600);
        setLocation(0,0);
        setTitle( "The Teaching Machine 3" ) ;
        setVisible( true ) ;


        ConfigurationServer.createInstance(tmMainPanel, platform);
        tmMainPanel.start() ; // Forces read of configuration file.
        ConfigurationServer server = ConfigurationServer.getConfigurationServer();
        server.register(this,"mainFrame");

        splash = makeSplash( platform ) ;
        flashSplash() ;
    }
    
    public void dispose() {
        tmMainPanel.dispose() ;
        ConfigurationServer server = ConfigurationServer.getConfigurationServer();
        server.deregister(this) ;
        super.dispose();
    }

    void addApplicationOnlyMenuItems() {
        // For the moment at least all menu items appear in the applet.
    }
    
    
    // MAIN PROGRAM //
    //////////////////

    public static void main( String[] arg ) {
        
        final ArgPackage argPackage = ArgPackage.processArgs( arg ) ;
        if( argPackage == null ) {
            System.err.println( "Usage: java tm.TMBigApplet {arguments} [file]") ;
            System.err.println( "   Arguments:" ) ;
            System.err.println( "     -installDirectory directory | -id directory  The directory where the tm.jar file is." ) ;
            System.err.println( "     -initialConfiguration file | -ic file  The initial configuration file to use." ) ;
            return ; }
        
//        System.out.println("Initial config file: " + argPackage.initialConfiguration.toString());

        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
            	TMMainPanel.setLookAndFeel( null ) ;
            	
                PlatformServicesInterface platform = new ApplicationPlatform(
                		argPackage.installDirectory,
                        argPackage.installDirectory) ;
                TMMainFrame tmMainFrame = new TMMainFrame( argPackage, platform ) ;

                // Add extra capabilites for applications.
                tmMainFrame.addApplicationOnlyMenuItems() ;
                
                if( argPackage.fileToLoad != null ) {
                    String fileURL = argPackage.fileToLoad.toString(); ;
                    tmMainFrame.loadRemoteFile( fileURL );
                }
            } } ) ;
    }
    
    private JWindow makeSplash(ImageSourceInterface imageSource) {
        String[] messages = { "The Teaching Machine 3.", tmMainPanel.getVersionString(),
        		TMMainPanel.COPYRIGHT };

        // Create a splash window.
        JWindow splash = new JWindow(this);
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLUE);
        splash.add(panel, BorderLayout.CENTER);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        Image logoImage = imageSource.fetchImage("subWindowPkg/logo.gif");
        JLabel logoLabel = new JLabel(new ImageIcon(logoImage));
        panel.add(logoLabel);
        Font font = new Font("Dialog", Font.PLAIN, 20);
        for (String message : messages) {
            JLabel label = new JLabel(message);
            label.setForeground(Color.YELLOW);
            label.setFont(font);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(label);
        }
        panel.setBorder(BorderFactory.createRaisedBevelBorder());
        splash.pack();

        // Center the splash on the main frame.
        Rectangle frameBounds = getBounds();
        Rectangle splashBounds = splash.getBounds();
        splashBounds.x = frameBounds.x + frameBounds.width / 2
                - splashBounds.width / 2;
        splashBounds.y = frameBounds.y + frameBounds.height / 2
                - splashBounds.height / 2;
        splash.setBounds(splashBounds);

        return splash;
    }
    
    private void flashSplash() {
        if( splashState == SPLASH_NOT_SHOWING ) {
            splash.setVisible( true ) ;
            splashState = SPLASH_SHOWING ;
            
            (new Thread() {
                // @Override
                public void run() {
                    try {
                        Thread.sleep(2000) ;
                    } catch (InterruptedException e) { }
                    killSplash() ; }}
            ).start() ;  }
    }
    
    private void killSplash() {
        if( splashState == SPLASH_SHOWING ) {
            splash.setVisible( false ) ;
            splash.dispose() ;
            splash = null ;
            splashState = SPLASH_SHOWN ; }
    }
    
    void help() { tmMainPanel.help() ; }

    class TMMainFrameWindowListener extends WindowAdapter {

	public void windowClosing(WindowEvent e){
    	   platform.exit() ;
       }
    }

    // IMPLEMENTING THE CONFIGURABLE INTERFACE //
    /////////////////////////////////////////////
    
	public String getId(){return "mainFrame";}
	

    public void notifyOfSave(Configuration config)/*throws Exception*/{
        Rectangle r = getBounds();
        config.setValue("Position.x", Integer.toString(r.x));
        config.setValue("Position.y", Integer.toString(r.y));
        config.setValue("Width", Integer.toString(r.width));
        config.setValue("Height", Integer.toString(r.height));
    }

    public void notifyOfLoad(Configuration config)
    {
        Rectangle r = this.getBounds();
        String temp = config.getValue("Position.x");
        if (temp != null) r.x = new Integer(temp).intValue();
        temp = config.getValue("Position.y");
        if (temp != null) r.y = new Integer(temp).intValue();
        temp = config.getValue("Width");
        if (temp != null) r.width = new Integer(temp).intValue();
        temp = config.getValue("Height");
        if (temp != null) r.height = new Integer(temp).intValue();
        //System.out.println( "Setting Bounds to " +r.x+ ", " +r.y+ ", " +r.width+ ", " +r.height ) ;
        setBounds(r);
        invalidate() ;
        //System.out.println( "invalidate completed." ) ;
        validate();
        //System.out.println( "validate completed." ) ;
        
    }
    
    // Implementing the ExternalCommandInterface  //
    ////////////////////////////////////////////////

	@Override
	public int getStatusCode() {
		return tmMainPanel.getStatusCode() ;
	}

	@Override
	public String getStatusMessage() {
		return tmMainPanel.getStatusMessage() ;
	}

	@Override
	public void setTestMode(boolean turnOn) {
		tmMainPanel.setTestMode(turnOn);
	}

	@Override
	public void loadString(String fileName, String programSource) {
		tmMainPanel.loadString(fileName, programSource);
	}

	@Override
	public void loadRemoteFile(String root, String fileName) {
		tmMainPanel.loadRemoteFile(root, fileName);
	}

	@Override
	public void loadRemoteFile(String fileName) {
		tmMainPanel.loadRemoteFile(fileName);
		
	}

	@Override
	public void loadLocalFile(File directory, String fileName) {
		tmMainPanel.loadLocalFile(directory, fileName);
	}

	@Override
	public void readRemoteConfiguration(String fileName) {
		tmMainPanel.readRemoteConfiguration(fileName);
	}

	@Override
	public void registerRemoteDataFile(String fileName) {
		tmMainPanel.readRemoteConfiguration(fileName);
	}

	@Override
	public void clearRemoteDataFiles() {
		tmMainPanel.clearRemoteDataFiles();
	}

	@Override
	public void addInputString(String input) {
		tmMainPanel.addInputString(input);
	}

	@Override
	public void addProgramArgument(String argument) {
		tmMainPanel.addProgramArgument(argument);
	}

	@Override
	public String getOutputString() {
		return tmMainPanel.getOutputString();
	}

	@Override
	public void reStart() {
		tmMainPanel.reStart();
	}

	@Override
	public void editCurrentFile() {
		tmMainPanel.editCurrentFile();
	}

	@Override
	public void quit() {
		tmMainPanel.quit() ;
	}

	@Override
	public void initializeTheState() {
		tmMainPanel.initializeTheState();
	}

	@Override
	public void goBack() {
		tmMainPanel.goBack();
	}

	@Override
	public void redo() {
		tmMainPanel.redo() ;
	}

	@Override
	public void go(String commandString) {
		tmMainPanel.go(commandString);
	}

	@Override
	public void goForward() {
		tmMainPanel.goForward();
	}

	@Override
	public void microStep() {
		tmMainPanel.microStep();
	}

	@Override
	public void overAll() {
		tmMainPanel.overAll();
	}

	@Override
	public void intoExp() {
		tmMainPanel.intoExp();
	}

	@Override
	public void intoSub() {
		tmMainPanel.intoSub();
	}

	@Override
	public void toBreakPoint() {
		tmMainPanel.toBreakPoint();
	}

	@Override
	public void toCursor(String fileName, int cursor) {
		tmMainPanel.toCursor(fileName, cursor);
	}

	@Override
	public void autoRun() {
		tmMainPanel.autoRun();
	}

	@Override
	public boolean isTMShowing() {
		return tmMainPanel.isTMShowing() ;
	}
    
    
    /** Show or hide the Teaching Machine's main window
     * @param visible When this is true, the TM's main window is comes to the front of the desktop.
     *                When this is false, the TM's main window is hidden
     */
	@Override
    public void showTM(boolean visible){
    	//TODO Move to application platform and 
        setVisible( visible ) ;
        if( visible ) {
            int state = getExtendedState() ;
            if( (state & JFrame.ICONIFIED) != 0 )
                setExtendedState( state & ~ JFrame.ICONIFIED ) ;
            toFront() ;
        }
    }

	@Override
	public void autoStep() {
		tmMainPanel.autoStep(); 
	}

	@Override
	public void autoStep(String fileName, int lineNo) {
		tmMainPanel.autoStep(fileName, lineNo);
	}

	@Override
	public void stopAuto() {
		tmMainPanel.stopAuto();
	}

	@Override
	public void setAutoStepRate(int rate) {
		tmMainPanel.setAutoStepRate(rate);
	}

	@Override
	public Image getSnap(String plugIn, String id) {
		return tmMainPanel.getSnap(plugIn, id);
	}

	@Override
	public int getLastSnapWidth() {
		return tmMainPanel.getLastSnapWidth();
	}

	@Override
	public int getLastSnapHeight() {
		return tmMainPanel.getLastSnapHeight();
	}

	@Override
	public boolean getComparison(String plugIn, int n) {
		return tmMainPanel.getComparison(plugIn, n);
	}

	@Override
	public long getLocalInt(String datumName) {
		return tmMainPanel.getLocalInt(datumName);
	}

	@Override
	public boolean isRunDone() {
		return tmMainPanel.isRunDone();
	}

	@Override
	public void setSelectionString(String str) {
		tmMainPanel.setSelectionString( str );
	}

	@Override
	public String getSelectionString() {
		return tmMainPanel.getSelectionString() ;
	}
}