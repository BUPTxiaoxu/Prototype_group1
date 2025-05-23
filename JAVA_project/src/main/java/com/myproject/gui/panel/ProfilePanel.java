package main.java.com.myproject.gui.panel;

import main.java.com.myproject.model.User;
import main.java.com.myproject.service.UserService;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 个人资料面板 - 显示和编辑用户个人信息
 */
public class ProfilePanel extends JPanel {

    // 用户服务
    private UserService userService;

    // 用户数据
    private User currentUser;

    // UI组件
    private JLabel userAvatarLabel;
    private BufferedImage avatarImage;
    private JTextField usernameField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextArea bioTextArea;
    private JButton saveButton;
    private JButton editAvatarButton;

    // 默认头像图片路径
    private static final String DEFAULT_AVATAR_PATH = "Prototype_group1/img.png";

    // 头像尺寸
    private static final int AVATAR_SIZE = 120;

    /**
     * 构造函数
     */
    public ProfilePanel() {
        // 创建用户服务并加载当前用户
        userService = new UserService();
        currentUser = userService.getCurrentUser();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 初始化组件
        initializeUI();

        // 加载用户数据
        loadUserData();
    }

    /**
     * 初始化UI组件
     */
    private void initializeUI() {
        // 创建主容器，使用垂直箱式布局
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // 创建标题面板
        JPanel titlePanel = createTitlePanel();

        // 创建中心内容面板 - 居中显示
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50)); // 左右边距增加

        // 创建头像和基本信息面板
        JPanel profileHeaderPanel = createProfileHeaderPanel();

        // 创建个人详细信息面板
        JPanel detailsPanel = createDetailsPanel();

        // 将内容面板添加到中心面板
        centerPanel.add(profileHeaderPanel, BorderLayout.NORTH);
        centerPanel.add(detailsPanel, BorderLayout.CENTER);

        // 添加到主容器
        mainContainer.add(titlePanel);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        mainContainer.add(centerPanel);

        // 创建滚动面板
        JScrollPane scrollPane = new JScrollPane(mainContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // 设置滚动速度

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * 创建标题面板
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel titleSubPanel = new JPanel();
        titleSubPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel titleLabel = new JLabel("User Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));

        JLabel subtitleLabel = new JLabel("Manage your personal information");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(127, 140, 141));

        titleSubPanel.add(titleLabel);
        titleSubPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        titleSubPanel.add(subtitleLabel);

        panel.add(titleSubPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 创建头像和基本信息面板
     */
    private JPanel createProfileHeaderPanel() {
        // 创建一个居中的面板
        JPanel centeringPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // 创建内部内容面板
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 20, 20, 20)
        ));
        panel.setMaximumSize(new Dimension(600, 200)); // 限制最大宽度

        // 创建头像面板
        JPanel avatarPanel = new JPanel(new BorderLayout());
        avatarPanel.setPreferredSize(new Dimension(AVATAR_SIZE + 40, AVATAR_SIZE + 60));
        avatarPanel.setMaximumSize(new Dimension(AVATAR_SIZE + 40, AVATAR_SIZE + 60));

        // 创建圆形头像标签
        userAvatarLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 如果有头像图片，显示它；否则显示默认图片
                if (avatarImage != null) {
                    // 创建圆形裁剪区域
                    Ellipse2D.Double clipShape = new Ellipse2D.Double(0, 0, AVATAR_SIZE, AVATAR_SIZE);
                    g2.setClip(clipShape);

                    // 绘制图像
                    g2.drawImage(avatarImage, 0, 0, AVATAR_SIZE, AVATAR_SIZE, null);

                    // 绘制边框
                    g2.setClip(null);
                    g2.setStroke(new BasicStroke(2));
                    g2.setColor(new Color(200, 200, 200));
                    g2.draw(clipShape);
                } else {
                    // 绘制占位符
                    g2.setColor(new Color(220, 220, 220));
                    g2.fill(new Ellipse2D.Double(0, 0, AVATAR_SIZE, AVATAR_SIZE));
                    g2.setColor(new Color(180, 180, 180));
                    g2.setFont(new Font("Arial", Font.BOLD, 40));
                    g2.drawString("?", AVATAR_SIZE/2 - 12, AVATAR_SIZE/2 + 15);
                }

                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(AVATAR_SIZE, AVATAR_SIZE);
            }
        };

        // 创建编辑头像按钮
        editAvatarButton = new JButton("Change Avatar");
        editAvatarButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        editAvatarButton.setFont(new Font("Arial", Font.PLAIN, 12));
        editAvatarButton.setFocusPainted(false);

        // 添加编辑头像事件
        editAvatarButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Avatar Image");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));

            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    avatarImage = ImageIO.read(selectedFile);
                    userAvatarLabel.repaint();

                    // 保存头像到用户数据
                    currentUser.setAvatarPath(selectedFile.getAbsolutePath());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error loading image: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel avatarLabelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        avatarLabelPanel.add(userAvatarLabel);

        avatarPanel.add(avatarLabelPanel, BorderLayout.CENTER);
        avatarPanel.add(editAvatarButton, BorderLayout.SOUTH);

        // 创建基本信息面板
        JPanel basicInfoPanel = new JPanel();
        basicInfoPanel.setLayout(new BoxLayout(basicInfoPanel, BoxLayout.Y_AXIS));

        // 用户名字段
        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setPreferredSize(new Dimension(100, 20));

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));

        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);

        // 全名字段
        JPanel fullNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel fullNameLabel = new JLabel("Full Name:");
        fullNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        fullNameLabel.setPreferredSize(new Dimension(100, 20));

        fullNameField = new JTextField(20);
        fullNameField.setFont(new Font("Arial", Font.PLAIN, 14));

        fullNamePanel.add(fullNameLabel);
        fullNamePanel.add(fullNameField);

        // 邮箱字段
        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emailLabel.setPreferredSize(new Dimension(100, 20));

        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));

        emailPanel.add(emailLabel);
        emailPanel.add(emailField);

        basicInfoPanel.add(usernamePanel);
        basicInfoPanel.add(fullNamePanel);
        basicInfoPanel.add(emailPanel);

        // 添加到头像和基本信息面板
        panel.add(avatarPanel);
        panel.add(Box.createRigidArea(new Dimension(20, 0)));
        panel.add(basicInfoPanel);

        // 将面板添加到居中容器
        centeringPanel.add(panel);

        return centeringPanel;
    }

    /**
     * 创建个人详细信息面板
     */
    private JPanel createDetailsPanel() {
        // 创建一个居中的容器
        JPanel centeringPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // 创建内部面板
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Contact Information"));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(600, 400)); // 限制最大宽度

        // 电话字段
        JPanel phonePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 14));
        phoneLabel.setPreferredSize(new Dimension(100, 20));

        phoneField = new JTextField(20);
        phoneField.setFont(new Font("Arial", Font.PLAIN, 14));

        phonePanel.add(phoneLabel);
        phonePanel.add(phoneField);

        // 地址字段
        JPanel addressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font("Arial", Font.BOLD, 14));
        addressLabel.setPreferredSize(new Dimension(100, 20));

        addressField = new JTextField(30);
        addressField.setFont(new Font("Arial", Font.PLAIN, 14));

        addressPanel.add(addressLabel);
        addressPanel.add(addressField);

        // 个人简介字段
        JPanel bioPanel = new JPanel(new BorderLayout());
        bioPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel bioLabel = new JLabel("About Me:");
        bioLabel.setFont(new Font("Arial", Font.BOLD, 14));

        bioTextArea = new JTextArea(5, 30);
        bioTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
        bioTextArea.setLineWrap(true);
        bioTextArea.setWrapStyleWord(true);
        bioTextArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        bioPanel.add(bioLabel, BorderLayout.NORTH);
        bioPanel.add(new JScrollPane(bioTextArea), BorderLayout.CENTER);

        // 保存按钮
        saveButton = new JButton("Save Changes");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setBackground(new Color(52, 152, 219));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(41, 128, 185), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        saveButton.addActionListener(e -> saveUserData());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);

        // 添加到详细信息面板
        panel.add(phonePanel);
        panel.add(addressPanel);
        panel.add(bioPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(buttonPanel);

        // 将面板添加到居中容器
        centeringPanel.add(panel);

        return centeringPanel;
    }


    private void loadUserData() {
        if (currentUser != null) {
            // 加载基本信息
            usernameField.setText(currentUser.getUsername());
            fullNameField.setText(currentUser.getFullName());
            emailField.setText(currentUser.getEmail());
            phoneField.setText(currentUser.getPhone());
            addressField.setText(currentUser.getAddress());
            bioTextArea.setText(currentUser.getBio());

            // 加载头像
            loadAvatarImage();
        } else {
            // 如果没有用户数据，使用默认值
            usernameField.setText("user123");
            fullNameField.setText("John Doe");
            emailField.setText("john.doe@example.com");
            phoneField.setText("+1 (123) 456-7890");
            addressField.setText("123 Main Street, Anytown, AN 12345");
            bioTextArea.setText("Tell us about yourself...");
        }
    }

    /**
     * 加载头像图片
     */
    private void loadAvatarImage() {
        try {
            if (currentUser.getAvatarPath() != null && !currentUser.getAvatarPath().isEmpty()) {
                // 尝试从用户头像路径加载
                File avatarFile = new File(currentUser.getAvatarPath());
                if (avatarFile.exists()) {
                    avatarImage = ImageIO.read(avatarFile);
                    userAvatarLabel.repaint();
                    return;
                }
            }

            // 尝试从项目根目录加载默认头像
            File defaultAvatarFile = new File(DEFAULT_AVATAR_PATH);
            if (defaultAvatarFile.exists()) {
                avatarImage = ImageIO.read(defaultAvatarFile);
                userAvatarLabel.repaint();
                return;
            }

            // 如果头像文件不存在，创建默认头像
            avatarImage = createDefaultAvatar();
            userAvatarLabel.repaint();

        } catch (Exception e) {
            System.err.println("Error loading avatar: " + e.getMessage());
            e.printStackTrace();

            // 创建一个简单的默认头像
            avatarImage = createDefaultAvatar();
            userAvatarLabel.repaint();
        }
    }

    /**
     * 创建默认头像
     */
    private BufferedImage createDefaultAvatar() {
        BufferedImage img = new BufferedImage(AVATAR_SIZE, AVATAR_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 填充背景
        g2.setColor(new Color(52, 152, 219)); // 蓝色背景
        g2.fillOval(0, 0, AVATAR_SIZE, AVATAR_SIZE);

        // 绘制默认人物轮廓
        g2.setColor(Color.WHITE);
        // 头部
        g2.fillOval(AVATAR_SIZE/4, AVATAR_SIZE/8, AVATAR_SIZE/2, AVATAR_SIZE/2);
        // 身体
        g2.fillOval(AVATAR_SIZE/3, AVATAR_SIZE/2 + AVATAR_SIZE/8, AVATAR_SIZE/3, AVATAR_SIZE/3);

        g2.dispose();
        return img;
    }

    /**
     * 保存用户数据
     */
    private void saveUserData() {
        // 更新用户对象
        if (currentUser == null) {
            currentUser = new User();
        }

        currentUser.setUsername(usernameField.getText().trim());
        currentUser.setFullName(fullNameField.getText().trim());
        currentUser.setEmail(emailField.getText().trim());
        currentUser.setPhone(phoneField.getText().trim());
        currentUser.setAddress(addressField.getText().trim());
        currentUser.setBio(bioTextArea.getText().trim());

        // 保存用户数据
        boolean success = userService.saveUser(currentUser);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "User profile saved successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to save user profile.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//
}