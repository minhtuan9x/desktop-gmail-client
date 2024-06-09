package org.tuanit;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeansException;
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
        FlatLaf.registerCustomDefaultsSource("themes");
        FlatLightLaf.setup();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace();
//                Helper.alert("Lỗi chương trình mời bạn thử lại sau!!!\n" + e.getMessage());
            return;
        });
        LoadingView loadingView = new LoadingView();
        loadingView.loading(null, CFUtils.runAsync(() -> {
            try {
                ApplicationContext applicationContext = new AnnotationConfigApplicationContext(Main.class);
                // In your main method or initialization block
                HomeView homeView = applicationContext.getBean(HomeView.class);
                homeView.showUI();
            } catch (BeansException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }));
    }
}