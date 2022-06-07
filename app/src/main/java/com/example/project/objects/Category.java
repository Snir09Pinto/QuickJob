package com.example.project.objects;

import java.io.Serializable;

/**
 * The category object class.
 */
public class Category implements Serializable
{
    /**
     * Category title.
     */
    private String title;
    /**
     * Category id.
     */
    private String categoryId;
    /**
     * Category image.
     */
    private String categoryImage;

    /**
     * Empty constructor for firebase.
     */
    public Category(){

    }


    /**
     * Sets the member variables to the given parameters.
     * @param title - category title.
     * @param categoryId - category id.
     * @param categoryImage - category image.
     */
    public Category(String title, String categoryId, String categoryImage)
    {
        this.title = title;
        this.categoryId = categoryId;
        this.categoryImage = categoryImage;
    }

    /**
     *
     * @return category's title.
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @return category's id.
     */
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * Sets member variable "title" to given title (parameter).
     * @param title
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Sets member variable "categoryId" to given categoryId (parameter).
     * @param categoryId
     */
    public void setCategoryId(String categoryId)
    {
        this.categoryId = categoryId;
    }

    /**
     *
     * @return category's image.
     */
    public String getCategoryImage() {
        return categoryImage;
    }

    /**
     * Sets member variable "categoryImage" to given categoryImage (parameter).
     * @param categoryImage
     */
    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }
}

