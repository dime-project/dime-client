/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.view;

import eu.dime.model.ModelConfiguration;
import eu.dime.restapi.DimeHelper;
import eu.dime.view.helper.UIHelper;
import eu.dime.view.viewmodel.Configuration;
import eu.dime.view.viewmodel.ModelConfigurationHelper;
import eu.dime.view.viewmodel.PresetList;
import eu.dime.view.viewmodel.Settings;
import java.awt.Window;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author simon
 */
public class Configurator extends javax.swing.JDialog {
    
    private Settings settings; 
    private PresetList presets = new PresetList();
    
    /**
     * Creates new form Configurator
     * @param parent
     * @param modal  
     */
    public Configurator(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();        
        this.setTitle("di.me Model Viewer - Settings");
        //load settings
        settings = ModelConfigurationHelper.getStoredSettings();
        updateFormFromConfiguration(settings.getCurrentConfiguration());
        for (Configuration configuration: settings.getConfigurations()){
            presets.addElement(configuration);
        }        
        this.presetsList.setModel(presets);
        UIHelper.moveWindowToMousePosition((Window)this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        hostNameText = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        portText = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        userNameText = new javax.swing.JTextField();
        useHTTPSCheck = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        passwordText = new javax.swing.JTextField();
        accessRemoteCheckbox = new javax.swing.JCheckBox();
        enablePersistenceCheckbox = new javax.swing.JCheckBox();
        getNotificationsCheckbox = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        presetNameText = new javax.swing.JTextField();
        loadPresetText = new javax.swing.JButton();
        savePresetText = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        presetsList = new javax.swing.JList();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("di.me Configurator");

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        jLabel1.setText("HostName");
        hostNameText.setText("jTextField1");
        jLabel2.setText("Port");
        portText.setText("jTextField1");
        jLabel3.setText("UserName");
        userNameText.setText("Username");
        useHTTPSCheck.setText("use HTTPS");
        jLabel4.setText("Password");
        passwordText.setText("jTextField1");
        accessRemoteCheckbox.setText("access remote");
        accessRemoteCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                accessRemoteCheckboxActionPerformed(evt);
            }
        });
        enablePersistenceCheckbox.setText("enable persistence");
        getNotificationsCheckbox.setText("get notifications");
        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(userNameText)
                        .addComponent(hostNameText, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING))
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4)
                    .addComponent(jLabel2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(portText, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(accessRemoteCheckbox)
                            .addComponent(useHTTPSCheck)
                            .addComponent(enablePersistenceCheckbox)
                            .addComponent(getNotificationsCheckbox)))
                    .addComponent(passwordText))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hostNameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(portText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(useHTTPSCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(userNameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(passwordText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(accessRemoteCheckbox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(enablePersistenceCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(getNotificationsCheckbox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        loadPresetText.setText("load");
        loadPresetText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadPresetTextActionPerformed(evt);
            }
        });

        savePresetText.setText("save");
        savePresetText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePresetTextActionPerformed(evt);
            }
        });

        presetsList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        presetsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                presetsListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(presetsList);

        jLabel5.setText("Presets");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(loadPresetText, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(savePresetText, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(presetNameText)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(presetNameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loadPresetText)
                    .addComponent(savePresetText))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void accessRemoteCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_accessRemoteCheckboxActionPerformed
        toggleRemoteFieldsEnable(accessRemoteCheckbox.isSelected());
    }//GEN-LAST:event_accessRemoteCheckboxActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        updateSettingsFromForm();
        ModelConfigurationHelper.saveConfiguration(settings);

        this.dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void loadPresetTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadPresetTextActionPerformed
        Configuration selConf = (Configuration) presetsList.getSelectedValue();
        if (selConf==null){
            return;
        }
        updateFormFromConfiguration(selConf);
    }//GEN-LAST:event_loadPresetTextActionPerformed

    private void savePresetTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePresetTextActionPerformed
        if (presetNameText.getText().length()==0){
            return;
        }
        Configuration conf = getConfigFromForm(presetNameText.getText());
        presets.addOrReplaceElement(conf);
        
    }//GEN-LAST:event_savePresetTextActionPerformed

    private void lookupHostnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lookupHostnameActionPerformed
        if (userNameText.getText().length()==0){
            return;
        }
        //FIXME: hostNameText.setText(DimeHelper.resolveIPOfPS(userNameText.getText()));
    }//GEN-LAST:event_lookupHostnameActionPerformed

    private void presetsListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_presetsListMouseClicked
        Configuration preset = (Configuration)presetsList.getSelectedValue();
        if (preset==null){
            return;
        }
        
        presetNameText.setText(preset.getPresetName());
    }//GEN-LAST:event_presetsListMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox accessRemoteCheckbox;
    private javax.swing.JButton cancelButton;
    private javax.swing.JCheckBox enablePersistenceCheckbox;
    private javax.swing.JCheckBox getNotificationsCheckbox;
    private javax.swing.JTextField hostNameText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton loadPresetText;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField passwordText;
    private javax.swing.JTextField portText;
    private javax.swing.JTextField presetNameText;
    private javax.swing.JList presetsList;
    private javax.swing.JButton savePresetText;
    private javax.swing.JCheckBox useHTTPSCheck;
    private javax.swing.JTextField userNameText;
    // End of variables declaration//GEN-END:variables

     private void toggleRemoteFieldsEnable(boolean remoteActive) {
        hostNameText.setEnabled(remoteActive);
        portText.setEnabled(remoteActive);
        useHTTPSCheck.setEnabled(remoteActive);
        userNameText.setEnabled(remoteActive);
        passwordText.setEnabled(remoteActive);
        getNotificationsCheckbox.setEnabled(remoteActive);
    }
    
    private Configuration getConfigFromForm(String presetName){
        int port=DimeHelper.DEFAULT_PORT;
        try{
            port = Integer.parseInt(portText.getText());
        } catch (NumberFormatException ex){
            Logger.getLogger(Configurator.class.getName()).log(Level.SEVERE, "portText.getText() is not a number! - using default port");
        }
        Configuration result = new Configuration(presetName, new ModelConfiguration(
                hostNameText.getText(),
                port,
                useHTTPSCheck.isSelected(),
                userNameText.getText(),
                userNameText.getText(),
                passwordText.getText(),
                enablePersistenceCheckbox.isSelected(),
                accessRemoteCheckbox.isSelected(),
                getNotificationsCheckbox.isSelected()                
                ));
        return result;        
    }
     
    private void updateFormFromConfiguration(Configuration configuration){
        hostNameText.setText(configuration.getHostname());
        portText.setText(configuration.getPort()+"");
        useHTTPSCheck.setSelected(configuration.isUseHTTPS());
        userNameText.setText(configuration.getUsername());
        passwordText.setText(configuration.getPassword());
        accessRemoteCheckbox.setSelected(configuration.isAccessRemoteRestAPI());
        enablePersistenceCheckbox.setSelected(configuration.isPersistence());
        getNotificationsCheckbox.setSelected(configuration.isFetchNotifications());
        toggleRemoteFieldsEnable(accessRemoteCheckbox.isSelected());
    }
    
    public Settings getConfiguration() {
        return settings;
    }

    private void updateSettingsFromForm() {
        settings.setCurrentConfiguration(getConfigFromForm("default"));
        settings.setConfigurations(presets.elements());
    }
    
}
