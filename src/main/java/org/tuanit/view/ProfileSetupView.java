package org.tuanit.view;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.gmail.model.Profile;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import jiconfont.icons.font_awesome.FontAwesome;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tuanit.model.ProfileModel;
import org.tuanit.model.swing.JFrameSetup;
import org.tuanit.service.AccountService;
import org.tuanit.service.ExecutingService;
import org.tuanit.service.ProfileService;
import org.tuanit.util.CFUtils;
import org.tuanit.util.Helper;
import org.tuanit.util.SwingUtils;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
public class ProfileSetupView extends JFrameCommon {
    private JTextField txtProfile;
    private JButton btnThem;
    private JButton btnXoa;
    private JPanel jpane;
    private JButton btnReload;
    private JTable tableProfile;
    private JButton btnOpen;
    private JPanel pane2;
    private JButton btnHttpProxy;
    private JButton btnRemoveProxy;
    private JButton btnRename;
    private JButton btnNickname;
    private DefaultTableModel defaultTableModel;
    private JDialog jDialog = new JDialog();
    private String globalName;

    @Autowired
    AccountService accountService;
    @Autowired
    ProfileService profileService;


    public ProfileSetupView() {
        SwingUtils.setIconBtn(btnHttpProxy, FontAwesome.CODE_FORK);
        SwingUtils.setIconBtn(btnRemoveProxy, FontAwesome.DEAF);
        SwingUtils.setIconBtn(btnReload, FontAwesome.SPINNER);
        SwingUtils.setIconBtn(btnThem, FontAwesome.PLUS_SQUARE_O);
        SwingUtils.setIconBtn(btnNickname, FontAwesome.SNAPCHAT);
        SwingUtils.setIconBtn(btnXoa, FontAwesome.TRASH);

        btnReload.addActionListener(e -> {
            getTable();
        });

        btnThem.addActionListener(e -> {
            String profile = txtProfile.getText();
            Credential credential = accountService.getCredentials(profile);
            profileService.addProfile(profile);
            getTable();
        });
        btnNickname.addActionListener(e -> {
            String nickname = JOptionPane.showInputDialog("Nhập nickname?");
            String profile = txtProfile.getText();
            profileService.changeNickname(profile, nickname);
            getTable();
        });
    }

    @Override
    public void setup(JFrameSetup jFrameSetup) {
        jFrameSetup.jPane(jpane).title("Quản lí account");
        tableListener();
        getTableInit();
//        profileService.buildDataTable(defaultTableModel);
    }

    private void tableListener() {
        tableProfile.getSelectionModel().addListSelectionListener(e -> {
            try {
                String profile = tableProfile.getValueAt(tableProfile.getSelectedRow(), 0).toString();
                txtProfile.setText(profile);
                globalName = profile;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

//    private void changeProfileName(String newProfile) {
//        try {
//            Runtime.getRuntime().exec("taskkill /im chrometuan.exe");
//        } catch (IOException e) {
//            e.printStackTrace();
//            Helper.alert("Error while trying to kill the process: " + e.getMessage());
//            return;
//        }
//
//        List<String> profiles = profileService.getModels();
//        if (profiles.contains(newProfile.trim())) {
//            Helper.alert("Profile đã tồn tại!");
//            return;
//        }
//        for (String profile : profiles) {
//            if (profile.equals(globalName)) {
//                String folderProfile = Helper.path() + "/toolbanhangdata/profile";
//                File oldFile = new File(folderProfile, "Profile-" + globalName);
//                File newFile = new File(folderProfile, "Profile-" + newProfile);
//
//                if (!oldFile.exists()) {
//                    Helper.alert("File " + globalName + " not found in the profile directory.");
//                    return;
//                }
//
//                if (!oldFile.renameTo(newFile)) {
//                    Helper.alert("Failed to rename profile " + globalName + " to " + newProfile);
//                    return;
//                }
//
////                ProxyModel proxyModel = proxyService.getProxyInfo(globalName);
////                if (proxyModel != null) {
////                    proxyService.delete(globalName);
////                    proxyModel.setProfile(newProfile);
////                    proxyService.addToListForce(proxyModel);
////                }
////
////                CredentialApi credentialApi = profileService.getCredentialApi(globalName);
////                if (credentialApi != null) {
////                    profileService.deleteCredentialApi(globalName);
////                    credentialApi.setProfile(newProfile);
////                    profileService.saveCredential(credentialApi);
////                }
//
//                getTable();
//                SwingUtils.alert("Đổi tên thành công!!!");
//                return;
//            }
//        }
//        SwingUtils.alert("Không tìm thấy profile để đổi tên.");
//    }


    private void buildTable() {
        defaultTableModel = new DefaultTableModel();
        defaultTableModel.addColumn("Profile");
        defaultTableModel.addColumn("Email");
        defaultTableModel.addColumn("Nickname");
//        defaultTableModel.addColumn("Proxy");
        tableProfile.setModel(defaultTableModel);
        tableProfile.setAutoCreateRowSorter(true);
    }


    public void getTable() {
        buildTable();
        loadingView.loading(this, CFUtils.runAsync(() -> {
            for (ProfileModel profile : profileService.getAllProfile()) {
                Credential credential = accountService.getCredentials(profile.getProfile());
                Profile person = accountService.getGmailProfile(credential);
                defaultTableModel.addRow(new String[]{profile.getProfile(), person.getEmailAddress(), profile.getNickname()});
            }
        }));
    }

    private void getTableInit() {
        buildTable();
        for (ProfileModel profile : profileService.getAllProfile()) {
            Credential credential = accountService.getCredentials(profile.getProfile());
            Profile person = accountService.getGmailProfile(credential);
            defaultTableModel.addRow(new String[]{profile.getProfile(), person.getEmailAddress(), profile.getNickname()});
        }
    }

//    public void reload() {
//        buildTable();
//        loadingView.loading(this, CFUtils.runAsync(() -> {
//            profileService.reloadTable(defaultTableModel);
//            tableProfile.setModel(defaultTableModel);
//        }));
//    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        jpane = new JPanel();
        jpane.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        jpane.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Profile");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtProfile = new JTextField();
        panel1.add(txtProfile, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        pane2 = new JPanel();
        pane2.setLayout(new GridLayoutManager(9, 1, new Insets(0, 0, 0, 0), -1, -1));
        jpane.add(pane2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 1, false));
        btnThem = new JButton();
        btnThem.setText("Thêm");
        pane2.add(btnThem, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        pane2.add(spacer1, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        btnXoa = new JButton();
        btnXoa.setText("Xoá");
        pane2.add(btnXoa, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnReload = new JButton();
        btnReload.setText("Kiểm tra trạng thái đăng nhập");
        pane2.add(btnReload, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        pane2.add(spacer2, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        pane2.add(spacer3, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        btnHttpProxy = new JButton();
        btnHttpProxy.setText("Set Http Proxy");
        pane2.add(btnHttpProxy, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnRemoveProxy = new JButton();
        btnRemoveProxy.setText("Gỡ proxy");
        pane2.add(btnRemoveProxy, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnNickname = new JButton();
        btnNickname.setText("Set Nickname");
        pane2.add(btnNickname, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        jpane.add(scrollPane1, new GridConstraints(0, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tableProfile = new JTable();
        scrollPane1.setViewportView(tableProfile);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return jpane;
    }

}
