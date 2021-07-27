/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogs;

import java.awt.Dimension;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import frames.AppFrame;
import frames.UtilFrame;
import main.ArrayManager;
import panels.ShufflePanel;
import panes.JErrorPane;
import utils.Distributions;
import utils.ShuffleGraph;
import utils.ShuffleInfo;
import utils.Shuffles;
import utils.shuffle_utils.GraphReader;
import utils.shuffle_utils.GraphWriter;
import utils.shuffle_utils.GraphReader.MalformedGraphFileException;

/*
 * 
MIT License

Copyright (c) 2019 w0rthy
Copyright (c) 2021 ArrayV 4.0 Team

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 *
 */

/**
 *
 * @author S630690
 */
final public class ShuffleDialog extends javax.swing.JDialog implements AppFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private ArrayManager ArrayManager;
    private JFrame Frame;
    private UtilFrame UtilFrame;

    private boolean bypassEvents;
    
    /**
     * Creates new form SortPrompt
     */
    @SuppressWarnings("unchecked")
    public ShuffleDialog(ArrayManager ArrayManager, JFrame frame, UtilFrame utilFrame) {
        super(frame, "ArrayV Advanced Shuffle Editor", true);

        this.ArrayManager = ArrayManager;
        this.Frame = frame;
        this.UtilFrame = utilFrame;
        
        initComponents();

        bypassEvents = true;
        this.shuffleEditor.graph = ArrayManager.getShuffle();
        jList1.setListData(ArrayManager.getDistributionIDs());
        jList3.setListData(ArrayManager.getDistributionIDs());
        jList2.setListData(ArrayManager.getShuffleIDs());
        bypassEvents = false;

        setMinimumSize(new Dimension(580, 300));
        setAlwaysOnTop(false);
        reposition();
        setVisible(true); 
    }

    @Override
    public void reposition() {
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings({ "rawtypes" })
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        this.shuffleEditor = new ShufflePanel();

        this.jButton1 = new javax.swing.JButton();
        this.jButton2 = new javax.swing.JButton();

        this.jScrollPane1 = new javax.swing.JScrollPane();
        this.jList1 = new javax.swing.JList();
        this.jLabel1 = new javax.swing.JLabel();

        this.jScrollPane3 = new javax.swing.JScrollPane();
        this.jList3 = new javax.swing.JList();
        this.jLabel3 = new javax.swing.JLabel();

        this.jScrollPane2 = new javax.swing.JScrollPane();
        this.jList2 = new javax.swing.JList();
        this.jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jButton1.setText("Import...");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed();
            }
        });
        
        jButton2.setText("Export...");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed();
            }
        });

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setViewportView(this.jList1);

        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setViewportView(this.jList3);
        
        jScrollPane2.setViewportView(this.jList2);
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                try {
                    jList1ValueChanged(evt);
                } catch (Exception e) {
                    JErrorPane.invokeErrorMessage(e);
                }
            }
        });

        jLabel1.setText("Distribution Change");

        jList3.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                try {
                    jList3ValueChanged(evt);
                } catch (Exception e) {
                    JErrorPane.invokeErrorMessage(e);
                }
            }
        });

        jLabel3.setText("Distribution Warp");

        jList2.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                try {
                    jList2ValueChanged(evt);
                } catch (Exception e) {
                    JErrorPane.invokeErrorMessage(e);
                }
            }
        });

        jLabel2.setText("Shuffle");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addGroup(layout.createSequentialGroup()
                    .addGap(10, 10, 10)
                    .addComponent(this.shuffleEditor)
                    .addGap(10, 10, 10))
                .addGroup(layout.createSequentialGroup()
                    .addGap(10, 75, 75)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(this.jLabel1)
                        .addComponent(this.jScrollPane1, 175, 175, 175))
                    .addGap(10, 75, 75)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(this.jLabel3)
                        .addComponent(this.jScrollPane3, 175, 175, 175))
                    .addGap(10, 75, 75)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(this.jLabel2)
                        .addComponent(this.jScrollPane2, 175, 175, 175))
                    .addGap(10, 75, 75))
                .addGroup(layout.createSequentialGroup()
                    .addGap(150, 150, 150)
                    .addComponent(this.jButton1)
                    .addGap(20, 20, 20)
                    .addComponent(this.jButton2)
                    .addGap(150, 150, 150))
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(this.shuffleEditor)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(this.jLabel1)
                        .addComponent(this.jScrollPane1, 175, 175, 175))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(this.jLabel3)
                        .addComponent(this.jScrollPane3, 175, 175, 175))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(this.jLabel2)
                        .addComponent(this.jScrollPane2, 175, 175, 175)))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(this.jButton1)
                    .addComponent(this.jButton2))
                .addGap(10, 10, 10)
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed() {//GEN-FIRST:event_jButton1ActionPerformed
        FileDialog fileDialog = new ImportShuffleDialog();
        ShuffleGraph newShuffle;
        try {
            newShuffle = new GraphReader().read(fileDialog.file);
        } catch (IOException e) {
            e.printStackTrace();
            JErrorPane.invokeCustomErrorMessage("IO Error: " + e.getMessage());
            return;
        } catch (MalformedGraphFileException e) {
            e.printStackTrace();
            JErrorPane.invokeCustomErrorMessage("Error Parsing File: " + e.getMessage());
            return;
        }
        ArrayManager.setShuffle(newShuffle);
        this.shuffleEditor.graph = newShuffle;
        this.shuffleEditor.repaint();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed() {//GEN-FIRST:event_jButton1ActionPerformed
        FileDialog fileDialog = new ExportShuffleDialog();
        try {
            new GraphWriter(shuffleEditor.graph).write(fileDialog.file);
        } catch (IOException e) {
            e.printStackTrace();
            JErrorPane.invokeCustomErrorMessage("IO Error: " + e.getMessage());
            return;
        }
        JOptionPane.showMessageDialog(null,
            "Successfully exported current shuffle to file \"" + fileDialog.file.getAbsolutePath() + "\"",
            "Advanced Shuffle Editor", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) throws Exception {//GEN-FIRST:event_jList1ValueChanged
        // TODO add your handling code here:
        if (bypassEvents)
            return;
        int selection = jList1.getSelectedIndex();
        Distributions[] distributions = ArrayManager.getDistributions();
        if (selection >= 0 && selection < distributions.length)
            shuffleEditor.graph.addDisconnected(new ShuffleInfo(distributions[selection], false), 250, 250);
        shuffleEditor.repaint();
        bypassEvents = true;
        jList1.clearSelection();
        bypassEvents = false;
    }//GEN-LAST:event_jList1ValueChanged

    private void jList3ValueChanged(javax.swing.event.ListSelectionEvent evt) throws Exception {//GEN-FIRST:event_jList1ValueChanged
        // TODO add your handling code here:
        if (bypassEvents)
            return;
        int selection = jList3.getSelectedIndex();
        Distributions[] distributions = ArrayManager.getDistributions();
        if (selection >= 0 && selection < distributions.length)
            shuffleEditor.graph.addDisconnected(new ShuffleInfo(distributions[selection], true), 250, 250);
        shuffleEditor.repaint();
        bypassEvents = true;
        jList3.clearSelection();
        bypassEvents = false;
    }//GEN-LAST:event_jList1ValueChanged

    private void jList2ValueChanged(javax.swing.event.ListSelectionEvent evt) throws Exception {//GEN-FIRST:event_jList1ValueChanged
        // TODO add your handling code here:
        if (bypassEvents)
            return;
        int selection = jList2.getSelectedIndex();
        Shuffles[] shuffles = ArrayManager.getShuffles();
        if (selection >= 0 && selection < shuffles.length)
            shuffleEditor.graph.addDisconnected(new ShuffleInfo(shuffles[selection]), 250, 250);
        shuffleEditor.repaint();
        bypassEvents = true;
        jList2.clearSelection();
        bypassEvents = false;
    }//GEN-LAST:event_jList1ValueChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ShufflePanel shuffleEditor;

    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;

    @SuppressWarnings("rawtypes")
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jLabel1;

    @SuppressWarnings("rawtypes")
    private javax.swing.JList jList3;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel jLabel3;

    @SuppressWarnings("rawtypes")
    private javax.swing.JList jList2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}