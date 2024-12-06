package org.example;

public class Reviews {
    private final int reviewID;
    private final int rating;
    private final String comment;
    private final int accountID;

    public Reviews(int reviewID, int rating, String comment, int accountID) {
        this.rating = rating;
        this.comment = comment;
        this.reviewID = reviewID;
        this.accountID = accountID;
    }

    public int getReviewID() {
        return reviewID;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public int getAccountID() {
        return accountID;
    }
}
