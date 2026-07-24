package com.kkbau;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class AddMemberDialog extends JDialog {
    private JTextField txtName;
    private JTextField txtEmail;
    private boolean succeeded = false;

    private String memberName;
    private String emailAddress;

    public AddMemberDialog(Frame parent) {
        super(parent, "Register New Member", true);
        setLayout(new BorderLayout());
        setSize(400, 360); 
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel headerLabel = new JLabel("Register New Member");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(new Color(15, 32, 67)); // COLOR_BUTTON_BG
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(headerLabel);
        formPanel.add(Box.createVerticalStrut(25));

        // Member Name
        formPanel.add(createFieldLabel("MEMBER NAME"));
        txtName = createStyledTextField();
        formPanel.add(txtName);
        formPanel.add(Box.createVerticalStrut(15));

        // Email Address
        formPanel.add(createFieldLabel("EMAIL ADDRESS"));
        txtEmail = createStyledTextField();
        formPanel.add(txtEmail);
        formPanel.add(Box.createVerticalStrut(30));

        // Action Buttons
        JPanel actionsRow = new JPanel(new GridLayout(1, 2, 15, 0));
        actionsRow.setBackground(Color.WHITE);
        actionsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        actionsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnSave = createStyledButton("Register", new Color(15, 32, 67), Color.WHITE);
        JButton btnCancel = createStyledButton("Cancel", new Color(241, 245, 249), new Color(107, 114, 128));

        btnSave.addActionListener(e -> {
            if (validateForm()) {
                memberName = txtName.getText().trim();
                emailAddress = txtEmail.getText().trim();
                succeeded = true;
                dispose();
            }
        });

        btnCancel.addActionListener(e -> dispose());

        actionsRow.add(btnSave);
        actionsRow.add(btnCancel);
        formPanel.add(actionsRow);

        add(formPanel, BorderLayout.CENTER);
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(new Color(148, 163, 184)); 
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setBackground(new Color(241, 245, 249)); 
        field.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private boolean validateForm() {
        if (txtName.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Email are required fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean isSucceeded() { return succeeded; }
    public String getMemberName() { return memberName; }
    public String getEmailAddress() { return emailAddress; }
}