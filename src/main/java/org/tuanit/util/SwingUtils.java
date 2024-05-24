package org.tuanit.util;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import org.apache.commons.lang3.StringUtils;
import org.tuanit.constant.SystemConstant;
import org.tuanit.model.swing.BaseJListCell;
import org.tuanit.model.swing.CheckboxListCellRenderer;
import org.tuanit.model.swing.MyDefaultListSelectionModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SwingUtils {

    public static <T> void setCustomJList(JList<BaseJListCell<T>> jList, DefaultListModel<BaseJListCell<T>> stringDefaultListModel, JLabel jCount) {
        setCustomJList(jList, stringDefaultListModel, jCount, null);
    }

    public static <T> void setCustomJList(JList<BaseJListCell<T>> jList, DefaultListModel<BaseJListCell<T>> stringDefaultListModel, JLabel jCount, JTextField filterTextField) {
        jList.setSelectionModel(new MyDefaultListSelectionModel());
        CheckboxListCellRenderer<T> stringCheckboxListCellRenderer = new CheckboxListCellRenderer<>();
        jList.setCellRenderer(stringCheckboxListCellRenderer);
        jList.setModel(stringDefaultListModel);
        if (jCount != null) {
            jList.addListSelectionListener(e -> jCount.setText(String.valueOf(jList.getSelectedValuesList().size())));
        }
        jList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        setSearch(jList, stringDefaultListModel, stringCheckboxListCellRenderer, filterTextField);
    }

    public static <T> void setSearch(JList<BaseJListCell<T>> jList, DefaultListModel<BaseJListCell<T>> stringDefaultListModel, CheckboxListCellRenderer<T> stringCheckboxListCellRenderer, JTextField filterTextField) {
        if (filterTextField != null) {
            filterTextField.setToolTipText("Tìm kiếm");
//            filterTextField.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    filterAndSortList(jList, stringDefaultListModel, filterTextField.getText(), stringCheckboxListCellRenderer);
//                }
//            });

            filterTextField.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    filterAndSortList(jList, stringDefaultListModel, filterTextField.getText(), stringCheckboxListCellRenderer);
                }
            });

            filterTextField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    filterAndSortList(jList, stringDefaultListModel, filterTextField.getText(), stringCheckboxListCellRenderer);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    filterAndSortList(jList, stringDefaultListModel, filterTextField.getText(), stringCheckboxListCellRenderer);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    // Plain text components do not fire these events
                }
            });
        }
    }

    private static <T> void filterAndSortList(JList<BaseJListCell<T>> jList, DefaultListModel<BaseJListCell<T>> listModel, String filterText, CheckboxListCellRenderer<T>  stringCheckboxListCellRenderer) {
        List<BaseJListCell<T>> selectedList = jList.getSelectedValuesList();
        List<BaseJListCell<T>> allList = new ArrayList<>();
        for (int i = 0; i < jList.getModel().getSize(); i++) {
            allList.add(jList.getModel().getElementAt(i));
        }
        List<BaseJListCell<T>> filteredList = allList.stream()
                .filter(s -> StringUtils.containsIgnoreCase(removeDiacriticalMarks(s.getShowValue()), removeDiacriticalMarks(filterText)) && StringUtils.isNotBlank(filterText)).collect(Collectors.toList());
        stringCheckboxListCellRenderer.filtered = filteredList.stream().map(BaseJListCell::getId).collect(Collectors.toList());
        List<BaseJListCell<T>> remainingItems = new ArrayList<>(allList);
        remainingItems.removeAll(filteredList);

        List<ListSelectionListener> listSelectionListeners = List.of(jList.getListSelectionListeners());
        for (ListSelectionListener listSelectionListener : listSelectionListeners) {
            jList.removeListSelectionListener(listSelectionListener);
        }
        listModel.removeAllElements();
        listModel.addAll(filteredList);
        filteredList.forEach(s -> {
            if (selectedList.contains(s)) {
                jList.addSelectionInterval(listModel.getSize() - 1, listModel.getSize() - 1);
            }
        });

        listModel.addAll(remainingItems);
        remainingItems.forEach(s -> {
            if (selectedList.contains(s)) {
                jList.addSelectionInterval(listModel.getSize() - 1, listModel.getSize() - 1);
            }
        });

        if (!filteredList.isEmpty()) {
            jList.ensureIndexIsVisible(0);
        }
        for (ListSelectionListener listSelectionListener : listSelectionListeners) {
            jList.addListSelectionListener(listSelectionListener);
        }
    }

    public static String removeDiacriticalMarks(String string) {
        return Normalizer.normalize(string, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public static void setHotkeyForButton(JFrame jFrame, JButton jButton, int keyEvent, String description) {
        jButton.setText(jButton.getText() + " (" + description + ")");
        // Add window listener to the frame
        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                // Enable the button when the frame is activated
                jButton.setEnabled(true);
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                // Disable the button when the frame is deactivated
                jButton.setEnabled(false);
            }
        });

        // Set the accelerator key
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyEvent, 0);
        jButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, description);
        jButton.getActionMap().put(description, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle the button click action
                jButton.doClick();
            }
        });
    }

    public static <T> void setSelectedItemList(JButton jButtonAll, JList<T> list, JButton... jButtons) {
        jButtonAll.addActionListener(e -> {
            ListSelectionModel sm = list.getSelectionModel();
            if (sm.isSelectionEmpty()) {
                sm.addSelectionInterval(0, list.getModel().getSize() - 1);
                list.ensureIndexIsVisible(list.getModel().getSize() - 1);
            } else {
                sm.clearSelection();
                list.ensureIndexIsVisible(0);
            }
        });
        for (int i = 0; i < jButtons.length; i++) {
            int finalI = i; // To capture the current value of i for the lambda expression
            jButtons[i].addActionListener(e -> {
                ListSelectionModel sm = list.getSelectionModel();
                int itemsCount = list.getModel().getSize();
                int indexFrom = finalI * 50;
                int indexTo = Math.min((finalI + 1) * 50 - 1, itemsCount - 1);
                if (indexFrom < itemsCount) {
                    sm.clearSelection();
                    sm.addSelectionInterval(indexFrom, indexTo);
                    /// Scroll to the last selected item
                    int lastIndex = Math.min(indexTo, itemsCount - 1);
                    list.ensureIndexIsVisible(lastIndex);
                }
            });
        }
    }

    public static <T> void setSelectedRangeItemList(JSpinner spinnerFrom, JSpinner spinnerTo, JButton btnSetRange, JList<T> list) {
        spinnerFrom.setValue(1);
        spinnerTo.setValue(50);
        SwingUtils.setIconBtn(btnSetRange, FontAwesome.COG);
        btnSetRange.addActionListener(e -> {
            ListSelectionModel sm = list.getSelectionModel();
            int itemsCount = list.getModel().getSize();
            int indexFrom = (int) spinnerFrom.getValue() - 1;
            int indexTo = Math.min((int) spinnerTo.getValue() - 1, itemsCount - 1);
            if (indexFrom < itemsCount && indexFrom < indexTo) {
                sm.clearSelection();
                sm.addSelectionInterval(indexFrom, indexTo);
                /// Scroll to the last selected item
                int lastIndex = Math.min(indexTo, itemsCount - 1);
                list.ensureIndexIsVisible(lastIndex);
            } else {
                alert("Không hợp lệ!!!");
            }
        });
    }

    public static void alert(String mess, String... params) {
        String message = mess.length() > 150 ? "<html><body><p style='width: 400px;'>" + mess + "</p></body></html>" : mess;
        message = message.replace("{}", "%s");
        message = String.format(message, params);
        String finalMessage = message;
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, finalMessage);
        });
    }


    public static List<String> getValuesOfTable(DefaultTableModel defaultTableModel) {
        List<String> numdata = new ArrayList<>();
        for (int count = 0; count < defaultTableModel.getRowCount(); count++) {
            numdata.add(defaultTableModel.getValueAt(count, 1).toString());
        }
        return numdata;
    }

    public static String getValuesOfTableString(DefaultTableModel defaultTableModel) {
        return String.join(";", getValuesOfTable(defaultTableModel));
    }

    public static void setIconBtn(JButton jButton, FontAwesome iconCode) {
        IconFontSwing.register(FontAwesome.getIconFont());
        Icon icon = IconFontSwing.buildIcon(iconCode, 15, SystemConstant.BTN_COLOR);
        jButton.setIcon(icon);
    }

    public static void setupImage(JButton buttonImageThem, JButton buttonImageXoa, JTable tableImage, DefaultTableModel defaultTableModel) {
        defaultTableModel.addColumn("Tên File");
        defaultTableModel.addColumn("Path");
        tableImage.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableImage.setModel(defaultTableModel);
        buttonImageThem.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    } else {
                        String filename = f.getName().toLowerCase();
                        return filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".png");
                    }
                }

                @Override
                public String getDescription() {
                    return "Hình (*.jpg, *.png)";
                }
            });
            jFileChooser.setDialogTitle("Chọn hình?");
            jFileChooser.setMultiSelectionEnabled(true);
            int result = jFileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                Arrays.stream(jFileChooser.getSelectedFiles())
                        //.filter(file -> file.getAbsolutePath().toLowerCase().endsWith(".png") || file.getAbsolutePath().toLowerCase().endsWith(".jpg"))
                        .forEach(file -> {
                            defaultTableModel.addRow(new Object[]{file.getName(), file.getAbsolutePath()});
                        });
            }
        });
        buttonImageXoa.addActionListener(e -> defaultTableModel.removeRow(tableImage.getSelectedRow()));
    }

    public static void option(Runnable runnable) {
        option(runnable, null);
    }

    public static void option(Runnable runnable, String message) {
        int input = JOptionPane.showConfirmDialog(null, message == null ? "Bạn có muốn thực hiện hành động này?" : message, "Thông báo", JOptionPane.YES_NO_OPTION);
        if (input == JOptionPane.YES_OPTION) {
            runnable.run();
        }
    }
}
