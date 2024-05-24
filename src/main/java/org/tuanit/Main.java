package org.tuanit;

import com.formdev.flatlaf.FlatLightLaf;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.tuanit.util.CFUtils;
import org.tuanit.view.HomeView;
import org.tuanit.view.LoadingView;

import java.io.IOException;


@ComponentScan(basePackages = "org.tuanit")
public class Main {
    public static void main(String[] args) throws IOException {
        FlatLightLaf.setup();
        LoadingView loadingView = new LoadingView();
        loadingView.loading(null, CFUtils.runAsync(() -> {
            ApplicationContext applicationContext = new AnnotationConfigApplicationContext(Main.class);
            // In your main method or initialization block
            HomeView homeView = applicationContext.getBean(HomeView.class);
            homeView.showUI();
        }));
    }
}