package com.example.barcodebuddy.recyclerview;

public class BlogClass {

    String blogName, blogAuthor, BlogContent, blogId;

    public BlogClass(String blogName, String blogAuthor, String BlogContent, String blogId) {
        this.blogName = blogName;
        this.blogAuthor = blogAuthor;
        this.BlogContent = BlogContent;
        this.blogId = blogId;
    }

    public BlogClass() { // Default constructor for Firebase deserialization
    }

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
        return BlogContent;
    }

    public void setBlogContent(String blogContent) {
        BlogContent = blogContent;
    }

    public String getBlogId() {
        return blogId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }
}
