/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphpanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author Mateusz
 */
public class JCanvasPanel extends JPanel{
    
//    ArrayList<Node> layers;
//    ArrayList<Node> convex;
    
    int offset = 50;    //offset of string with coordinates
    
    DataManager dm;

    public JCanvasPanel(DataManager dm) {
        this.dm = dm;
    }

    //update list of layers
    void update(){

        this.repaint();
        
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.
        
        BufferedImage canvasImg = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);     
        Graphics2D canvasPen = canvasImg.createGraphics();
        
        //======================================================================
        //draw selection
        
        if(dm.currentlyPicked.x != 0 && dm.currentlyPicked.y != 0){
            int xDrag = dm.initiallyPicked.x - dm.currentlyPicked.x;
            int yDrag = dm.initiallyPicked.y - dm.currentlyPicked.y;

            g.setColor(new Color(0, 0, 0, 64));
            g.fillRect(dm.initiallyPicked.x, dm.initiallyPicked.y, -xDrag, -yDrag);
        }
        
        //end draw selection
        //======================================================================


        //======================================================================
        canvasPen.setColor(Color.BLACK);
        //convex hull
//        for(int i = 0; i < dm.convexHull.size()-1; i++){
//        
//            canvasPen.drawLine(dm.convexHull.get(i).x, dm.convexHull.get(i).y, dm.convexHull.get(i+1).x, dm.convexHull.get(i+1).y);
//            
//        }
        
        //if(dm.convexHull.size() > 0)
            //canvasPen.drawLine(dm.convexHull.get(dm.convexHull.size()-1).x, dm.convexHull.get(dm.convexHull.size()-1).y, dm.convexHull.get(0).x, dm.convexHull.get(0).y);
        
//        //======================================================================
//
//        canvasPen.setColor(Color.BLACK);
//        
//        for(int i = dm.layers.size()-1; i >= 0; i--){
//            
//            int leftX = dm.layers.get(i).x - dm.layers.get(i).img.getWidth()/2;
//            int leftY = dm.layers.get(i).y - dm.layers.get(i).img.getHeight()/2;
//        
//            canvasPen.drawImage(dm.layers.get(i).img, leftX, leftY, null);
//            canvasPen.drawString("(" + i + "|" + leftX + ", " + leftY + ")", leftX+offset, leftY+offset);
//            //g.drawRect(layers.get(i).x-layers.get(i).img.getWidth()/2, layers.get(i).y-layers.get(i).img.getHeight()/2, layers.get(i).img.getWidth(), layers.get(i).img.getHeight());
//            
//        }

        //======================================================================

        //draw only if there's something to draw
        if(dm.polygons.size() > 0){
        
            //if(dm.polygons.get(dm.selectedPolygon).nodes.size() > 0){

                for(Polygon poly : dm.polygons){
                    
                    if(poly.edges.size() > 0)
                        for(Edge ed : poly.edges){
                            
                            if(ed.selected)
                                canvasPen.setStroke(new BasicStroke(3));
                            else
                                canvasPen.setStroke(new BasicStroke(1));

                            canvasPen.drawLine(ed.nodes[0].x, ed.nodes[0].y, ed.nodes[1].x, ed.nodes[1].y);
         
                        }
                    
                    if(poly.nodes.size() > 0)
                        for(int i = poly.nodes.size()-1; i >= 0 ; i--){

                            Node polyNode = poly.nodes.get(i);

                            int leftX = polyNode.x - polyNode.img.getWidth()/2;
                            int leftY = polyNode.y - polyNode.img.getHeight()/2;
                            
                            //draw actual node
                            canvasPen.drawImage(polyNode.img, leftX, leftY, null);
                            if(polyNode.selected)
                                canvasPen.drawRect(polyNode.x-polyNode.img.getWidth()/2, polyNode.y-polyNode.img.getHeight()/2, polyNode.img.getWidth(), polyNode.img.getHeight());
                            canvasPen.drawString("#" + dm.polygons.get(dm.polygons.indexOf(poly)).id + " (" + i + "|" + leftX + ", " + leftY + ")", leftX+offset, leftY+offset);
                            canvasPen.drawString("Test", leftX+offset, (int)(leftY+(1.3*offset)));

                        }
                }
                
            //}
            
        }
        
        //======================================================================
        
        g.drawImage(canvasImg, 0, 0, this);
        
    }

    @Override
    public void repaint() {
        super.repaint(); //To change body of generated methods, choose Tools | Templates.

    }
    
    
    
    
    
}
