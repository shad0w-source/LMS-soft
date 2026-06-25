package com.kkbau;

enum IssueStatus {
    ISSUED,
    RETURNED,
    OVERDUE
}

public class IssueBooks {
    int issue_id;
    int member_id;
    int book_id;
    String issue_date;
    String return_date;
    IssueStatus status;
    public IssueBooks(int issue_id, int member_id, int book_id, String issue_date, String return_date, IssueStatus status) {
        this.issue_id = issue_id;
        this.member_id = member_id;
        this.book_id = book_id;
        this.issue_date = issue_date;
        this.return_date = return_date;
        this.status = status;
    }

    public int getIssue_id() {
        return issue_id;
    }

    public void setIssue_id(int issue_id) {
        this.issue_id = issue_id;
    }

    public int getMember_id() {
        return member_id;
    }

    public void setMember_id(int member_id) {
        this.member_id = member_id;
    }

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    public String getIssue_date() {
        return issue_date;
    }

    public void setIssue_date(String issue_date) {
        this.issue_date = issue_date;
    }

    public String getReturn_date() {
        return return_date;
    }

    public void setReturn_date(String return_date) {
        this.return_date = return_date;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }
    @Override
    public String toString() {
        return "IssueBooks{" +
                "issue_id=" + issue_id +
                ", member_id=" + member_id +
                ", book_id=" + book_id +
                ", issue_date='" + issue_date + '\'' +
                ", return_date='" + return_date + '\'' +
                ", status='" + status + '\'' +
                '}';
    }    
}
