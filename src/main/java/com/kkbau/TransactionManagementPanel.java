package com.kkbau;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class TransactionManagementPanel extends JPanel implements SearchablePanel {

    private static final Color COLOR_BG = new Color(245, 247, 250);
    private static final Color COLOR_CARD_BG = Color.WHITE;
    private static final Color COLOR_PRIMARY = new Color(11, 37, 69);
    private static final Color COLOR_SECONDARY_BTN = new Color(212, 221, 247);
    private static final Color COLOR_TEXT_MAIN = new Color(33, 37, 41);
    private static final Color COLOR_TEXT_MUTED = new Color(140, 150, 165);
    private static final Color COLOR_BORDER = new Color(235, 238, 243);
    private static final Color COLOR_INPUT_BG = new Color(241, 243, 246);

    private IssueBooksModel ibm;
    private LibraryDashboard dashboard;
    private JTable table;
    private DefaultTableModel model;

    public TransactionManagementPanel(IssueBooksModel issueBooksModel, LibraryDashboard dashboard) {
        this.ibm = issueBooksModel;
        this.dashboard = dashboard;

        setLayout(new BorderLayout(25, 0));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel leftFormPanel = new JPanel(new BorderLayout(0, 20));
        leftFormPanel.setBackground(COLOR_CARD_BG);
        leftFormPanel.setPreferredSize(new Dimension(300, 0));
        leftFormPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(25, 20, 25, 20)
        ));

        JLabel formTitle = new JLabel("Process Transaction");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formTitle.setForeground(COLOR_PRIMARY);
        leftFormPanel.add(formTitle, BorderLayout.NORTH);

        JPanel fieldsContainer = new JPanel(new GridLayout(4, 1, 0, 10));
        fieldsContainer.setBackground(COLOR_CARD_BG);

        JLabel lblMemberId = new JLabel("MEMBER ID");
        lblMemberId.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblMemberId.setForeground(COLOR_TEXT_MUTED);
        JTextField txtMemberId = createPlaceholderField("Enter Member ID");

        JLabel lblBookId = new JLabel("BOOK ID");
        lblBookId.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblBookId.setForeground(COLOR_TEXT_MUTED);
        JTextField txtBookId = createPlaceholderField("Enter Book ID");

        fieldsContainer.add(lblMemberId);
        fieldsContainer.add(txtMemberId);
        fieldsContainer.add(lblBookId);
        fieldsContainer.add(txtBookId);

        leftFormPanel.add(fieldsContainer, BorderLayout.CENTER);

        JPanel btnActionRow = new JPanel(new GridLayout(1, 2, 12, 0));
        btnActionRow.setBackground(COLOR_CARD_BG);
        btnActionRow.setPreferredSize(new Dimension(0, 42));

        JButton btnIssue = createRoundedButton("Issue", COLOR_PRIMARY, Color.WHITE);
        JButton btnReturn = createRoundedButton("Return", COLOR_SECONDARY_BTN, COLOR_PRIMARY);

        btnIssue.addActionListener(e -> {
            String memberId = txtMemberId.getText().trim();
            String bookId = txtBookId.getText().trim();

            if (memberId.isEmpty() || "Enter Member ID".equals(memberId)
                    || bookId.isEmpty() || "Enter Book ID".equals(bookId)) {
                JOptionPane.showMessageDialog(this, "Please enter both Member ID and Book ID.", "Input Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean success = ibm.issueBook(memberId, bookId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Book issued successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadIssuedRecords();

                if (dashboard != null) {
                    dashboard.refreshCatalogData();
                }

                resetField(txtMemberId, "Enter Member ID");
                resetField(txtBookId, "Enter Book ID");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to issue book. Check ID validity or stock availability.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnReturn.addActionListener(e -> {
            String memberId = txtMemberId.getText().trim();
            String bookId = txtBookId.getText().trim();

            if (memberId.isEmpty() || "Enter Member ID".equals(memberId)
                    || bookId.isEmpty() || "Enter Book ID".equals(bookId)) {
                JOptionPane.showMessageDialog(this, "Please enter both Member ID and Book ID to process return.", "Input Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean success = ibm.returnBook(memberId, bookId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Book returned successfully! Issue record removed from logs.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadIssuedRecords();

                if (dashboard != null) {
                    dashboard.refreshCatalogData();
                }

                resetField(txtMemberId, "Enter Member ID");
                resetField(txtBookId, "Enter Book ID");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to process return. Verify matching active issuance entry.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnActionRow.add(btnIssue);
        btnActionRow.add(btnReturn);
        leftFormPanel.add(btnActionRow, BorderLayout.SOUTH);

        add(leftFormPanel, BorderLayout.WEST);

        JPanel rightCardPanel = new JPanel(new BorderLayout(0, 15));
        rightCardPanel.setBackground(COLOR_CARD_BG);
        rightCardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JLabel sectionTitle = new JLabel("Currently Issued Records");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sectionTitle.setForeground(COLOR_PRIMARY);
        rightCardPanel.add(sectionTitle, BorderLayout.NORTH);

        String[] columns = {"MEMBER", "BOOK TITLE", "ISSUE DATE"};

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(65);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(Color.WHITE);
        header.setForeground(COLOR_TEXT_MUTED);
        header.setPreferredSize(new Dimension(100, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isS, boolean hasF, int r, int c) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, val, isS, hasF, r, c);
                label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                label.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 10));
                label.setForeground(COLOR_TEXT_MAIN);
                label.setBackground(Color.WHITE);

                if (c == 0) {
                    label.setForeground(COLOR_PRIMARY);
                    label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                }
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(new EmptyBorder(0, 10, 10, 10));

        rightCardPanel.add(scrollPane, BorderLayout.CENTER);
        add(rightCardPanel, BorderLayout.CENTER);

        loadIssuedRecords();
    }

    private void loadIssuedRecords() {
        model.setRowCount(0);
        ArrayList<Object[]> liveRecords = ibm.findAllIssuedRecordsForTable();
        for (Object[] row : liveRecords) {
            if (row.length >= 3) {
                model.addRow(new Object[]{row[0], row[1], row[2]});
            } else {
                model.addRow(row);
            }
        }
    }

    private void resetField(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(COLOR_TEXT_MUTED);
    }

    private JTextField createPlaceholderField(final String placeholder) {
        final JTextField tf = new JTextField(placeholder);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(COLOR_INPUT_BG);
        tf.setForeground(COLOR_TEXT_MUTED);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));

        tf.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder)) {
                    tf.setText("");
                    tf.setForeground(COLOR_TEXT_MAIN);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (tf.getText().trim().isEmpty()) {
                    tf.setText(placeholder);
                    tf.setForeground(COLOR_TEXT_MUTED);
                }
            }
        });

        return tf;
    }

    private JButton createRoundedButton(String text, Color bg, Color fg) {
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
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(fg);
        return btn;
    }

    @Override
    public void search(String query) {
        if (model == null) {
            return;
        }

        model.setRowCount(0);

        // Treat placeholder value or empty string as an empty query
        if (query == null || query.trim().isEmpty() || "Search".equalsIgnoreCase(query.trim())) {
            loadIssuedRecords();
            return;
        }

        String lowerQuery = query.toLowerCase().trim();
        ArrayList<Object[]> liveRecords = ibm.findAllIssuedRecordsForTable();

        for (Object[] row : liveRecords) {
            if (row.length >= 3) {
                String member = String.valueOf(row[0]).toLowerCase();
                String bookTitle = String.valueOf(row[1]).toLowerCase();
                String issueDate = String.valueOf(row[2]).toLowerCase();

                if (member.contains(lowerQuery) || bookTitle.contains(lowerQuery) || issueDate.contains(lowerQuery)) {
                    model.addRow(new Object[]{row[0], row[1], row[2]});
                }
            }
        }
    }
}