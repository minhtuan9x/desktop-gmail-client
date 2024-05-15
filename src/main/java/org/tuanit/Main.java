package org.tuanit;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import javax.swing.*;


@ComponentScan(basePackages = "org.tuanit")
public class Main {
    public static void main(String[] args){
        SSLUtil.trustAllHosts();
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(Main.class);
        // In your main method or initialization block
        GmailSenderApp gmailSenderApp = applicationContext.getBean(GmailSenderApp.class);
        gmailSenderApp.initialize();
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                gmailSenderApp.shutdown();
            }
        });
        JFrame frame = new JFrame("Gmail Sender");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);
        gmailSenderApp.placeComponents(panel);

        frame.setVisible(true);
    }
}