package com.example.project.managers;

import com.example.project.objects.Category;
import com.example.project.objects.Subcategory;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * The type Firebase manager.
 */
public class FirebaseManager
{

    /**
     * The constant categories ArrayList.
     */
    public static ArrayList<Category> categories = new ArrayList<>();
    /**
     * The constant subcategories ArrayList.
     */
    public static ArrayList<ArrayList<Subcategory>> subcategories = new ArrayList<>();

    /**
     * The constant categoriesStrings ArrayList.
     */
    public static ArrayList<String> categoriesStrings = new ArrayList<>();
    /**
     * The constant subcategoriesStrings ArrayList.
     */
    public static ArrayList<ArrayList<String>> subcategoriesStrings = new ArrayList<>();

    /**
     * The reference to Firebase FireStore.
     */
    public static FirebaseFirestore DBReference = FirebaseFirestore.getInstance();
    /**
     * The reference to Firebase FireStorage.
     */
    public static StorageReference FSReference = FirebaseStorage.getInstance().getReference();

    /**
     * Amount of documents in the home screen.
     */
    public static final int MAX_DOCUMENTS = 6;

    /**
     * Gets db reference.
     *
     * @return the db reference
     */
    public static FirebaseFirestore getDBReference()
    {
        return FirebaseManager.DBReference;
    }

    /**
     * Gets fs reference.
     *
     * @return the fs reference
     */
    public static StorageReference getFSReference()
    {
        return FirebaseManager.FSReference;
    }

}
