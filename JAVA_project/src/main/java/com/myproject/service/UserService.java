package main.java.com.myproject.service;

import main.java.com.myproject.model.User;

import java.io.*;
import java.util.Properties;

/**
 * 用户服务类 - 处理用户数据存储和检索
 */
public class UserService {

    // 用户配置文件路径
    private static final String USER_CONFIG_FILE = "user_profile.properties";

    // 默认头像路径
    private static final String DEFAULT_AVATAR_PATH = "img.png";

    // 当前用户
    private User currentUser;

    /**
     * 构造函数
     */
    public UserService() {
        // 尝试加载用户配置
        loadUserFromConfig();
    }

    /**
     * 获取当前用户
     * @return 当前用户对象
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * 保存用户数据
     * @param user 要保存的用户对象
     * @return 判断是否保存成功
     */
    public boolean saveUser(User user) {
        if (user == null) {
            return false;
        }

        // 更新当前用户
        this.currentUser = user;

        // 保存到配置文件
        return saveUserToConfig();
    }

    /**
     * 从配置文件加载用户数据
     */
    private void loadUserFromConfig() {
        Properties properties = new Properties();
        File configFile = new File(USER_CONFIG_FILE);

        // 如果配置文件存在，则加载
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);

                // 创建用户对象
                currentUser = new User(
                        properties.getProperty("username", ""),
                        properties.getProperty("fullName", ""),
                        properties.getProperty("email", ""),
                        properties.getProperty("phone", ""),
                        properties.getProperty("address", ""),
                        properties.getProperty("bio", ""),
                        properties.getProperty("avatarPath", "")
                );

                System.out.println("User profile loaded from " + USER_CONFIG_FILE);

                // 检查头像文件是否存在
                if (currentUser.getAvatarPath() == null || currentUser.getAvatarPath().isEmpty()
                        || !new File(currentUser.getAvatarPath()).exists()) {
                    // 若头像不存在，则使用默认头像
                    currentUser.setAvatarPath(DEFAULT_AVATAR_PATH);
                }

            } catch (IOException e) {
                System.err.println("Error loading user profile: " + e.getMessage());
                // 创建一个默认用户
                createDefaultUser();
            }
        } else {
            // 若配置文件不存在，则创建默认用户
            createDefaultUser();
        }
    }

    private boolean saveUserToConfig() {
        if (currentUser == null) {
            return false;
        }

        Properties properties = new Properties();

        // 设置属性
        properties.setProperty("username", currentUser.getUsername());
        properties.setProperty("fullName", currentUser.getFullName());
        properties.setProperty("email", currentUser.getEmail());
        properties.setProperty("phone", currentUser.getPhone());
        properties.setProperty("address", currentUser.getAddress());
        properties.setProperty("bio", currentUser.getBio());
        properties.setProperty("avatarPath", currentUser.getAvatarPath() != null ? currentUser.getAvatarPath() : "");

        // 保存到文件
        try (FileOutputStream fos = new FileOutputStream(USER_CONFIG_FILE)) {
            properties.store(fos, "User Profile");
            System.out.println("User profile saved to " + USER_CONFIG_FILE);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving user profile: " + e.getMessage());
            return false;
        }
    }

    private void createDefaultUser() {
        currentUser = new User(
                "BUPT",
                "北京邮电大学",
                "12345678@qq.com",
                "+86 111111111",
                "西土城路10号北京邮电大学",
                "包的老弟",
                DEFAULT_AVATAR_PATH  // 使用默认头像路径
        );

    }
}
