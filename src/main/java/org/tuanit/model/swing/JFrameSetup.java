package org.tuanit.model.swing;

import lombok.Getter;

import javax.swing.*;

@Getter
public class JFrameSetup {
    private String title;
    private JComponent jPane;

    public JFrameSetup title(String title) {
        this.title = title;
        return this;
    }

    public JFrameSetup jPane(JComponent jPane) {
        this.jPane = jPane;
        return this;
    }
}
