package org.tuanit.view;

import com.intellij.uiDesigner.core.GridLayoutManager;
import org.springframework.stereotype.Component;
import org.tuanit.model.swing.JFrameSetup;

import javax.swing.*;
import java.awt.*;

@Component
public class HomeView extends JFrameCommon {

    private JPanel pane;

    @Override
    public void setup(JFrameSetup jFrameSetup) {
        jFrameSetup.jPane(pane).title("Hello");
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
        pane = new JPanel();
        pane.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return pane;
    }

}
