package org.tuanit.view;


import org.springframework.stereotype.Component;
import org.tuanit.util.CFUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;

@Component
public class ToastMessage extends JFrame {
    private JLabel jLabel = new JLabel();

    public ToastMessage(){
        setUndecorated(true);
        setLayout(new GridBagLayout());
        setBackground(new Color(240, 240, 240, 250));
        setSize(300, 50);
        setLocationRelativeTo(null);
        add(jLabel);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setShape(new RoundRectangle2D.Double(0, 0, getWidth(),
                        getHeight(), 20, 20));
            }
        });
    }

    public void notify(String message){
        jLabel.setText(message);
        CFUtils.runAsync(this::display);
    }
    private void display() {
        try {
            setOpacity(1);
            setVisible(true);
            Thread.sleep(2000);

            //hide the toast message in slow motion
            for (double d = 1.0; d > 0.2; d -= 0.1) {
                Thread.sleep(100);
                setOpacity((float) d);
            }

            // set the visibility to false
            setVisible(false);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
