/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphpanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author Mateusz
 */
public class Utility {
    
    DataManager dm;
    Color transparent;
    
    int id = 0;

    public Utility(DataManager dm) {
        this.dm = dm;
        
        transparent = new Color(0, 0, 0, 0);
        
    }
    
    //method adds point to stack
    void addPoint(int x, int y) throws IOException{
        
        int size = 50;

            ImageIcon imageIcon = new ImageIcon("assets/top/point.png");
            Image tmpImage = imageIcon.getImage();

            BufferedImage im = new BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR);
            im.getGraphics().drawImage(tmpImage, 0, 0, null);
       
            
            Node newLay = new Node();
            
            newLay.img = im;
            newLay.id = id++;
//            newLay.layernum = id++;
            newLay.polygonId = dm.selectedPolygon;
            
            newLay.x = x;
            newLay.y = y;

            
            
            dm.polygons.get(dm.selectedPolygon).nodes.add(newLay);
        
    }
    
    void deselectAll(){
    
        for(Node n : dm.polygons.get(dm.selectedPolygon).nodes){
        
            n.selected = false;
            
        }
        
        for(Edge e : dm.polygons.get(dm.selectedPolygon).edges){
        
            e.selected = false;
            
        }
        
    }
    
    //function turns off all selected tools
    void disengageAllTools(){
    
        dm.nodeConnectionMode = false;
        dm.nodeDeletionMode = false;
        dm.nodeInsertMode = false;
        dm.edgeSelectionMode = false;
        
    }
    
    //function clears selection buffer
    void clearConnection(){
    
        //dm.connection = new Node[2];
        dm.connections = new ArrayList<>();
        
    }
    
    //function adds new connection between nodes
    void addEdge(int selectedNode){
    
//        if(dm.connections.size() == 0){
        
            //add first node to connection
//            dm.connections.add(dm.polygons.get(dm.selectedPolygon).nodes.get(selectedNode));
            
//        }else{
//        
//            //dm.connections.add(dm.polygons.get(dm.selectedPolygon).nodes.get(selectedNode));
//            
//            for(Node n : dm.polygons.get(dm.selectedPolygon).nodes)
//                if(n.id == dm.connections.get(0).id)
//                    dm.connections.get(0).next = dm.polygons.get(dm.selectedPolygon).nodes.get(selectedNode);
//            
//            //dm.connections.get(0).next = dm.connections.get(1);
//            //dm.connections.get(1).prev = dm.connections.get(0);
//            
//            //clearConnection();
//            
//        }

        dm.connections.add(dm.polygons.get(dm.selectedPolygon).nodes.get(selectedNode));
        
        if(dm.connections.size() > 1){
        
            dm.prevNode.selected = false;
            //dm.connections.get(0).connections.add(dm.polygons.get(dm.selectedPolygon).nodes.get(selectedNode));
            dm.polygons.get(dm.selectedPolygon).edges.add(new Edge(dm.prevNode, dm.polygons.get(dm.selectedPolygon).nodes.get(selectedNode)));
            dm.prevNode = dm.polygons.get(dm.selectedPolygon).nodes.get(selectedNode);
            dm.polygons.get(dm.selectedPolygon).nodes.get(selectedNode).selected = true;
            
        }else{
            dm.polygons.get(dm.selectedPolygon).nodes.get(selectedNode).selected = true;
            dm.prevNode = dm.polygons.get(dm.selectedPolygon).nodes.get(selectedNode);
        }
        //dm.edgeStarted = true;
        
        
        //dm.polygons.get(dm.selectedPolygon).nodes.get(selectedNode).connections.add(e)
        
        //clearConnection();
        
    }
    
    void addPointRandomly() throws IOException{
    
        File toLoad[] = new File(dm.topPath).listFiles();
        
        int size = 50;
        
        //for(File f : toLoad){
        
            Random rand = new Random(System.currentTimeMillis());
            
            ImageIcon imageIcon = new ImageIcon("assets/top/point.png");
            Image tmpImage = imageIcon.getImage();

            BufferedImage im = new BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR);
            im.getGraphics().drawImage(tmpImage, 0, 0, null);
            
            //Graphics g = im.getGraphics();
            //g.setColor(Color.red);
            //g.drawOval(0, 0, size, size);
            //tmpImage.flush();
            
            
            
            Node newLay = new Node();
            
            newLay.img = im;
            newLay.id = id;
            newLay.layernum = id++;
            
            newLay.x = 100 + rand.nextInt(1000);
            newLay.y = 100 + rand.nextInt(800);
            
            dm.layers.add(newLay);
            
            //g.dispose();
            
        //}
        
    }
    
    //legacy version
    void jarvisConvexHullLegacy(){

        if(dm.layers.size() > 2){

            dm.convexHull = new ArrayList<>();  //clear previously found convex hull

            Node rightPoint = dm.layers.get(0);   //point located on the rightmost

            //find point on the right
            for(int i = 0; i < dm.layers.size(); i++){

                if(dm.layers.get(i).x >= rightPoint.x){

                    rightPoint = dm.layers.get(i);

                }

            }

            //======================================================================
            dm.convexHull.add(rightPoint);  //add first point to convex hull

            Node nextPoint = dm.layers.get(0);
            
            int i = 0;
            
            Random rand = new Random(System.currentTimeMillis());

            do{
                
                nextPoint = dm.layers.get(i % dm.layers.size());
                
                if(rightPoint.x == dm.layers.get(0).x && rightPoint.y == dm.layers.get(0).y)
                    nextPoint = dm.layers.get(1 + rand.nextInt(dm.layers.size()-1));
                
                for(int j = 0; j < dm.layers.size(); j++){

                    if(onLeft(dm.layers.get(j), nextPoint, dm.convexHull.get(i)) == 2){  //check if there's any point on the left of the line
                        nextPoint = dm.layers.get(j);
                    }

                }

                dm.convexHull.add(nextPoint);
                i++;
                
            }while((nextPoint.x != dm.convexHull.get(0).x && nextPoint.y != dm.convexHull.get(0).y));

        }
        
    }
    
    void jarvisConvexHull(){

        Polygon curPoly = dm.polygons.get(dm.selectedPolygon);
        
        //clear old
//        for(Node n : curPoly.nodes){
//        
//            n.connections.clear();
//            
//        }

        dm.polygons.get(dm.selectedPolygon).edges.clear();
        
        if(curPoly.nodes.size() > 2){

            dm.convexHull = new ArrayList<>();  //clear previously found convex hull

            Node rightPoint = curPoly.nodes.get(0);   //point located on the rightmost

            //find point on the right
            for(int i = 0; i < curPoly.nodes.size(); i++){

                if(curPoly.nodes.get(i).x >= rightPoint.x){

                    rightPoint = curPoly.nodes.get(i);

                }

            }

            //======================================================================
            dm.convexHull.add(rightPoint);  //add first point to convex hull

            Node nextPoint = curPoly.nodes.get(0);
            
            int i = 0;
            
            Random rand = new Random(System.currentTimeMillis());

            do{
                
                nextPoint = curPoly.nodes.get(i % curPoly.nodes.size());
                
                if(rightPoint.x == curPoly.nodes.get(0).x && rightPoint.y == curPoly.nodes.get(0).y)
                    nextPoint = curPoly.nodes.get(1 + rand.nextInt(curPoly.nodes.size()-1));
                
                for(int j = 0; j < curPoly.nodes.size(); j++){

                    if(onLeft(curPoly.nodes.get(j), nextPoint, dm.convexHull.get(i)) == 2){  //check if there's any point on the left of the line
                        nextPoint = curPoly.nodes.get(j);
                    }

                }

                dm.convexHull.add(nextPoint);
                i++;
                
                curPoly.edges.add(new Edge(dm.convexHull.get(i-1), dm.convexHull.get(i)));
                
            }while((nextPoint.x != dm.convexHull.get(0).x && nextPoint.y != dm.convexHull.get(0).y));

//            for(int k = 0; k < dm.convexHull.size()-2; k++){
//            
//                //dm.convexHull.get(k).connections.add(dm.convexHull.get(k+1));
//                curPoly.nodes.get(k).connections.add(dm.convexHull.get(k+1));
//                
//            }
            
//            curPoly.nodes.clear();
//            curPoly.nodes.addAll(dm.convexHull);
            
        }
        
    }
    
    ArrayList<Node> sortPointsByX(){
        
        ArrayList<Node> sorted = new ArrayList<>();
        
        sorted.addAll(dm.layers);
        
        int maxX = 0;
        int idx = 0;
    
        for(int i = 0; i < dm.layers.size(); i++){
        
            for(int j = 0; j < dm.layers.size(); j++){
            
                if(sorted.get(j).x > maxX){
                
                    maxX = sorted.get(j).x;
                    idx = j;
                    
                }
                
                Node tmp = sorted.get(i);
                sorted.set(i, sorted.get(idx));
                sorted.set(idx, tmp);
                
            }
            
        }
        
        return sorted;
        
    }
    
    //check if provided point is part of the convex
    Boolean isConvex(Node chck){
        
        Boolean isPartOfConvex = false;
    
        for(Node lay : dm.convexHull){
        
            if(chck.x == lay.x && chck.y == lay.y)
                isPartOfConvex = true;
            else 
                isPartOfConvex = false;
            
        }
        
        return isPartOfConvex;
        
    }
    
    //check if point is on the left
    //if > 0 point is on the left
    //if < 0 point is on the right
    //if == 0 points on the same line
    int onLeft(Node p1, Node p2, Node p3){
    
        //apply correction
//        p1.x = p1.x - p1.img.getWidth()/2;
//        p1.y = p1.y - p1.img.getHeight()/2;
//        p2.x = p2.x - p2.img.getWidth()/2;
//        p2.y = p2.y - p2.img.getHeight()/2;
//        p3.x = p3.x - p3.img.getWidth()/2;
//        p3.y = p3.y - p3.img.getHeight()/2;
        
        
        //p1 is investigated point
        //p2-p3 is line
        
        int isLeft = 0;
        
        double eq = (p3.x - p2.x) * (p1.y - p2.y) - (p1.x - p2.x) * (p3.y - p2.y);
        //System.out.println("Eq: " + eq);
        
        if(eq > 0)
            isLeft = 2;
        
        if(eq < 0)
            isLeft = 1;
        
        return isLeft;
        //return ((p2.x - p1.x) * (p3.y - p1.y)) > ((p2.y - p1.y) * (p3.x - p1.x));
        
    }
    
    int orientation(Node p, Node q, Node r) 
    { 
        int val = (q.y - p.y) * (r.x - q.x) - 
                  (q.x - p.x) * (r.y - q.y);

        if (val == 0) return 0;  // colinear 
        return (val > 0)? 1: 2; // clock or counterclock wise 
    } 
    
    //legacy method used when theres no polygons on canvas
    int getSelectedLayer(int x, int y){
    
        int selected = -1;
        
        for(Node lay : dm.layers){
            
            int leftX = lay.x - lay.img.getWidth()/2;
            int leftY = lay.y - lay.img.getHeight()/2;
            
            int sizeX = lay.img.getWidth();
            int sizeY = lay.img.getHeight();
        
            if(x > lay.x-lay.img.getWidth()/2 && x < (lay.x-lay.img.getWidth()/2 + sizeX) && y > lay.y-lay.img.getHeight()/2 && y < (lay.y-lay.img.getHeight()/2 + sizeY)){
            
                //get color from selected pixel and check transparency
                Color col = new Color(lay.img.getRGB(x-leftX, y-leftY), true);  //important to have alpha value!!!
                
                if(col.getRGB() != transparent.getRGB()){
                
                    selected = dm.layers.indexOf(lay);
                    break;
                    
                }
                
            }
            
        }
        
        //swap layers (always with the top one)
        if(selected >= 0){
            
            Node tmp = dm.layers.get(selected);

            //dont actually swap, selected needs to be on top, rest below
            dm.layers.remove(selected);
            dm.layers.add(0, tmp);
            
            selected = 0;
        
        }
        
        return selected;

    }
    
    //method returns index of selected node regarding to polygon
    int getSelectedNodeFromPolygon(int x, int y){
    
        int selected = -1;
        
        if(dm.polygons.size() > 0){
        
            for(Node lay : dm.polygons.get(dm.selectedPolygon).nodes){

                int leftX = lay.x - lay.img.getWidth()/2;
                int leftY = lay.y - lay.img.getHeight()/2;

                int sizeX = lay.img.getWidth();
                int sizeY = lay.img.getHeight();

                if(x > lay.x-lay.img.getWidth()/2 && x < (lay.x-lay.img.getWidth()/2 + sizeX) && y > lay.y-lay.img.getHeight()/2 && y < (lay.y-lay.img.getHeight()/2 + sizeY)){

                    //get color from selected pixel and check transparency
                    Color col = new Color(lay.img.getRGB(x-leftX, y-leftY), true);  //important to have alpha value!!!

                    if(col.getRGB() != transparent.getRGB()){

                        selected = dm.polygons.get(dm.selectedPolygon).nodes.indexOf(lay);
                        break;

                    }

                }

            }

            //swap layers (always with the top one)
            if(selected >= 0){

                Node tmp = dm.polygons.get(dm.selectedPolygon).nodes.get(selected);

                //dont actually swap, selected needs to be on top, rest below
                dm.polygons.get(dm.selectedPolygon).nodes.remove(selected);
                dm.polygons.get(dm.selectedPolygon).nodes.add(0, tmp);

                selected = 0;

            }
        
        }
        
        return selected;

    }
    
    int getSelectedEdge(int x, int y){
    
        int selected = -1;
        
        if(dm.polygons.size() > 0){
        
            for(Edge ed : dm.polygons.get(dm.selectedPolygon).edges){
                
                //linear equation
                //y = ax + b
                
//                ed.nodes[0].x = 0;
//                ed.nodes[0].y = 2;
//                ed.nodes[1].x = 10;
//                ed.nodes[1].y = 12;
                
                //double a = (ed.nodes[1].y - ed.nodes[0].y) / (ed.nodes[1].x - ed.nodes[0].x);
                //double b = ed.nodes[0].y - ((ed.nodes[1].y - ed.nodes[0].y) / (ed.nodes[1].x - ed.nodes[0].x)) * ed.nodes[0].x;
                
                //==============================================================

//                int leftX = lay.x - lay.img.getWidth()/2;
//                int leftY = lay.y - lay.img.getHeight()/2;
//
//                int sizeX = lay.img.getWidth();
//                int sizeY = lay.img.getHeight();

                int test = ed.nodes[0].x * (y - ed.nodes[1].y) + x * (ed.nodes[1].y - ed.nodes[0].y) +  ed.nodes[1].x * (ed.nodes[0].y - y); 

                double tolerance = 2;
                //double equation = a * x + b;
                
                //System.out.println("a " + a + " " + b);
                
                //int eq1 = (ed.nodes[1].y - y) * (x - ed.nodes[0].x);
                //int eq2 = (y - ed.nodes[0].y) * (ed.nodes[1].x - x);

                //if(y >= (equation - tolerance) && y <= (equation + tolerance)){
                if(test >= 0 - tolerance && test <= 0 + tolerance){
                //if ((eq1 >= (eq2 - tolerance)) && (eq1 <= (eq2 + tolerance))){
                    
                    //dm.polygons.get(dm.selectedPolygon).edges
                    //ed.selected = true;
                    selected = dm.polygons.get(dm.selectedPolygon).edges.indexOf(ed);
                    
                }
                
                //System.out.println("Y: " + y + " | " + equation);
                System.out.println("==============================");

            }
        
        }
        
        return selected;
        
    }
    
}
