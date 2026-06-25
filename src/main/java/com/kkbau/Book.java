package com.kkbau;

public class Book {
    int id;
    String title;
    String author;
    int stock;
    String addedDate; // 1. Added this field

    // 2. Updated constructor to include addedDate
    public Book(int id, String title, String author, int stock, String addedDate) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.stock = stock;
        this.addedDate = addedDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    // 3. Added getter and setter for addedDate
    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    // 4. Updated toString() method
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", stock=" + stock +
                ", addedDate='" + addedDate + '\'' +
                '}';
    }
}