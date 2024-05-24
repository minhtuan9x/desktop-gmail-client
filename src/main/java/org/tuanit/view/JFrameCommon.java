package org.tuanit.view;

import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.tuanit.constant.SystemConstant;
import org.tuanit.model.swing.JFrameSetup;
import org.tuanit.util.CFUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class JFrameCommon extends JFrame {
    @Autowired
    HomeView homeView;
    @Autowired
    LoadingView loadingView;

    public static JFrame currentFrame = null;

    protected JFrame getJFrame() {
        return this;
    }

    protected JMenuBar buildMenu(JFrame jFrame) {
        IconFontSwing.register(FontAwesome.getIconFont());
        JMenuBar menuBar = new JMenuBar();
        getRootPane().putClientProperty("JRootPane.titleBarBackground", new Color(52, 185, 219, 43));
        Font font1 = new Font("Serif", Font.BOLD, 15);
        menuBar.setFont(font1);
        ///
        JMenu menu = new JMenu("M̲enu");
        menu.setIcon(IconFontSwing.buildIcon(FontAwesome.BARS, 16, SystemConstant.BTN_COLOR));
        menu.add(buildSubItem("Thành viên nhóm", jFrame, FontAwesome.HAND_O_RIGHT, () -> homeView.showUI()));
        menu.addSeparator();
        //
        JMenu menuHome = new JMenu("H̲ome");
        menuHome.setIcon(IconFontSwing.buildIcon(FontAwesome.WINDOWS, 16, SystemConstant.BTN_COLOR));
        JMenuItem menuItemHome = new JMenuItem("Trang Chủ", IconFontSwing.buildIcon(FontAwesome.HOME, 16, SystemConstant.BTN_COLOR));
        menuItemHome.addActionListener(e -> {
            jFrame.setVisible(false);
            homeView.showUI();
        });
        //menuHome.add(menuItemHome);
        //menuHome.addSeparator();
        //menuHome.add(buildSubItem("Quản lí account ", jFrame, FontAwesome.USERS, () -> Context.getInstance(ProfileSetupView.class).showUI()));

        //
        JMenu menuOptions = new JMenu("O̲ptions");
        //menuOptions.setIcon(IconFontSwing.buildIcon(FontAwesome.COGS, 16, SystemConstant.BTN_COLOR));
        //buildSetting(menuOptions);
        //buildSettingMenu(menuOptions);
        //
        JMenu menuAbout = new JMenu("A̲bout");
        //menuAbout.setIcon(IconFontSwing.buildIcon(FontAwesome.INFO_CIRCLE, 16, SystemConstant.BTN_COLOR));
        //menuAbout.add(buildSubItem("Đăng xuất", jFrame, FontAwesome.SIGN_OUT, () -> Context.getInstance(HelloUserView.class).signOut()));
        //menuAbout.addSeparator();
        //menuAbout.add(buildItemUpgrade("Cập nhật phiên bản mới"));
        //menuAbout.addSeparator();
        //menuAbout.add(buildSubItem("Logs", jFrame, FontAwesome.BUILDING, () -> Context.getInstance(LogsView.class).showUI()));
        //menuAbout.addSeparator();
        menuAbout.add(buildSubItem("Về chúng tôi", null, FontAwesome.GLOBE, () -> {
            try {
                Runtime rt = Runtime.getRuntime();
                String url = "https://google.com";
                rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }));

        menuBar.add(menuHome);
        menuBar.add(menu);
        //menuBar.add(menuTools);
        menuBar.add(menuOptions);
        menuBar.add(menuAbout);
        return menuBar;
    }


    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        JComboBox<?> comboBox = clearJComboBoxListener();
        if (comboBox != null) {
            if (comboBox.getItemListeners().length > 1) {
                comboBox.removeItemListener(comboBox.getItemListeners()[1]);
            }
        }
    }

    protected JComboBox<?> clearJComboBoxListener() {
        return null;
    }

    private JMenuItem buildSubItem(String title, JFrame jFrame, IconCode iconCode, Runnable runnable) {
        IconFontSwing.register(FontAwesome.getIconFont());
        JMenuItem menuItem = new JMenuItem(title, IconFontSwing.buildIcon(iconCode, 16, SystemConstant.BTN_COLOR));
        menuItem.addActionListener(e -> {
            if (jFrame != null) {
                jFrame.setVisible(false);
            }
            loadingView.loading(null, CFUtils.runAsync(runnable));
        });
        return menuItem;
    }

    //private void buildSettingMenu(JMenu settingOptions) {
    //    JMenu subMenu = new JMenu("Cài đặt quãng nghỉ");
    //    subMenu.add(buildSleepingTime());
    //    subMenu.addSeparator();
    //    subMenu.add(buildCountToTime());
    //    settingOptions.add(subMenu);
    //}
    //
    //
    //private JMenu buildSleepingTime() {
    //    JMenu subMenu = new JMenu("Thời gian nghỉ");
    //    JRadioButtonMenuItem jRadioButtonMenuItem0 = new JRadioButtonMenuItem("Tắt");
    //    JRadioButtonMenuItem jRadioButtonMenuItem5 = new JRadioButtonMenuItem("5 phút");
    //    JRadioButtonMenuItem jRadioButtonMenuItem10 = new JRadioButtonMenuItem("10 phút");
    //    JRadioButtonMenuItem jRadioButtonMenuItem15 = new JRadioButtonMenuItem("15 phút");
    //    ButtonGroup bg = new ButtonGroup();
    //    bg.add(jRadioButtonMenuItem0);
    //    bg.add(jRadioButtonMenuItem5);
    //    bg.add(jRadioButtonMenuItem10);
    //    bg.add(jRadioButtonMenuItem15);
    //    subMenu.add(jRadioButtonMenuItem0);
    //    subMenu.add(jRadioButtonMenuItem5);
    //    subMenu.add(jRadioButtonMenuItem10);
    //    subMenu.add(jRadioButtonMenuItem15);
    //
    //    // Add ItemListener to each radio button
    //    ItemListener itemListener = e -> {
    //        if (e.getStateChange() == ItemEvent.SELECTED) {
    //            SettingModel settingModel = Helper.getObjectOptionally(SettingModel.class).orElse(new SettingModel());
    //            JRadioButtonMenuItem selectedMenuItem = (JRadioButtonMenuItem) e.getSource();
    //            String selectedText = selectedMenuItem.getText();
    //            System.out.println("Selected: " + selectedText);
    //
    //            switch (selectedText) {
    //                case "Tắt":
    //                    settingModel.setSleepMinutes(0);
    //                    break;
    //                case "5 phút":
    //                    settingModel.setSleepMinutes(5);
    //                    break;
    //                case "10 phút":
    //                    settingModel.setSleepMinutes(10);
    //                    break;
    //                case "15 phút":
    //                    settingModel.setSleepMinutes(15);
    //                    break;
    //            }
    //            Helper.writeFile(settingModel);
    //        }
    //    };
    //
    //    SettingModel settingModel = Helper.getObjectOptionally(SettingModel.class).orElse(new SettingModel());
    //    if (settingModel.getSleepMinutes() == null) {
    //        settingModel.setSleepMinutes(5);
    //    }
    //    switch (settingModel.getSleepMinutes()) {
    //        case 0:
    //            bg.setSelected(jRadioButtonMenuItem0.getModel(), true);
    //            break;
    //        case 5:
    //            bg.setSelected(jRadioButtonMenuItem5.getModel(), true);
    //            break;
    //        case 10:
    //            bg.setSelected(jRadioButtonMenuItem10.getModel(), true);
    //            break;
    //        case 15:
    //            bg.setSelected(jRadioButtonMenuItem15.getModel(), true);
    //            break;
    //    }
    //
    //    jRadioButtonMenuItem0.addItemListener(itemListener);
    //    jRadioButtonMenuItem5.addItemListener(itemListener);
    //    jRadioButtonMenuItem10.addItemListener(itemListener);
    //    jRadioButtonMenuItem15.addItemListener(itemListener);
    //    return subMenu;
    //}

    ////private JMenu buildCountToTime() {
    ////    JMenu subMenu = new JMenu("Số tin nhắn");
    ////    JRadioButtonMenuItem jRadioButtonMenuItem5 = new JRadioButtonMenuItem("5 tin nhắn");
    ////    JRadioButtonMenuItem jRadioButtonMenuItem10 = new JRadioButtonMenuItem("10 tin nhắn");
    ////    JRadioButtonMenuItem jRadioButtonMenuItem15 = new JRadioButtonMenuItem("15 tin nhắn");
    ////    JRadioButtonMenuItem jRadioButtonMenuItem20 = new JRadioButtonMenuItem("20 tin nhắn");
    ////    ButtonGroup bg = new ButtonGroup();
    ////    bg.add(jRadioButtonMenuItem5);
    ////    bg.add(jRadioButtonMenuItem10);
    ////    bg.add(jRadioButtonMenuItem15);
    ////    bg.add(jRadioButtonMenuItem20);
    ////    subMenu.add(jRadioButtonMenuItem5);
    ////    subMenu.add(jRadioButtonMenuItem10);
    ////    subMenu.add(jRadioButtonMenuItem15);
    ////    subMenu.add(jRadioButtonMenuItem20);
    ////
    ////    // Add ItemListener to each radio button
    ////    ItemListener itemListener = e -> {
    ////        if (e.getStateChange() == ItemEvent.SELECTED) {
    ////            SettingModel settingModel = Helper.getObjectOptionally(SettingModel.class).orElse(new SettingModel());
    ////            JRadioButtonMenuItem selectedMenuItem = (JRadioButtonMenuItem) e.getSource();
    ////            String selectedText = selectedMenuItem.getText();
    ////            System.out.println("Selected: " + selectedText);
    ////
    ////            switch (selectedText) {
    ////                case "5 tin nhắn":
    ////                    settingModel.setCountToSleep(5);
    ////                    break;
    ////                case "10 tin nhắn":
    ////                    settingModel.setCountToSleep(10);
    ////                    break;
    ////                case "15 tin nhắn":
    ////                    settingModel.setCountToSleep(15);
    ////                    break;
    ////                case "20 tin nhắn":
    ////                    settingModel.setCountToSleep(20);
    ////                    break;
    ////            }
    ////            Helper.writeFile(settingModel);
    ////        }
    ////    };
    ////
    ////    SettingModel settingModel = Helper.getObjectOptionally(SettingModel.class).orElse(new SettingModel());
    ////    if (settingModel.getCountToSleep() == null) {
    ////        settingModel.setCountToSleep(10);
    ////    }
    ////    switch (settingModel.getCountToSleep()) {
    ////        case 5:
    ////            bg.setSelected(jRadioButtonMenuItem5.getModel(), true);
    ////            break;
    ////        case 10:
    ////            bg.setSelected(jRadioButtonMenuItem10.getModel(), true);
    ////            break;
    ////        case 15:
    ////            bg.setSelected(jRadioButtonMenuItem15.getModel(), true);
    ////            break;
    ////        case 20:
    ////            bg.setSelected(jRadioButtonMenuItem20.getModel(), true);
    ////            break;
    ////    }
    ////
    ////    jRadioButtonMenuItem5.addItemListener(itemListener);
    ////    jRadioButtonMenuItem10.addItemListener(itemListener);
    ////    jRadioButtonMenuItem15.addItemListener(itemListener);
    ////    jRadioButtonMenuItem20.addItemListener(itemListener);
    ////    return subMenu;
    ////}
    //
    //private JMenuItem buildItemUpgrade(String title) {
    //    JMenuItem menuItem = new JMenuItem(title, IconFontSwing.buildIcon(FontAwesome.UPLOAD, 16, SystemConstant.BTN_COLOR));
    //
    //
    //    menuItem.addActionListener(e -> {
    //        CFUtils.runAsync(this::updateCore);
    //    });
    //
    //    return menuItem;
    //}
    //
    //protected void updateCore() {
    //    try {
    //        String jarName = "zalo-tools.exe";
    //        String jarNameBackup = "zalo-tools-backup.exe";
    //        CommonService commonService = Context.getInstance(CommonService.class);
    //
    //        List<String> libFiles = new ArrayList<>();
    //        try {
    //            File fileLib = new File(Helper.path() + "/libs");
    //            libFiles.addAll(FileUtils.listFiles(fileLib, null, false).stream().map(File::getName).filter(StringUtils::isNotBlank).collect(Collectors.toList()));
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //
    //        BufferedInputStream in = new BufferedInputStream(
    //                new URL(UrlConstant.URL_ENUM.getGithub() + "/library.txt")
    //                        .openStream());
    //        String value = new String(ByteStreams.toByteArray(in), Charsets.UTF_8);
    //        in.close();
    //        List<String> libsFromServer = new ArrayList<>(List.of(value.split(","))).stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
    //        String url = "";
    //        int updateLength = 0;
    //        boolean isContain = true;
    //        for (String item : new HashSet<>(libsFromServer)) {
    //            if (!libFiles.contains(item)) {
    //                isContain = false;
    //                break;
    //            }
    //        }
    //        if (isContain) {
    //            updateLength = 930000;
    //            url = UrlConstant.URL_ENUM.getLiteUpdate();
    //        } else {
    //            updateLength = SystemConstant.UPDATED_LENGTH;
    //            url = UrlConstant.URL_ENUM.getFullUpdate();
    //        }
    //        File fileFetched = new File(Helper.path() + "/" + jarName);
    //        File fileBackup = new File(Helper.path() + "/" + jarNameBackup);
    //        try {
    //            FileUtils.copyFile(fileFetched, fileBackup);
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //        commonService.downloadZipFile(url, this, zipFile -> {
    //            try {
    //                zipFile.extractAll(Helper.path());
    //
    //                Helper.alert("Cập nhật thành công, Enjoy!!");
    //                try {
    //                    if (SocketListener.SERVER_SOCKET != null)
    //                        SocketListener.SERVER_SOCKET.close();
    //                } catch (Exception es) {
    //                    es.printStackTrace();
    //                }
    //                Desktop.getDesktop().open(fileFetched);
    //                CFUtils.runAsync(() -> {
    //                    Helper.sleep(1000);
    //                    System.exit(0);
    //                });
    //            } catch (Exception ex) {
    //                throw new RuntimeException(ex);
    //            }
    //        });
    //    } catch (IOException e) {
    //        e.printStackTrace();
    //        Helper.alert("Cập nhập thất bại, lỗi: {}", e.getMessage());
    //    }
    //}


    private void buildSetting(JMenu jMenu) {
//        JCheckBoxMenuItem jCheckBoxShowChrome = new JCheckBoxMenuItem("Hiện chrome");
//        SettingModel settingModel = Helper.getObjectOptionally(SettingModel.class).orElse(new SettingModel());
//        jCheckBoxShowChrome.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                AbstractButton aButton = (AbstractButton) e.getSource();
//                boolean selected = aButton.getModel().isSelected();
//                if (selected) {
//                    settingModel.setShowChrome(true);
//                    Helper.writeFile(settingModel);
//                } else {
//                    settingModel.setShowChrome(false);
//                    Helper.writeFile(settingModel);
//                }
//            }
//        });
//        jMenu.add(jCheckBoxShowChrome);
//        jCheckBoxShowChrome.setSelected(settingModel.isShowChrome());
        ////
//        jMenu.addSeparator();
//        JCheckBoxMenuItem jCheckBoxAutoUpdate = new JCheckBoxMenuItem("Tự động cập nhật");
//        jCheckBoxAutoUpdate.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                AbstractButton aButton = (AbstractButton) e.getSource();
//                boolean selected = aButton.getModel().isSelected();
//                if (selected) {
//                    settingModel.setAutoUpdate(true);
//                    Helper.writeFile(settingModel);
//                } else {
//                    settingModel.setAutoUpdate(false);
//                    Helper.writeFile(settingModel);
//                }
//            }
//        });
//        jMenu.add(jCheckBoxAutoUpdate);
//        jCheckBoxAutoUpdate.setSelected(settingModel.isAutoUpdate());
    }

    @SneakyThrows
    protected void commonSetup(JFrame jFrame) {
        setResizable(false);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        //setDefaultCloseOperation(0);
        hide(jFrame);
        setJMenuBar(buildMenu(jFrame));
        //setIconImage(ImageIO.read(getClass().getResource(UrlConstant.URL_ENUM.getIcon())));
        jFrame.setVisible(true);
    }

    protected void hide(JFrame frame) {
        for (WindowListener windowListener : frame.getWindowListeners()) {
            frame.removeWindowListener(windowListener);
        }
        currentFrame = this;
        if (SystemTray.isSupported()) {
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    Object[] options = {"Yes", "Thu nhỏ"};
                    int choosing = JOptionPane.showOptionDialog(frame,
                            "Bạn có muốn thoát", "Close Window?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
                    if (choosing == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
                }
            });
        }
        SystemTray systemTray = SystemTray.getSystemTray();
        // Check if the tray icon is already added
        TrayIcon[] trayIcons = systemTray.getTrayIcons();
        for (TrayIcon icon : trayIcons) {
            if (icon.getActionCommand().equals("YourApp")) {
                return; // Tray icon already added, exit early
            }
        }

        //Image originalImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource(UrlConstant.URL_ENUM.getIcon()));
        //Image scaledImage = originalImage.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        //TrayIcon trayIcon = new TrayIcon(scaledImage);
        //trayIcon.setActionCommand("YourApp");
        PopupMenu popupMenu = new PopupMenu();

        MenuItem show = new MenuItem("Show");
        show.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentFrame.setVisible(true);
            }
        });

        MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        popupMenu.add(show);
        popupMenu.add(exit);

        //trayIcon.setPopupMenu(popupMenu);

        try {
            //systemTray.add(trayIcon);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showConfirmDialog(null, e.getMessage(), "Error", JOptionPane.DEFAULT_OPTION);
        }
    }

    public abstract void setup(JFrameSetup jFrameSetup);

    public void showUI() {
        JFrameSetup jFrameSetup = new JFrameSetup();
        setup(jFrameSetup);
        setTitle(jFrameSetup.getTitle() + " - v" + SystemConstant.VERSION);
        setContentPane(jFrameSetup.getJPane());
        commonSetup(getJFrame());
    }


}
