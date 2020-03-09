/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphpanel;

/**
 *
 * @author Mateusz
 */
public class Edge {
    
    Node nodes[];   //every edge has two nodes, start point and end point
    
    Boolean selected = false;   //value to track if edge is selected by user

    public Edge() {
        
        this.nodes = new Node[2];
        
    }

    public Edge(Node n1, Node n2) {
        this.nodes = new Node[2];
        
        this.nodes[0] = n1;
        this.nodes[1] = n2;
        
    }
    
}
