/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Mateusz
 */
public class MainWindow extends javax.swing.JFrame {

    /**
     * Creates new form MainWindow
     */
    
    JCanvasPanel canvas;
    
    DataManager dm;
    Utility util;
    
    //int selectedLayer = 0;  //selected layer for dragging
    Boolean isPressed = false;  //track if mouse button is pressed
    
    //Point initialP; //save coords of when the mouse was pressed
    Point differenceP;  //calculate difference between initial click and dragged point
    
    ButtonGroup butGrpPolygon; //button group with all buttons from polygon tools tab
    
    public MainWindow() throws IOException {
        
        butGrpPolygon = new ButtonGroup();

        dm = new DataManager();
        util = new Utility(dm);
        
        //setting up canvas panel
        canvas = new JCanvasPanel(dm);
        
        canvas.update();   
        differenceP = new Point(0, 0);
        //end setting up
        
        //ADD MOUSE LISTENER
            
            canvas.addMouseMotionListener(new MouseMotionListener() {
            
            //enable dragging motion for selected node
            @Override
            public void mouseDragged(MouseEvent e) {
                
                if(isPressed && dm.selectedNode != -1){
                    dm.polygons.get(dm.selectedPolygon).nodes.get(dm.selectedNode).x = e.getX() + differenceP.x;
                    dm.polygons.get(dm.selectedPolygon).nodes.get(dm.selectedNode).y = e.getY() + differenceP.y;
                    
                    if(dm.polygons.get(dm.selectedPolygon).jarvisConvexHull){
                    
                        util.jarvisConvexHull();
                        
                    }
                    //util.jarvisConvexHull();
                    canvas.update();
                    canvas.repaint();
                }
                
                if(isPressed && dm.selectedNode == -1){
                
                    dm.currentlyPicked = new Point(e.getX(), e.getY());
                    canvas.update();
                    
                }
                
            }
            
            

            @Override
            public void mouseMoved(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
            
            canvas.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

                
                
            }

            @Override
            public void mousePressed(MouseEvent e) {
                
                isPressed = true;
                dm.selectedPolygon = polygonList.getSelectedIndex();
                dm.selectedNode = util.getSelectedNodeFromPolygon(e.getX(), e.getY());
                
                //save initially picked point
                dm.initiallyPicked.x = e.getX();
                dm.initiallyPicked.y = e.getY();
                //===========================
                
                //disable currently used tool
                //DISENGAGE ALL
                if(SwingUtilities.isRightMouseButton(e)){
                
                    //if(dm.nodeInsertMode){
                        //disengage all modes
                        util.disengageAllTools();
                        dm.nodeTranslationMode = true;
                        
                        addNodeToggle.setSelected(false);
                        connectNodesToggle.setSelected(false);
                        edgeSelectToggle.setSelected(false);
                        
                        setCursor(dm.cursorDefault);
                        
                        util.clearConnection();
                        util.deselectAll();
                    //}
                    
                }
                
                //calculate difference between picked point and layer position
                if(dm.selectedNode != -1){
                    differenceP.x = dm.polygons.get(dm.selectedPolygon).nodes.get(dm.selectedNode).x - dm.initiallyPicked.x;
                    differenceP.y = dm.polygons.get(dm.selectedPolygon).nodes.get(dm.selectedNode).y - dm.initiallyPicked.y;
                }
                
                //selecting one of nodes
                if(isPressed && dm.selectedNode != -1){
                    
                    //node connection mode is active -> start connecting nodes
                    if(dm.nodeConnectionMode){
                    
                        util.addEdge(dm.selectedNode);
                        
                    }
                    
                    if(dm.nodeTranslationMode){
                        dm.polygons.get(dm.selectedPolygon).nodes.get(dm.selectedNode).x = e.getX() + differenceP.x;
                        dm.polygons.get(dm.selectedPolygon).nodes.get(dm.selectedNode).y = e.getY() + differenceP.y;
                    }
                    
                    canvas.update();
                    canvas.repaint();
                    
                }else{
                    //insert node tool is selected -> switch to node insertion mode
                    if(dm.nodeInsertMode){
                    
                        try {
                            util.addPoint(e.getX(), e.getY());
                            canvas.update();
                            canvas.repaint();
                            //selectedLayer = dm.polygons.get(dm.selectedPolygon).nodes.size()-1;
                        } catch (IOException ex) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                    }
                    
                    //edge selection tool is active
                    if(dm.edgeSelectionMode){
                    
                        dm.selectedEdge = util.getSelectedEdge(e.getX(), e.getY());
                        if(dm.selectedEdge != -1)
                            dm.polygons.get(dm.selectedPolygon).edges.get(dm.selectedEdge).selected = true;
                        
                        canvas.update();
                        canvas.repaint();
                        
                    }
                    
                }

            }

            //when mouse is released the cursor is set back to layer -1 which means nothing is selected
            @Override
            public void mouseReleased(MouseEvent e) {
                
                isPressed = false;
                dm.selectedNode = -1;
                
                dm.currentlyPicked = new Point(0, 0);
                canvas.update();

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseExited(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        
        
        //END
        
        initComponents();
        
        this.setLocationRelativeTo(null);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        mainPan.add(canvas, BorderLayout.CENTER);
        
        setCursor(dm.cursorDefault);
        
        //get access to list model
        polygonList.setModel(dm.polygonList);
        
        //if there's no polygon add new
        if(dm.polygons.size() == 0){

            Polygon poly = new Polygon();
            poly.id = 0;
            dm.polygons.add(poly);

            dm.polygonList.addElement("Polygon #" + poly.id);
            
            dm.selectedPolygon = 0;
            polygonList.setSelectedIndex(dm.selectedPolygon);
            
            polygonList.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    
                    dm.selectedPolygon = polygonList.getSelectedIndex();
                    canvas.update();
                    
                }
            });
            
        }
        
        this.validate();
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPan = new javax.swing.JPanel();
        utilityPanel = new javax.swing.JPanel();
        toolPanel = new javax.swing.JPanel();
        polygonPan = new javax.swing.JPanel();
        createPolygonBut = new javax.swing.JButton();
        deletePolygonBut = new javax.swing.JButton();
        addNodeToggle = new javax.swing.JToggleButton();
        connectNodesToggle = new javax.swing.JToggleButton();
        applyJarvisConvexHullBut = new javax.swing.JButton();
        edgeSelectToggle = new javax.swing.JToggleButton();
        jButton9 = new javax.swing.JButton();
        polygonListPan = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        polygonList = new javax.swing.JList<>();
        polygonDispOptPan = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mainPan.setLayout(new java.awt.BorderLayout());

        utilityPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        utilityPanel.setPreferredSize(new java.awt.Dimension(120, 1063));
        utilityPanel.setLayout(new java.awt.GridLayout(3, 1));
        mainPan.add(utilityPanel, java.awt.BorderLayout.EAST);

        toolPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        toolPanel.setPreferredSize(new java.awt.Dimension(140, 861));
        toolPanel.setLayout(new java.awt.GridLayout(3, 1));

        polygonPan.setBorder(javax.swing.BorderFactory.createTitledBorder("Polygon tools"));
        polygonPan.setPreferredSize(new java.awt.Dimension(150, 128));
        polygonPan.setLayout(new java.awt.GridLayout(5, 2));

        createPolygonBut.setIcon(new javax.swing.ImageIcon("D:\\NetBeansProjects\\GOapp\\assets\\newPolygon.png")); // NOI18N
        createPolygonBut.setToolTipText("Create new polygon");
        createPolygonBut.setBorderPainted(false);
        createPolygonBut.setContentAreaFilled(false);
        createPolygonBut.setPreferredSize(new java.awt.Dimension(50, 50));
        createPolygonBut.setPressedIcon(new javax.swing.ImageIcon("D:\\NetBeansProjects\\GOapp\\assets\\newPolygonPressed.png")); // NOI18N
        createPolygonBut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createPolygonButActionPerformed(evt);
            }
        });
        polygonPan.add(createPolygonBut);

        deletePolygonBut.setIcon(new javax.swing.ImageIcon("D:\\NetBeansProjects\\GOapp\\assets\\deletePolygon.png")); // NOI18N
        deletePolygonBut.setToolTipText("Delete selected polygon");
        deletePolygonBut.setBorderPainted(false);
        deletePolygonBut.setContentAreaFilled(false);
        deletePolygonBut.setPressedIcon(new javax.swing.ImageIcon("D:\\NetBeansProjects\\GOapp\\assets\\deletePolygonPressed.png")); // NOI18N
        deletePolygonBut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deletePolygonButActionPerformed(evt);
            }
        });
        polygonPan.add(deletePolygonBut);

        addNodeToggle.setIcon(new javax.swing.ImageIcon("D:\\NetBeansProjects\\GOapp\\assets\\isolatedNode.png")); // NOI18N
        addNodeToggle.setBorderPainted(false);
        addNodeToggle.setContentAreaFilled(false);
        addNodeToggle.setPressedIcon(new javax.swing.ImageIcon("D:\\NetBeansProjects\\GOapp\\assets\\isolatedNodePressed.png")); // NOI18N
        addNodeToggle.setSelectedIcon(new javax.swing.ImageIcon("D:\\NetBeansProjects\\GOapp\\assets\\isolatedNodeSelected.png")); // NOI18N
        addNodeToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNodeToggleActionPerformed(evt);
            }
        });
        polygonPan.add(addNodeToggle);

        connectNodesToggle.setIcon(new javax.swing.ImageIcon("D:\\NetBeansProjects\\GOapp\\assets\\connectNode.png")); // NOI18N
        connectNodesToggle.setBorderPainted(false);
        connectNodesToggle.setContentAreaFilled(false);
        connectNodesToggle.setPressedIcon(new javax.swing.ImageIcon("D:\\NetBeansProjects\\GOapp\\assets\\connectNodePressed.png")); // NOI18N
        connectNodesToggle.setSelectedIcon(new javax.swing.ImageIcon("D:\\NetBeansProjects\\GOapp\\assets\\connectNodeSelected.png")); // NOI18N
        connectNodesToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectNodesToggleActionPerformed(evt);
            }
        });
        polygonPan.add(connectNodesToggle);

        applyJarvisConvexHullBut.setIcon(new javax.swing.ImageIcon("D:\\NetBeansProjects\\GOapp\\assets\\convexJarvis.png")); // NOI18N
        applyJarvisConvexHullBut.setToolTipText("");
        applyJarvisConvexHullBut.setBorderPainted(false);
        applyJarvisConvexHullBut.setContentAreaFilled(false);
        applyJarvisConvexHullBut.setPressedIcon(new javax.swing.ImageIcon("D:\\NetBeansProjects\\GOapp\\assets\\convexJarvisPressed.png")); // NOI18N
        applyJarvisConvexHullBut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyJarvisConvexHullButActionPerformed(evt);
            }
        });
        polygonPan.add(applyJarvisConvexHullBut);

        edgeSelectToggle.setText("Edge");
        edgeSelectToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edgeSelectToggleActionPerformed(evt);
            }
        });
        polygonPan.add(edgeSelectToggle);

        jButton9.setText("jButton9");
        polygonPan.add(jButton9);

        toolPanel.add(polygonPan);

        polygonListPan.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        polygonList.setBorder(javax.swing.BorderFactory.createTitledBorder("Polygons"));
        polygonList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        polygonList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(polygonList);

        polygonListPan.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        polygonDispOptPan.setLayout(new java.awt.GridLayout(1, 0));

        jCheckBox1.setText("jCheckBox1");
        polygonDispOptPan.add(jCheckBox1);

        polygonListPan.add(polygonDispOptPan, java.awt.BorderLayout.PAGE_END);

        toolPanel.add(polygonListPan);

        mainPan.add(toolPanel, java.awt.BorderLayout.LINE_START);

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPan, javax.swing.GroupLayout.DEFAULT_SIZE, 950, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void createPolygonButActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createPolygonButActionPerformed
        
        Polygon newPoly = new Polygon();
        newPoly.id = dm.maxPolygonId++;
        
        dm.polygons.add(newPoly);
        dm.polygonList.addElement("Polygon #" + newPoly.id);
        
        dm.selectedPolygon = dm.polygonList.size()-1;
        polygonList.setSelectedIndex(dm.selectedPolygon);
        
    }//GEN-LAST:event_createPolygonButActionPerformed

    private void deletePolygonButActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deletePolygonButActionPerformed
        
        int removedIdx = dm.selectedPolygon;
        
        dm.polygons.remove(dm.selectedPolygon);
        dm.polygonList.remove(dm.selectedPolygon);
        
        dm.selectedPolygon = removedIdx-1;
        
        if(dm.selectedPolygon < 0)
            dm.selectedPolygon = 0;
        
        polygonList.setSelectedIndex(dm.selectedPolygon);
        
        canvas.update();
        
    }//GEN-LAST:event_deletePolygonButActionPerformed

    private void connectNodesToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectNodesToggleActionPerformed
        
        if(dm.nodeConnectionMode){
            
            util.disengageAllTools();   //turn off all selected tools
            
            dm.nodeTranslationMode = true;
            setCursor(dm.cursorDefault);
        }
        else{
            dm.nodeConnectionMode = true;
            setCursor(dm.cursorConnect);
        }
        
    }//GEN-LAST:event_connectNodesToggleActionPerformed

    private void addNodeToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNodeToggleActionPerformed
        
        if(dm.nodeInsertMode){
            
            util.disengageAllTools();   //turn off all selected tools
            
            dm.nodeTranslationMode = true;
            setCursor(dm.cursorDefault);
        }
        else{
            dm.nodeInsertMode = true;
            setCursor(dm.cursorNodeTool);
        }
        
    }//GEN-LAST:event_addNodeToggleActionPerformed

    private void applyJarvisConvexHullButActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyJarvisConvexHullButActionPerformed
        
        dm.polygons.get(dm.selectedPolygon).jarvisConvexHull = true;
        
        util.jarvisConvexHull();
        canvas.update();
        
    }//GEN-LAST:event_applyJarvisConvexHullButActionPerformed

    private void edgeSelectToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edgeSelectToggleActionPerformed
        
        if(dm.edgeSelectionMode){
            util.disengageAllTools();   //turn off all selected tools
            
            dm.nodeTranslationMode = true;
            setCursor(dm.cursorDefault);
        }
        else{
            dm.edgeSelectionMode = true;
            setCursor(dm.cursorNodeTool);
        }
        
    }//GEN-LAST:event_edgeSelectToggleActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new MainWindow().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton addNodeToggle;
    private javax.swing.JButton applyJarvisConvexHullBut;
    private javax.swing.JToggleButton connectNodesToggle;
    private javax.swing.JButton createPolygonBut;
    private javax.swing.JButton deletePolygonBut;
    private javax.swing.JToggleButton edgeSelectToggle;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel mainPan;
    private javax.swing.JPanel polygonDispOptPan;
    private javax.swing.JList<String> polygonList;
    private javax.swing.JPanel polygonListPan;
    private javax.swing.JPanel polygonPan;
    private javax.swing.JPanel toolPanel;
    private javax.swing.JPanel utilityPanel;
    // End of variables declaration//GEN-END:variables
}
