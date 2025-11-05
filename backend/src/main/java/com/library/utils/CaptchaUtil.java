package com.library.utils;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证码工具类
 */
@Component
public class CaptchaUtil {
    
    private final DefaultKaptcha kaptcha;
    private final Map<String, String> captchaStore = new ConcurrentHashMap<>();
    private static final long CAPTCHA_EXPIRE_TIME = 5 * 60 * 1000; // 5分钟过期
    
    public CaptchaUtil() {
        this.kaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", "yes");
        properties.setProperty("kaptcha.border.color", "105,179,90");
        properties.setProperty("kaptcha.textproducer.font.color", "blue");
        properties.setProperty("kaptcha.image.width", "120");
        properties.setProperty("kaptcha.image.height", "40");
        properties.setProperty("kaptcha.textproducer.font.size", "30");
        properties.setProperty("kaptcha.session.key", "code");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        properties.setProperty("kaptcha.textproducer.font.names", "Arial,Courier");
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        properties.setProperty("kaptcha.noise.color", "white");
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.DefaultNoise");
        properties.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.WaterRipple");
        Config config = new Config(properties);
        kaptcha.setConfig(config);
    }
    
    /**
     * 生成验证码
     */
    public Map<String, String> generateCaptcha() {
        String captchaText = kaptcha.createText();
        BufferedImage image = kaptcha.createImage(captchaText);
        
        String key = UUID.randomUUID().toString();
        captchaStore.put(key, captchaText.toLowerCase());
        
        // 5分钟后自动删除
        new Thread(() -> {
            try {
                Thread.sleep(CAPTCHA_EXPIRE_TIME);
                captchaStore.remove(key);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        
        Map<String, String> result = new HashMap<>();
        result.put("key", key);
        result.put("image", imageToBase64(image));
        
        return result;
    }
    
    /**
     * 验证验证码
     */
    public boolean validateCaptcha(String key, String code) {
        if (key == null || code == null) {
            return false;
        }
        
        String storedCode = captchaStore.get(key);
        if (storedCode == null) {
            return false;
        }
        
        boolean valid = storedCode.equals(code.toLowerCase());
        if (valid) {
            captchaStore.remove(key); // 验证成功后删除
        }
        return valid;
    }
    
    /**
     * 将图片转换为Base64
     */
    private String imageToBase64(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException("验证码图片生成失败", e);
        }
    }
}

