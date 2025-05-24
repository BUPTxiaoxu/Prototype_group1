package main.java.com.myproject.model;

/**
 * 用户模型类 - 存储用户个人信息
 */
public class User {
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String bio;
    private String avatarPath;

    /**
     * 默认构造函数
     */
    public User() {
        this.username = "";
        this.fullName = "";
        this.email = "";
        this.phone = "";
        this.address = "";
        this.bio = "";
        this.avatarPath = "";
    }

    /**
     * 构造函数
     * @param username 用户名
     * @param fullName 全名
     * @param email 邮箱
     * @param phone 电话
     * @param address 地址
     * @param bio 个人简介
     * @param avatarPath 头像路径
     */
    public User(String username, String fullName, String email, String phone, String address, String bio, String avatarPath) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.bio = bio;
        this.avatarPath = avatarPath;
    }

    // 获取用户名
    public String getUsername() {
        return username;
    }

    // 设置用户名
    public void setUsername(String username) {
        this.username = username;
    }

    // 获取全名
    public String getFullName() {
        return fullName;
    }

    // 设置全名
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // 获取邮箱
    public String getEmail() {
        return email;
    }

    // 设置邮箱
    public void setEmail(String email) {
        this.email = email;
    }

    // 获取电话
    public String getPhone() {
        return phone;
    }

    // 设置电话
    public void setPhone(String phone) {
        this.phone = phone;
    }

    // 获取地址
    public String getAddress() {
        return address;
    }

    // 设置地址
    public void setAddress(String address) {
        this.address = address;
    }

    // 获取个人简介
    public String getBio() {
        return bio;
    }

    // 设置个人简介
    public void setBio(String bio) {
        this.bio = bio;
    }

    // 获取头像路径
    public String getAvatarPath() {
        return avatarPath;
    }

    // 设置头像路径
    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", bio='" + bio + '\'' +
                ", avatarPath='" + avatarPath + '\'' +
                '}';
    }
}