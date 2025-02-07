package com.example.barcodebuddy.recyclerview;

public class BlogClass {

    private String blogName;
    private String blogAuthor;
    private String blogContent; // Changed 'BlogContent' to 'blogContent' (consistent naming)
    private String blogId;

    // Default constructor for Firebase deserialization
    public BlogClass() {
    }

    // Parameterized constructor
    public BlogClass(String blogName, String blogAuthor, String blogContent,String blogId) {
        this.blogName = blogName;
        this.blogAuthor = blogAuthor;
        this.blogContent = blogContent; // Changed 'BlogContent' to 'blogContent'
        this.blogId = blogId;
    }

    // Getters and Setters
    public String getBlogName() {
        return blogName;
    }

    public void setBlogName(String blogName) {
        this.blogName = blogName;
    }

    public String getBlogAuthor() {
        return blogAuthor;
    }

    public void setBlogAuthor(String blogAuthor) {
        this.blogAuthor = blogAuthor;
    }

    public String getBlogContent() {
        return blogContent;
    }

    public void setBlogContent(String blogContent) {
        this.blogContent = blogContent;
    }

    public String getBlogId() {
        return blogId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }
}
