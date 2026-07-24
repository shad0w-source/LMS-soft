package com.kkbau;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;

public class MemberManagementPanel extends JPanel implements SearchablePanel {

    private static final Color COLOR_BG = new Color(245, 247, 250);
    private static final Color COLOR_CARD_BG = Color.WHITE;
    private static final Color COLOR_PRIMARY = new Color(11, 37, 69);
    private static final Color COLOR_TEXT_MAIN = new Color(17, 24, 39);
    private static final Color COLOR_TEXT_MUTED = new Color(107, 114, 128);
    private static final Color COLOR_BORDER = new Color(243, 244, 246);
    private static final Color COLOR_SECONDARY_BTN = new Color(212, 221, 247);

    List<Member> memberList = new ArrayList<>();
    private JTable table;
    private DefaultTableModel model;
    private MemberModel mm;

    public MemberManagementPanel(MemberModel memberModel) {
        this.mm = memberModel;
        setLayout(new BorderLayout(0, 20));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(Color.WHITE);
        page.setBorder(BorderFactory.createLineBorder(new Color(230, 233, 238), 1));

        JPanel subHeader = new JPanel(new BorderLayout());
        subHeader.setBackground(Color.WHITE);
        subHeader.setBorder(new EmptyBorder(25, 25, 15, 25));

        JLabel titleLabel = new JLabel("Member Directory");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(COLOR_PRIMARY);

        JButton btnRegister = new JButton("+ Register New Member") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnRegister.setOpaque(false);
        btnRegister.setContentAreaFilled(false);
        btnRegister.setBorderPainted(false);
        btnRegister.setFocusPainted(false);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRegister.setBorder(new EmptyBorder(10, 20, 10, 20));

        btnRegister.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            AddMemberDialog dialog = new AddMemberDialog(parentFrame);
            dialog.setVisible(true);

            if (dialog.isSucceeded()) {
                String name = dialog.getMemberName();
                String email = dialog.getEmailAddress();
                try {
                    mm.insertMember(name, email);
                    refreshMemberTable();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }
        });

        subHeader.add(titleLabel, BorderLayout.WEST);
        subHeader.add(btnRegister, BorderLayout.EAST);
        page.add(subHeader, BorderLayout.NORTH);

        String[] columns = {"MEMBER", "MEMBER ID", "EMAIL ADDRESS", "Actions"};

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; 
            }
        };

        table = new JTable(model);
        loadMembers();
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
                return label;
            }
        });

        table.getColumnModel().getColumn(3).setCellRenderer(new ActionButtonsRendererOrEditor());
        table.getColumnModel().getColumn(3).setCellEditor(new ActionButtonsRendererOrEditor());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(new EmptyBorder(0, 10, 10, 10));

        page.add(scrollPane, BorderLayout.CENTER);
        add(page, BorderLayout.CENTER);
    }

    private class ActionButtonsRendererOrEditor extends AbstractCellEditor implements TableCellEditor, javax.swing.table.TableCellRenderer {

        private final JPanel renderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 16));
        private final JPanel editPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 16));
        private final JButton btnRender = new JButton("Edit/Delete");
        private final JButton btnEdit = new JButton("Edit/Delete");

        public ActionButtonsRendererOrEditor() {
            styleButton(btnRender);
            renderPanel.setBackground(Color.WHITE);
            renderPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
            renderPanel.add(btnRender);

            styleButton(btnEdit);
            editPanel.setBackground(Color.WHITE);
            editPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
            editPanel.add(btnEdit);

            btnEdit.addActionListener(e -> {
                int row = table.convertRowIndexToModel(table.getEditingRow());

                if (row == -1) {
                    row = table.convertRowIndexToModel(table.getSelectedRow());
                }

                if (row != -1 && row < memberList.size()) {
                    Member selectedMember = memberList.get(row);
                    int memberId = selectedMember.getId();
                    String rawName = selectedMember.getName();
                    String currentEmail = selectedMember.getEmail();

                    fireEditingStopped();

                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(MemberManagementPanel.this);
                    EditMemberDialog editDialog = new EditMemberDialog(parentFrame, rawName, currentEmail);
                    editDialog.setVisible(true);

                    if (editDialog.isDeleteRequested()) {
                        try {
                            mm.deleteMember(memberId);
                            refreshMemberTable();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    } else if (editDialog.isEditSucceeded()) {
                        String updatedName = editDialog.getMemberName();
                        String updatedEmail = editDialog.getEmailAddress();
                        try {
                            mm.updateMember(memberId, updatedName, updatedEmail);
                            refreshMemberTable();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                } else {
                    fireEditingStopped();
                }
            });
        }

        private void styleButton(JButton btn) {
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.setForeground(COLOR_PRIMARY);
            btn.setBackground(COLOR_SECONDARY_BTN);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object val, boolean isS, boolean hasF, int r, int c) {
            return renderPanel;
        }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object val, boolean isS, int r, int c) {
            return editPanel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    private void loadMembers() {
        model.setRowCount(0);
        memberList = mm.findAllMembers();

        for (Member m : memberList) {
            model.addRow(new Object[]{
                m.getName(),
                String.valueOf(m.getId()),
                m.getEmail(),
                ""
            });
        }
    }

    public void refreshMemberTable() {
        loadMembers();
    }

    @Override
    public void search(String query) {
        model.setRowCount(0);

        if (query == null || query.trim().isEmpty() || "Search".equalsIgnoreCase(query.trim())) {
            loadMembers();
            return;
        }

        String lowerQuery = query.toLowerCase().trim();

        for (Member m : memberList) {
            if (m.getName().toLowerCase().contains(lowerQuery) || 
                m.getEmail().toLowerCase().contains(lowerQuery) ||
                String.valueOf(m.getId()).contains(lowerQuery)) {
                
                model.addRow(new Object[]{
                    m.getName(),
                    String.valueOf(m.getId()),
                    m.getEmail(),
                    ""
                });
            }
        }
    }
}