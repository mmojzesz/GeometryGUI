/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphpanel;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author Mateusz
 */
public class Node {
    
    int id;
    int layernum;
    
    //center coordinates
    int x;
    int y;
    
    BufferedImage img;
    
    Node next = null;   //save next instance of node
    Node prev = null;   //save previous instance of node
    
    ArrayList<Node> connections;
    
    int polygonId = 0;
    
    Boolean selected = false;

    public Node() {
        
        connections = new ArrayList<>();
        connections.add(this);
        
    }
    
    
    
}
