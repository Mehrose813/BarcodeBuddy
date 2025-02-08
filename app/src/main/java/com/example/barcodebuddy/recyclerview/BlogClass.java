package com.example.barcodebuddy.recyclerview;

public class BlogClass {

    private String blogName;
    private String blogAuthor;
    private String blogContent;
    private String blogId;
    private String blogImage; // Added for storing the image in Base64
    private String blogLink;
    private String blogTitle;

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public BlogClass() {
    }

    public String getBlogLink() {
        return blogLink;
    }

    public void setBlogLink(String blogLink) {
        this.blogLink = blogLink;
    }

    public BlogClass(String blogName, String blogAuthor, String blogContent, String blogId, String blogImage, String blogLink) {
        this.blogName = blogName;
        this.blogAuthor = blogAuthor;
        this.blogContent = blogContent;
        this.blogId = blogId;
        this.blogImage = blogImage;
        this.blogLink = blogLink;
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

    public String getBlogImage() {
        return blogImage;
    }

    public void setBlogImage(String blogImage) {
        this.blogImage = blogImage;
    }
}
