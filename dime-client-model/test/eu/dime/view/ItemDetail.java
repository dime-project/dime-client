/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.view;

import eu.dime.model.displayable.AgentItem;
import eu.dime.model.GenItem;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.SharingNotSupportedForSAIDException;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.ProfileAttributeItem;
import eu.dime.model.displayable.ProfileItem;
import eu.dime.view.helper.UIHelper;
import eu.dime.view.viewmodel.ItemDetailItemsListModel;
import eu.dime.view.viewmodel.TypeSpecificFieldsListModel;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author simon
 */
public class ItemDetail extends javax.swing.JFrame {

    private Vector<GenItem> items = new Vector();
    private int itemShowIndex = 0;
    private final ModelViewer mv;

    /**
     * Creates new form ItemDetail
     * @param mv 
     */
    public ItemDetail(ModelViewer mv) {
        this.mv = mv;
        initComponents();

        UIHelper.moveWindowToMousePosition(this);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        nameText = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        guidText = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        typeText = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        itemsList = new javax.swing.JList();
        jLabel6 = new javax.swing.JLabel();
        imageUrlLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        prevButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();
        showJSONButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        shareButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        typeSpecificFieldsTable = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        addItemButton = new javax.swing.JButton();
        removeItemButton = new javax.swing.JButton();
        editValuesButton = new javax.swing.JButton();
        genSaidButton = new javax.swing.JButton();

        jLabel1.setText("Name");

        nameText.setEditable(false);
        nameText.setText("jTextField1");

        jLabel2.setText("GUID");

        guidText.setText("jTextField1");

        jLabel3.setText("Items");

        jLabel4.setText("Type");

        typeText.setEditable(false);
        typeText.setText("jTextField2");

        itemsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                itemsListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(itemsList);

        jLabel6.setText("ImageUrl");

        imageUrlLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imageUrlLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        imageUrlLabel.setIconTextGap(0);
        imageUrlLabel.setMaximumSize(new java.awt.Dimension(100, 100));
        imageUrlLabel.setMinimumSize(new java.awt.Dimension(100, 100));
        imageUrlLabel.setPreferredSize(new java.awt.Dimension(100, 100));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameText, javax.swing.GroupLayout.DEFAULT_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(guidText, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                    .addComponent(typeText)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel2)
                            .addComponent(jLabel6))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(imageUrlLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(guidText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(typeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(imageUrlLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        prevButton.setText("prev");
        prevButton.setEnabled(false);
        prevButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevButtonActionPerformed(evt);
            }
        });

        nextButton.setText("next");
        nextButton.setEnabled(false);
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(prevButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nextButton)
                .addGap(6, 6, 6))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nextButton, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(prevButton, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        closeButton.setText("close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        showJSONButton.setText("show JSON");
        showJSONButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showJSONButtonActionPerformed(evt);
            }
        });

        updateButton.setText("save");
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        deleteButton.setText("delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        shareButton.setText("share");
        shareButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shareButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(shareButton)
                .addGap(18, 18, 18)
                .addComponent(showJSONButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(closeButton)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(updateButton)
                        .addComponent(deleteButton)
                        .addComponent(shareButton)
                        .addComponent(showJSONButton))
                    .addComponent(closeButton))
                .addContainerGap())
        );

        jLabel5.setText("Type specific fields");

        typeSpecificFieldsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        typeSpecificFieldsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                typeSpecificFieldsTableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(typeSpecificFieldsTable);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                .addContainerGap())
        );

        addItemButton.setText("add");
        addItemButton.setEnabled(false);
        addItemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addItemButtonActionPerformed(evt);
            }
        });

        removeItemButton.setText("remove");
        removeItemButton.setEnabled(false);
        removeItemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeItemButtonActionPerformed(evt);
            }
        });

        editValuesButton.setText("edit");
        editValuesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editValuesButtonActionPerformed(evt);
            }
        });

        genSaidButton.setText("gen SAID");
        genSaidButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genSaidButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(addItemButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeItemButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editValuesButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(genSaidButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addItemButton)
                    .addComponent(removeItemButton)
                    .addComponent(editValuesButton)
                    .addComponent(genSaidButton))
                .addGap(0, 12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_closeButtonActionPerformed

    private void showJSONButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showJSONButtonActionPerformed
        if (items.isEmpty()) {
            return;
        }
        JOptionPane.showMessageDialog(this, items.lastElement().getJSONObject().toString());
    }//GEN-LAST:event_showJSONButtonActionPerformed

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        if (itemShowIndex >= items.size() - 1) {
            return;
        }
        itemShowIndex++;
        showCurrIndexItem();
    }//GEN-LAST:event_nextButtonActionPerformed

    private void prevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevButtonActionPerformed
        if (itemShowIndex == 0) {
            return;
        }
        itemShowIndex--;
        showCurrIndexItem();

    }//GEN-LAST:event_prevButtonActionPerformed

    private void itemsListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_itemsListMouseClicked
       
        if (evt.getClickCount()<2){ //only handle double click to allow for mouse selection with single click
            return;
        }
        
        String itemId = (String) itemsList.getSelectedValue();
        if (itemId == null) {
            return;
        }
        GenItem curItem = getCurItem();

        TYPES childType = ModelHelper.getChildType(curItem.getMType());


        GenItem item = Model.getInstance().getItem(mv.getMRC(), childType, itemId);
        showNewItem(item);
    }//GEN-LAST:event_itemsListMouseClicked

    private void typeSpecificFieldsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_typeSpecificFieldsTableMouseClicked
        if (evt.getClickCount() < 2) {
            return;
        }

        int selRow = typeSpecificFieldsTable.getSelectedRow();
        if (selRow == -1) {
            return;
        }
        TextView tw = new TextView(this, false);
        tw.setText(typeSpecificFieldsTable.getModel().getValueAt(selRow, 2).toString());
        tw.setVisible(true);
    }//GEN-LAST:event_typeSpecificFieldsTableMouseClicked

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        
        GenItem item = getCurItem();        
        Model.getInstance().removeItem(mv.getMRC(), item.getGuid(), item.getMType());
        
        //show previous item in the list
        items.remove(items.size()-1);
        itemShowIndex--;
        if (items.size()>0){
            showCurrIndexItem();
        }else{
            this.setVisible(false);                    
        }
        
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        GenItem item = getCurItem();        
        Model.getInstance().updateItem(mv.getMRC(), item);
        showCurrIndexItem();
    }//GEN-LAST:event_updateButtonActionPerformed

    private void addItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addItemButtonActionPerformed
        
        DisplayableItem item;
        
        try {
            item = (DisplayableItem) getCurItem();
        } catch (ClassCastException ex) {
            Logger.getLogger(ItemDetail.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return;
        }     
        
        TYPES childType = ModelHelper.getChildType(item.getMType());
        
        List<DisplayableItem> childItems = ModelHelper.getAllDisplayableItems(mv.getMRC(), childType);
        ModelHelper.sortItemsByName(childItems);        
        
        ItemSelector itemSelector = new ItemSelector(this, true, 
                childItems);
        
        itemSelector.setVisible(true);
        
        DisplayableItem selectedItem = itemSelector.getSelectedItem();        
        if ((selectedItem!=null) && (!item.containsItem(selectedItem.getGuid()))){
            item.addItem(selectedItem.getGuid());
        }
                
        showCurrIndexItem();
                
    }//GEN-LAST:event_addItemButtonActionPerformed

    private void removeItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeItemButtonActionPerformed
        DisplayableItem item;
        
        try {
            item = (DisplayableItem) getCurItem();
        } catch (ClassCastException ex) {
            Logger.getLogger(ItemDetail.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return;
        }  
        
        String itemId = (String) itemsList.getSelectedValue();
        if (itemId == null) {
            return;
        }
        
        item.removeItem(itemId);
        
        showCurrIndexItem();
    }//GEN-LAST:event_removeItemButtonActionPerformed

    private void shareButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shareButtonActionPerformed
        try {
            GenItem item = getCurItem();
            ModelRequestContext myMRC = mv.getMRC();
            
            if (!ModelHelper.isShareable(item.getMType())){
                JOptionPane.showMessageDialog(this, "Item cannot be shared!");
                return;
            }
            List<DisplayableItem> agents = ModelHelper.getAllAgentsAsDisplayable(myMRC);
            ItemSelector itemSelector = new ItemSelector(this, true, agents);
            itemSelector.setVisible(true);
            
            String saidSender = ModelHelper.getDefaultSenderSaid(myMRC);
            if ((saidSender==null)||(saidSender.length()==0)){
                JOptionPane.showMessageDialog(this, "Cannot share item, since owner("
                        +myMRC.owner+") doesnt have a profile at hoster:"+myMRC.hoster);
                return;
            }
            
            ModelHelper.shareItemToAgent(myMRC, item, (AgentItem)itemSelector.getSelectedItem()
                    , saidSender); //TODO - add profile selector
            
            updateCurItemFromModelAndShow();
        } catch (SharingNotSupportedForSAIDException ex) {
            Logger.getLogger(ItemDetail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_shareButtonActionPerformed

    private void editValuesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editValuesButtonActionPerformed
        ProfileAttributeItem item = (ProfileAttributeItem) getCurItem().getClone();
        
        ProfileAttrEditor profAttrEdit = new ProfileAttrEditor(mv, true, item);
        profAttrEdit.setVisible(true);
        updateCurItemFromModelAndShow();        
    }//GEN-LAST:event_editValuesButtonActionPerformed

    private void genSaidButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genSaidButtonActionPerformed
        ProfileItem item = (ProfileItem) getCurItem();
        item.setServiceAccountId(UUID.randomUUID().toString());
        Model.getInstance().updateItem(mv.getMRC(), item);
        updateCurItemFromModelAndShow();
    }//GEN-LAST:event_genSaidButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addItemButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton editValuesButton;
    private javax.swing.JButton genSaidButton;
    private javax.swing.JTextField guidText;
    private javax.swing.JLabel imageUrlLabel;
    private javax.swing.JList itemsList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField nameText;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton prevButton;
    private javax.swing.JButton removeItemButton;
    private javax.swing.JButton shareButton;
    private javax.swing.JButton showJSONButton;
    private javax.swing.JTable typeSpecificFieldsTable;
    private javax.swing.JTextField typeText;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables

    private void showCurrIndexItem() {
        GenItem item = getCurItem();

        this.setTitle("di.me detail: " + item.getGuid());
        guidText.setText(item.getGuid());
        typeText.setText(item.getType());

        if (ModelHelper.isDisplayableItem(item.getMType())) {
            DisplayableItem dItem = (DisplayableItem) item;
            nameText.setText(dItem.getName());
            imageUrlLabel.setText(dItem.getImageUrl());
            imageUrlLabel.setIcon(null);
            
            if (dItem.getImageUrl().length()!=0){
                try{
                    URL imageUrl = new URL(
                            ModelHelper.guessURLString(dItem.getImageUrl()));
                    imageUrlLabel.setIcon(new ImageIcon(imageUrl));
                    imageUrlLabel.setText(null);
                }catch (Exception ex){
                    Logger.getLogger(ItemDetail.class.getName()).log(Level.SEVERE, ex.getMessage());
                }
            }           
            
            itemsList.setModel(new ItemDetailItemsListModel(dItem.getItems()));
            typeSpecificFieldsTable.setModel(new TypeSpecificFieldsListModel(item));
            
            if (ModelHelper.isParentable(item)){
                addItemButton.setEnabled(true);
                removeItemButton.setEnabled(!dItem.getItems().isEmpty());
            }else{
                addItemButton.setEnabled(false);
                removeItemButton.setEnabled(false);
            }
            
            if (item.getMType()==TYPES.PROFILEATTRIBUTE){
                editValuesButton.setVisible(true);
            }else{
                editValuesButton.setVisible(false);
            }
            if (item.getMType()==TYPES.PROFILE){
                genSaidButton.setVisible(true);
            }else{
                genSaidButton.setVisible(false);
            }
            

        } else {
            nameText.setText("");
            imageUrlLabel.setText("");
            imageUrlLabel.setIcon(null);
            itemsList.setModel(new ItemDetailItemsListModel());
            typeSpecificFieldsTable.setModel(new TypeSpecificFieldsListModel());
        }

        prevButton.setEnabled(itemShowIndex != 0);
        nextButton.setEnabled(itemShowIndex < items.size() - 1);
    }

    public void showNewItem(GenItem item) {

        //cut right part of the history
        while (itemShowIndex < (items.size() - 1)) {
            items.remove(itemShowIndex + 1);
        }

        this.items.add(item);
        itemShowIndex = items.size() - 1;
        showCurrIndexItem();

    }
    
    private GenItem getCurItem(){
        return items.get(itemShowIndex);
    }

    private void updateCurItemFromModelAndShow() {
        GenItem item = items.remove(itemShowIndex);
        if (item==null) {
            return;
        }
        GenItem newItem = Model.getInstance().getItem(mv.getMRC(), item.getMType(), item.getGuid());
        if (newItem==null){
            itemShowIndex--;
            JOptionPane.showMessageDialog(this, "Unable to reload current item from model. Item was removed?");            
        }else{
            items.add(itemShowIndex, newItem);
        }
        
        showCurrIndexItem(); 
        this.toFront();
    }
}
