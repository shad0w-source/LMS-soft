package com.kkbau;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

public class LibraryDashboard extends JFrame {

    private final Color COLOR_SIDEBAR_BG = new Color(245, 247, 250);
    private final Color COLOR_CONTENT_BG = new Color(238, 241, 245);
    private final Color COLOR_PRIMARY_NAV = new Color(232, 240, 254);
    private final Color COLOR_NAV_TEXT_ACTIVE = new Color(26, 84, 184);
    private final Color COLOR_TEXT_MAIN = new Color(17, 24, 39);
    private final Color COLOR_TEXT_MUTED = new Color(107, 114, 128);
    private final Color COLOR_BUTTON_BG = new Color(15, 32, 67);
    private static final Color COLOR_SECONDARY_BTN = new Color(212, 221, 247); 

    // Navigation and Panels
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JPanel[] navButtons;
    private String[] panelNames = {"Book Catalog", "Member Management", "Borrowing/Returns"};

    ArrayList<Book> books = new ArrayList<>();
    private JTable table;
    private DefaultTableModel model;
    private BookModel bm;
    private MemberModel mm;
    private IssueBooksModel ibm;

    public LibraryDashboard(Connection conn) {
        try {
            bm = new BookModel(conn);
            mm = new MemberModel(conn);
            ibm = new IssueBooksModel(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to initialize models with database connection.", "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        setTitle("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);

        // Main Layout Split: Sidebar vs Content
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_CONTENT_BG);

        // Create Sidebar
        JPanel sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        // Create Top Search Bar Area
        JPanel topHeader = createTopHeader();

        // Create Multi-page Content Panel (CardLayout)
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(COLOR_CONTENT_BG);
        cardPanel.setBorder(new EmptyBorder(0, 30, 30, 30));

        // Add Pages
        cardPanel.add(createBookCatalogPage(bm), panelNames[0]);
        cardPanel.add(new MemberManagementPanel(mm), panelNames[1]);
        cardPanel.add(new TransactionManagementPanel(ibm, this), panelNames[2]);

        // Right side wrapper to hold Header + Dynamic Content View
        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.setBackground(COLOR_CONTENT_BG);
        rightContainer.add(topHeader, BorderLayout.NORTH);
        rightContainer.add(cardPanel, BorderLayout.CENTER);

        mainPanel.add(rightContainer, BorderLayout.CENTER);
        add(mainPanel);

        // Highlight first navigation item by default
        setTabActive(0);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(250, 750));
        sidebar.setBackground(COLOR_SIDEBAR_BG);
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        // App Title
        JLabel appTitle = new JLabel("<html><b>Library Management</b><br>System</html>");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appTitle.setForeground(COLOR_BUTTON_BG);
        appTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(appTitle);
        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));

        // Navigation Menu Container
        navButtons = new JPanel[panelNames.length];
        for (int i = 0; i < panelNames.length; i++) {
            final int index = i;
            JPanel navItem = new JPanel(new BorderLayout());
            navItem.setMaximumSize(new Dimension(210, 45));
            navItem.setPreferredSize(new Dimension(210, 45));
            navItem.setBackground(COLOR_SIDEBAR_BG);
            navItem.setBorder(new EmptyBorder(0, 15, 0, 15));
            navItem.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel navLabel = new JLabel(panelNames[i]);
            navLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            navLabel.setForeground(COLOR_TEXT_MUTED);
            navItem.add(navLabel, BorderLayout.CENTER);

            navItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    setTabActive(index);
                    cardLayout.show(cardPanel, panelNames[index]);
                }
            });

            navButtons[i] = navItem;
            sidebar.add(navItem);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        return sidebar;
    }

    private JPanel createTopHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_CONTENT_BG);
        header.setPreferredSize(new Dimension(950, 80));
        header.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Mimic the search field from UI
        JTextField searchField = new JTextField("Search");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setForeground(Color.GRAY); 
        searchField.setPreferredSize(new Dimension(400, 40));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 233, 238), 1),
                BorderFactory.createEmptyBorder(0, 15, 0, 15)
        ));

        searchField.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("Search")) {
                    searchField.setText("");
                    searchField.setForeground(new Color(30, 41, 59)); 
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search");
                    searchField.setForeground(Color.GRAY); 
                }
            }
        });

        JPanel searchWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchWrapper.setBackground(COLOR_CONTENT_BG);
        searchWrapper.setFocusable(true); 
        searchWrapper.add(searchField);

        header.add(searchWrapper, BorderLayout.WEST);

        SwingUtilities.invokeLater(() -> {
            searchWrapper.requestFocusInWindow();
        });

        return header;
    }

    private void refreshBooksFromDatabase(BookModel bm) {
        books.clear();
        books.addAll(bm.findAllBooks());
    }

    private void loadBooks(BookModel bm) {
    books = bm.findAllBooks();
    model.setRowCount(0); 

    for (Book b : books) {
        // We use b.getId() so the UI displays the database primary key.
        // This ensures the "Book ID" typed into the Transaction panel 
        // will always match the ID the user sees in the table.
        model.addRow(new Object[]{
            String.valueOf(b.getId()), // Column 0: Actual Database ID
            "<html><b>" + b.getTitle() + "</b><br><span style='font-size:11px; color:#6B7280;'>" + b.getAuthor() + "</span></html>",    // Column 1
            b.getStock() + " Copies",                                        // Column 2
            b.getAddedDate(),                                                // Column 3
            ""                                                               // Column 4
        });
    }
}

    private JPanel createBookCatalogPage(BookModel bm) {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(Color.WHITE);
        page.setBorder(BorderFactory.createLineBorder(new Color(230, 233, 238), 1));

        JPanel subHeader = new JPanel(new BorderLayout());
        subHeader.setBackground(Color.WHITE);
        subHeader.setBorder(new EmptyBorder(25, 25, 15, 25));

        JLabel titleLabel = new JLabel("Library Index");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(COLOR_BUTTON_BG);

        JButton btnAddBook = new JButton("Add New Book") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_BUTTON_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g); 
            }
        };

        btnAddBook.setOpaque(false);
        btnAddBook.setContentAreaFilled(false);
        btnAddBook.setBorderPainted(false);
        btnAddBook.setFocusPainted(false);
        btnAddBook.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAddBook.setForeground(Color.WHITE); 
        btnAddBook.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAddBook.setBorder(new EmptyBorder(10, 20, 10, 20));

        btnAddBook.addActionListener(e -> {
            AddBookDialog dialog = new AddBookDialog(LibraryDashboard.this);
            dialog.setVisible(true);

            if (dialog.isSucceeded()) {
                String title = dialog.getBookTitle();
                String author = dialog.getAuthorName();
                int stock = dialog.getStock();

                boolean isSaved = bm.saveNewBook(title, author, stock);

                if (isSaved) {
                    JOptionPane.showMessageDialog(LibraryDashboard.this,
                            "Book saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshBooksFromDatabase(bm);
                    loadBooks(bm);
                    revalidate();
                    repaint();
                } else {
                    JOptionPane.showMessageDialog(LibraryDashboard.this,
                            "Failed to save book to database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        subHeader.add(titleLabel, BorderLayout.WEST);
        subHeader.add(btnAddBook, BorderLayout.EAST);
        page.add(subHeader, BorderLayout.NORTH);

        // Updated 5-Column structural title header tags
        String[] columns = {"ID", "BOOK TITLE & AUTHOR", "STOCK QUANTITY", "ADDED DATE", "ACTIONS"};

        // Target index 4 explicitly for editing interactions (Actions column)
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        table = new JTable(model);
        refreshBooksFromDatabase(bm);
        loadBooks(bm);

        table.setRowHeight(65);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(Color.WHITE);

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setForeground(COLOR_TEXT_MUTED);
        table.getTableHeader().setPreferredSize(new Dimension(100, 40));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(243, 244, 246)));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isS, boolean hasF, int r, int c) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, val, isS, hasF, r, c);
                label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                label.setBorder(new EmptyBorder(0, 25, 0, 10));
                label.setForeground(COLOR_TEXT_MAIN);
                label.setBackground(Color.WHITE);
                return label;
            }
        });

        // Safe dynamic renderer mapping targeting column index 4 (Actions)
        table.getColumnModel().getColumn(4).setCellRenderer(new ActionButtonsRendererOrEditor());
        table.getColumnModel().getColumn(4).setCellEditor(new ActionButtonsRendererOrEditor());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(new EmptyBorder(0, 10, 10, 10));

        page.add(scrollPane, BorderLayout.CENTER);
        return page;
    }

    private class ActionButtonsRendererOrEditor extends AbstractCellEditor implements TableCellEditor, javax.swing.table.TableCellRenderer {
        private final JPanel renderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 16));
        private final JPanel editPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 16));
        private final JButton btnRender = new JButton("Edit/Delete");
        private final JButton btnEdit = new JButton("Edit/Delete");

        public ActionButtonsRendererOrEditor() {
            styleButton(btnRender);
            renderPanel.setBackground(Color.WHITE);
            renderPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(243, 244, 246)));
            renderPanel.add(btnRender);

            styleButton(btnEdit);
            editPanel.setBackground(Color.WHITE);
            editPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(243, 244, 246)));
            editPanel.add(btnEdit);

            btnEdit.addActionListener(e -> {
                int row = table.convertRowIndexToModel(table.getEditingRow());
                if (row == -1) {
                    row = table.convertRowIndexToModel(table.getSelectedRow());
                }

                if (row != -1 && row < books.size()) {
                    Book selectedBook = books.get(row);
                    int bookId = selectedBook.getId();
                    String currentTitle = selectedBook.getTitle();
                    String currentAuthor = selectedBook.getAuthor();

                    fireEditingStopped();

                    EditBookDialog editDialog = new EditBookDialog(LibraryDashboard.this, currentTitle, currentAuthor);
                    editDialog.setVisible(true);

                    if (editDialog.isDeleteRequested()) {
                        try {
                            bm.deleteBook(bookId);
                            refreshBooksFromDatabase(bm);
                            loadBooks(bm);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    } else if (editDialog.isEditSucceeded()) {
                        String updatedTitle = editDialog.getBookName();
                        String updatedAuthor = editDialog.getAuthorName();
                        try {
                            bm.updateBook(bookId, updatedTitle, updatedAuthor);
                            refreshBooksFromDatabase(bm);
                            loadBooks(bm);
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
            btn.setForeground(COLOR_BUTTON_BG);
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

    private JPanel createPlaceholderPage(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(230, 233, 238), 1));

        JLabel label = new JLabel(title + " View Window");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        label.setForeground(COLOR_TEXT_MUTED);
        panel.add(label);
        return panel;
    }

    private void setTabActive(int index) {
        for (int i = 0; i < navButtons.length; i++) {
            JLabel lbl = (JLabel) navButtons[i].getComponent(0);
            if (i == index) {
                navButtons[i].setBackground(COLOR_PRIMARY_NAV);
                lbl.setForeground(COLOR_NAV_TEXT_ACTIVE);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            } else {
                navButtons[i].setBackground(COLOR_SIDEBAR_BG);
                lbl.setForeground(COLOR_TEXT_MUTED);
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            }
        }
        repaint();
    }

    /**
     * Public gateway allowing sub-panels to trigger a synchronized data redraw 
     * for Book list elements when quantities fluctuate.
     */
    public void refreshCatalogData() {
        if (bm != null) {
            refreshBooksFromDatabase(bm);
            loadBooks(bm);
        }
    }
}