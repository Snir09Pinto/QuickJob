package com.example.project.objects;

import java.io.Serializable;

/**
 * The subcategory object class
 */
public class Subcategory implements Serializable
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
    private String subcategoryImage;

    /**
     * Subcategory id.
     */
    private String subcategoryId;

    /**
     * Empty constructor for firebase.
     */



    public Subcategory()
    {

    }


    /**
     * Sets the member variables to the given parameters.
     * @param title
     * @param categoryId
     * @param subcategoryId
     * @param subcategoryImage
     */
    public Subcategory(String title, String categoryId, String subcategoryId, String subcategoryImage) {
        this.title = title;
        this.categoryId = categoryId;
        this.subcategoryId = subcategoryId;
        this.subcategoryImage = subcategoryImage;
    }

    /**
     * Sets member variable "subcategoryId" to given subcategoryId (parameter).
     * @param subcategoryId
     */
    public void setSubcategoryId(String subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    /**
     *
     * @return subcategoryId
     */
    public String getSubcategoryId() {
        return subcategoryId;
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
     * @return subcategory's image.
     */
    public String getSubcategoryImage() {
        return subcategoryImage;
    }

    /**
     * Sets member variable "subcategoryImage" to given subcategoryImage (parameter).
     * @param subcategoryImage
     */
    public void setSubcategoryImage(String subcategoryImage)
    {
        this.subcategoryImage = subcategoryImage;
    }
}
