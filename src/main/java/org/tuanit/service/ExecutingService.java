package org.tuanit.service;

import org.springframework.stereotype.Component;
import org.tuanit.constant.SystemConstant;
import org.tuanit.model.ExecutingHistory;
import org.tuanit.model.ListExecutingStatus;
import org.tuanit.model.swing.ButtonColumn;
import org.tuanit.util.Helper;
import org.tuanit.view.ExecutingStatus;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class ExecutingService {
    private static final List<ExecutingStatus> EXECUTING_STATUSES = new ArrayList<>();
    private static Integer STT = null;
    public static List<String> PROFILE_ACTIVE = new ArrayList<>();


    public void execute(JFrame jFrame, String profile, String name, Consumer<ExecutingStatus> consumer, Object... ui) {
        if (PROFILE_ACTIVE.contains(profile)) {
            Helper.alert("Profile: " + profile + " đang chạy tiến trình. Bạn vui lòng chờ hoàn tất!!");
            return;
        }
        processExecuting(jFrame, profile, name, consumer);
    }

    public void executeWithoutCheck(JFrame jFrame, String profile, String name, Consumer<ExecutingStatus> consumer, Object... ui) {
        processExecuting(jFrame, profile, name, consumer);
    }

    private void processExecuting(JFrame jFrame, String profile, String name, Consumer<ExecutingStatus> consumer) {
        ListExecutingStatus listExecutingStatus = Helper.getObjectOptionally(ListExecutingStatus.class).orElse(new ListExecutingStatus());
        if (STT == null) {
            if (!listExecutingStatus.getExecutingHistories().isEmpty()) {
                STT = listExecutingStatus.getExecutingHistories().stream().map(ExecutingHistory::getStt).max(Integer::compareTo).get();
            } else {
                STT = 1;
            }
        }
        ExecutingStatus executingStatus = new ExecutingStatus(++STT, profile, name);
        executingStatus.showUI(jFrame, consumer);
        EXECUTING_STATUSES.add(executingStatus);
    }

    public void buildTable(DefaultTableModel defaultTableModel) {
        defaultTableModel.setRowCount(0);
        EXECUTING_STATUSES.sort((o1, o2) -> o2.getStt().compareTo(o1.getStt()));
        for (ExecutingStatus executingStatus : EXECUTING_STATUSES) {
            defaultTableModel.addRow(new Object[]{executingStatus.stt, executingStatus.date.toString(),
                    executingStatus.profileName.replace(SystemConstant.PROFILE_PREFIX, ""), executingStatus.name,
                    executingStatus.statusEnum.getValue(), "Mở Bảng nhật ký"});
        }
        buildSubTable(defaultTableModel);
    }

    public void buildSubTable(DefaultTableModel defaultTableModel) {
        ListExecutingStatus listExecutingStatus = Helper.getObjectOptionally(ListExecutingStatus.class).orElse(new ListExecutingStatus());
        listExecutingStatus.getExecutingHistories().sort((o1, o2) -> o2.getStt().compareTo(o1.getStt()));
        for (ExecutingHistory item : listExecutingStatus.getExecutingHistories()) {
            try {
                defaultTableModel.addRow(new Object[]{item.getStt(), item.getDate(),
                        item.getProfileName().replace(SystemConstant.PROFILE_PREFIX, ""), item.getName(),
                        item.getStatus(), "Mở Lịch Sử"});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void saveToFile() {
        ListExecutingStatus listExecutingStatus = Helper.getObjectOptionally(ListExecutingStatus.class).orElse(new ListExecutingStatus());
        listExecutingStatus.getExecutingHistories().addAll(EXECUTING_STATUSES.stream().map(item -> {
            ExecutingHistory executingHistory = new ExecutingHistory();
            executingHistory.setStt(item.stt);
            executingHistory.setName(item.name);
            executingHistory.setProfileName(item.profileName);
            executingHistory.setDate(item.date.toString());
            executingHistory.setStatus("Lịch sử");
            executingHistory.setTitle(item.getTitle());
            List<String> logMess = new ArrayList<>();
            for (int i = 0; i < item.logs.size(); i++) {
                logMess.add(item.logs.get(i));
            }
            executingHistory.setLogs(logMess);
            return executingHistory;
        }).collect(Collectors.toList()));
        Helper.writeFile(listExecutingStatus);
    }

    public void addAction(JTable table) {
        Action open = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                JTable table = (JTable) e.getSource();
                int modelRow = Integer.valueOf(e.getActionCommand());
                Object IdColumn = table.getModel().getValueAt(modelRow, 0);
                int id = (int) IdColumn;
                ExecutingStatus executingStatus = EXECUTING_STATUSES.stream().filter(item -> item.stt == id).findFirst().orElse(null);
                if (executingStatus != null) {
                    executingStatus.setVisible(true);
                } else {
                    ListExecutingStatus listExecutingStatus = Helper.getObjectOptionally(ListExecutingStatus.class).orElse(new ListExecutingStatus());
                    listExecutingStatus.getExecutingHistories().stream().filter(item -> item.getStt() == id).findFirst().ifPresent(executingHistory -> {
                        getScrollPane(executingHistory.getLogs(), executingHistory.getTitle(), executingHistory.getProfileName());
                    });
                }
            }
        };

        ButtonColumn buttonColumn = new ButtonColumn(table, open, 5);
        buttonColumn.setMnemonic(KeyEvent.VK_D);
    }

    private void getScrollPane(List<String> logs, String title, String profile) {
        ExecutingStatus executingStatus = new ExecutingStatus();
        executingStatus.logs.addAll(logs);
        executingStatus.setProfileName(profile);
        executingStatus.showLog(title, profile);
    }
}
