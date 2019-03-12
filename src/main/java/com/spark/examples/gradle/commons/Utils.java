package com.spark.examples.gradle.commons;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by fabrice on 3/12/19.
 */
public class Utils {

    /**
     * load the application properties from Resources
     * @return
     */
    public static Properties loadPropertiesFromResources(){
        String appConfigPath = Thread.currentThread().getContextClassLoader().getResource("application.properties").getPath();
        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(appConfigPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return appProps;
    }


    public  static String getPathFromResources(String fileName){
        return Thread.currentThread().getContextClassLoader().getResource(fileName).getPath();
    }
}
