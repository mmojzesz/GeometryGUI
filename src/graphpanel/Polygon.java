/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphpanel;

import java.util.ArrayList;

/**
 *
 * @author Mateusz
 */
public class Polygon {
    
    ArrayList<Node> nodes;
    ArrayList<Edge> edges;
    
    int id = 0;
    
    Boolean jarvisConvexHull = false;

    public Polygon() {
        
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
        
    }
    
    
    
}
