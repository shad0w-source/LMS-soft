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

public class AddBookDialog extends JDialog {
    private JTextField txtTitle;
    private JTextField txtAuthor;
    private JTextField txtStock;
    private boolean succeeded = false;

    private String bookTitle;
    private String authorName;
    private int stock;

    public AddBookDialog(Frame parent) {
        super(parent, "Add New Book", true); 
        setLayout(new BorderLayout());
        setSize(400, 450);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel headerLabel = new JLabel("Create New Book");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(new Color(15, 32, 67)); // COLOR_BUTTON_BG
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(headerLabel);
        formPanel.add(Box.createVerticalStrut(25));

        // Book Title
        formPanel.add(createFieldLabel("BOOK TITLE"));
        txtTitle = createStyledTextField("e.g. The Bauhaus Manual");
        formPanel.add(txtTitle);
        formPanel.add(Box.createVerticalStrut(15));

        // Author Name
        formPanel.add(createFieldLabel("AUTHOR NAME"));
        txtAuthor = createStyledTextField("e.g. Walter Gropius");
        formPanel.add(txtAuthor);
        formPanel.add(Box.createVerticalStrut(15));

        // Stock Count
        formPanel.add(createFieldLabel("INITIAL STOCK"));
        txtStock = createStyledTextField("e.g. 5");
        formPanel.add(txtStock);
        formPanel.add(Box.createVerticalStrut(30));

        // Action Buttons 
        JPanel actionsRow = new JPanel(new GridLayout(1, 2, 15, 0));
        actionsRow.setBackground(Color.WHITE);
        actionsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        actionsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnSave = createStyledButton("Save Book", new Color(15, 32, 67), Color.WHITE);
        JButton btnCancel = createStyledButton("Cancel", new Color(241, 245, 249), new Color(107, 114, 128));

        btnSave.addActionListener(e -> {
            if (validateForm()) {
                bookTitle = txtTitle.getText().trim();
                authorName = txtAuthor.getText().trim();
                stock = Integer.parseInt(txtStock.getText().trim());
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
        label.setForeground(new Color(148, 163, 184)); // Muted text gray
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setBackground(new Color(241, 245, 249)); // Soft field gray background
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
        if (txtTitle.getText().trim().isEmpty() || txtAuthor.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and Author are required fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            int val = Integer.parseInt(txtStock.getText().trim());
            if (val < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Stock must be a valid positive integer.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean isSucceeded() { return succeeded; }
    public String getBookTitle() { return bookTitle; }
    public String getAuthorName() { return authorName; }
    public int getStock() { return stock; }
}
