package org.tuanit.view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import jiconfont.icons.font_awesome.FontAwesome;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.tuanit.annotation.excel.export.ExportExcel;
import org.tuanit.enums.ExecuteStatusEnum;
import org.tuanit.model.swing.JFrameSetup;
import org.tuanit.service.ExecutingService;
import org.tuanit.util.CFUtils;
import org.tuanit.util.ExcelUtil;
import org.tuanit.util.Helper;
import org.tuanit.util.SwingUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Data
public class ExecutingStatus extends JFrameCommon {
    public JList<String> listExecute;
    private JPanel panel;
    private JLabel lbTitle;
    private JButton btnStop;
    private JButton btnExport;
    private JButton btnPause;
    private JScrollPane scroll;
    private JCheckBox autoScrollCheckbox;
    //@Autowired
    //private ChromeService chromeService;
    private CompletableFuture<Void> voidCompletableFuture;
    public MyDefaultListModel<String> logs = new MyDefaultListModel<>(this);
    public boolean isBreak = false;
    public boolean isPause = false;
    private String titleToExport = "";

    public Integer stt;
    public Date date;
    public String profileName;
    public String name;
    public ExecuteStatusEnum statusEnum;

    public ExecutingStatus(int stt, String profileName, String name) {
        this.stt = stt;
        this.date = new Date();
        this.profileName = profileName;
        this.name = name;
        this.statusEnum = ExecuteStatusEnum.DANG_CHAY;
        SwingUtils.setIconBtn(btnExport, FontAwesome.ANGLE_DOUBLE_UP);
        SwingUtils.setIconBtn(btnStop, FontAwesome.STOP_CIRCLE_O);
        SwingUtils.setIconBtn(btnPause, FontAwesome.PAUSE_CIRCLE_O);
        btnStop.addActionListener(e -> {
            SwingUtils.option(() -> {
                isBreak = true;
                Helper.notify("Đang Huỷ");
            });
        });
        btnExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // parent component of the dialog
                JFrame parentFrame = new JFrame();

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Chọn folder để lưu file");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int userSelection = fileChooser.showSaveDialog(parentFrame);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    String fileName = JOptionPane.showInputDialog("Nhập tên file: ");
                    if (fileName != null && !fileName.trim().isEmpty()) {
                        File fileToSave = new File(fileChooser.getSelectedFile(), fileName + ".xlsx");

                        try {
                            ByteArrayInputStream outputStream = toExcel();
                            FileUtils.copyToFile(outputStream, fileToSave);
                            Helper.alert("Save as file: " + fileToSave.getPath());
                            outputStream.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            Helper.log(ex.getMessage());
                        }
                    } else {
                        Helper.alert("Tên file không được để trống.");
                    }
                }
            }
        });
        btnPause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isPause = !isPause;
                if (isPause) {
                    SwingUtils.setIconBtn(btnPause, FontAwesome.PLAY_CIRCLE_O);
                    Helper.notify("Đã tạm dừng");
                    btnPause.setText("Tiếp tục");
                } else {
                    SwingUtils.setIconBtn(btnPause, FontAwesome.PAUSE_CIRCLE_O);
                    Helper.notify("Đã tiếp tục");
                    btnPause.setText("Tạm dừng");
                }
            }
        });
    }

    public ExecutingStatus() {
    }

    private ByteArrayInputStream toExcel() {
        return ExcelUtil.toExcel(Arrays.stream(this.logs.toArray()).map(o -> {
            String val = (String) o;
            return new StatusHistory(val);
        }).collect(Collectors.toList()), Arrays.asList("HISTORY"), "data", StatusHistory.class);
    }

    public void executeWhenIsPausing() {
        while (isPause) {
            btnPause.setText("Tiếp tục");
            Helper.sleep(1000);
        }

        btnPause.setText("Tạm dừng");
    }

    public void showLog(String title, String profile) {
        lbTitle.setText("Lịch sử: " + title);
        setTitle("Profile: " + profile);
        commonSetup(this);
        getJMenuBar().setVisible(false);
        setContentPane(panel);
        listExecute.setModel(logs);
    }

    public void showUI(JFrame jFrame, Consumer<ExecutingStatus> consumer) {
        ExecutingService.PROFILE_ACTIVE.add(profileName);
        isBreak = false;
        isPause = false;
        btnPause.setText("Tạm Dừng");
        setTitle("Trạng thái thực thi, Profile: " + profileName);
        lbTitle.setText("Đang thực thi: " + jFrame.getTitle());
        commonSetup(jFrame);
        getJMenuBar().setVisible(false);
        setContentPane(panel);
        this.titleToExport = jFrame.getTitle();

        listExecute.setModel(logs);
        Helper.notify("Bắt đầu ...");

        Timer timer = getTimer();

        voidCompletableFuture = CFUtils.runAsync(() -> consumer.accept(this)).whenComplete((unused, a) -> {
//            Helper.alert("Hoàn thành !!!");
            ExecutingService.PROFILE_ACTIVE.remove(profileName);
            lbTitle.setText("Đã hoàn thành: " + jFrame.getTitle());
            if (a != null) {
                a.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    logs.addElement("Hoàn thành với lỗi: " + a.getMessage());
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    logs.addElement("Đã hoàn thành!");
                });
            }
            CompletableFuture.runAsync(() -> {
                Helper.sleep(3000);
                timer.stop();
            });
            isBreak = false;
            isPause = false;
            this.statusEnum = ExecuteStatusEnum.DA_XONG;
            btnPause.setText("Tạm Dừng");
        });

    }


    private Timer getTimer() {
        Timer timer = new Timer(200, e -> {
            // Adjust the value of the vertical scrollbar to scroll down
            if (autoScrollCheckbox.isSelected()) {
                JScrollBar verticalScrollBar = scroll.getVerticalScrollBar();
                verticalScrollBar.setValue(verticalScrollBar.getMaximum());
            }
        });

        // Start the timer
        timer.start();
        return timer;
    }

    @Override
    @SneakyThrows
    protected void commonSetup(JFrame jFrame) {
        setResizable(false);
        setSize(650, 720);
//        setLocationRelativeTo(null);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setJMenuBar(buildMenu(jFrame));
        //setIconImage(ImageIO.read(getClass().getResource(UrlConstant.URL_ENUM.getIcon())));
        setVisible(true);
    }

    @Override
    public void setup(JFrameSetup jFrameSetup) {

    }

    @Override
    public void showUI() {

    }

    @Override
    protected JFrame getJFrame() {
        return this;
    }


    public static class MyDefaultListModel<String> extends DefaultListModel<String> {
        private ExecutingStatus executingStatus;


        public MyDefaultListModel(ExecutingStatus executingStatus) {
            super();
            this.executingStatus = executingStatus;
        }


        @Override
        public void addElement(String element) {
            SwingUtilities.invokeLater(() -> {
                super.addElement(element);
            });
        }
    }

    @Data
    @AllArgsConstructor
    @ExportExcel
    public static class StatusHistory {
        private String history;
    }


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
        panel = new JPanel();
        panel.setLayout(new GridLayoutManager(3, 6, new Insets(0, 0, 0, 0), -1, -1));
        scroll = new JScrollPane();
        panel.add(scroll, new GridConstraints(1, 0, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        listExecute = new JList();
        scroll.setViewportView(listExecute);
        btnStop = new JButton();
        btnStop.setText("Dừng");
        panel.add(btnStop, new GridConstraints(2, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lbTitle = new JLabel();
        lbTitle.setText("");
        panel.add(lbTitle, new GridConstraints(0, 0, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnExport = new JButton();
        btnExport.setText("Xuất Excel");
        panel.add(btnExport, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnPause = new JButton();
        btnPause.setText("Tạm Dừng");
        panel.add(btnPause, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel.add(spacer1, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        autoScrollCheckbox = new JCheckBox();
        autoScrollCheckbox.setSelected(true);
        autoScrollCheckbox.setText("Bật tự động cuộn");
        panel.add(autoScrollCheckbox, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }


}
