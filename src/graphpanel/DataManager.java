/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphpanel;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.ArrayList;
import javax.swing.DefaultListModel;

/**
 *
 * @author Mateusz
 */
public class DataManager {
    
    ArrayList<Node> layers;
    volatile ArrayList<Node> convexHull;
    
    ArrayList<Polygon> polygons;
    int selectedPolygon = 0;    //currently selected polygon(from list of all polygons)
    int selectedNode = 0;   //currently selected node belonging to a polygon
    int selectedEdge = 0;   //currently selected edge from polygon
    int maxPolygonId = 1;   //needs to be 1 because there is already one polygon each time program starts
    
    String topPath = "assets/top/"; //used???
    
    Boolean nodeInsertMode = false; //value to determine whenever node insertion mode is running
    //Boolean polygonStarted = false; //value used to keep track when to add a new polygon
    Boolean nodeConnectionMode = false; //value to determine if the node insertion mode is active
    Boolean nodeTranslationMode = true;    //in this mode nodes are moved using cursor --standard behaviour
    Boolean nodeDeletionMode = false;   //is deleting tool in use? (for node deletion only)
    Boolean edgeSelectionMode = false;  //value used to determine whenever edge selection tool is being used
    
    //Boolean edgeStarted = false;    //value is tracking if the first node was selected in polygon or not
    
    Cursor cursorDefault;   //default shape of cursor "idling"
    Cursor cursorNodeTool;  //cursor when node tool is used
    Cursor cursorConnect;   //cursor for node connection tool
    
    Point initiallyPicked;  //initially picked point when clicked on canvas
    Point currentlyPicked;  //currenlty selected point
    
    ArrayList<Node> connections;    //connection between each nodes has two ends, variable is stored temporarily
    //int connections
    
    DefaultListModel<String> polygonList;   //list of polygons for purpose of displaing them in polygons view window
    
    //==========================================================================
    
    int toolSize = 25;
    
    //==========================================================================
    
    Node prevNode;

    public DataManager() {
        
        layers = new ArrayList<>();
        convexHull = new ArrayList<>();
        polygons = new ArrayList<>();
        connections = new ArrayList<>();
        
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image img = toolkit.getImage("assets/CursorBase.png");
        Image imgNode = toolkit.getImage("assets/CursorNode.png");
        Image imgConnect = toolkit.getImage("assets/CursorConnect.png");
        
        initiallyPicked = new Point(0, 0);
        currentlyPicked = new Point(0, 0);
        
        
        this.cursorDefault = toolkit.createCustomCursor(img, new Point(0, 0), "DefaultCursor");
        this.cursorNodeTool = toolkit.createCustomCursor(imgNode, new Point(0, 0), "NodeCursor");
        this.cursorConnect = toolkit.createCustomCursor(imgConnect, new Point(0, 0), "ConnectionCursor");
        
        polygonList = new DefaultListModel<>();
        
    }
    
    
    
}
