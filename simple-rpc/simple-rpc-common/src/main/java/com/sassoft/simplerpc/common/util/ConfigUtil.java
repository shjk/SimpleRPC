package com.sassoft.simplerpc.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by shjk_000 on 2019/3/9.
 */
public class ConfigUtil {
    private static final String CONFIG_FILE_NAME="app.properties";

    public static String readConfig(String key){
        Properties properties = new Properties();
        // 使用ClassLoader加载properties配置文件生成对应的输入流
        InputStream in = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(CONFIG_FILE_NAME);
        // 使用properties对象加载输入流
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //获取key对应的value值
        String value =properties.getProperty(key);
        return value;
    }
}
