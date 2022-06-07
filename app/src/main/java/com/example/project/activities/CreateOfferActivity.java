package com.example.project.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project.R;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.FirebaseManager;
import com.example.project.managers.UserManager;
import com.example.project.objects.Offer;
import com.example.project.objects.Request;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.regex.Pattern;


public class CreateOfferActivity extends AppCompatActivity implements View.OnClickListener
{
    /**
     * Offer description edit text.
     */
    private EditText offerDescriptionET;
    /**
     * Offer price edit text.
     */
    private EditText offerPriceET;
    /**
     * Button for canceling the creation of the offer.
     */
    private Button cancelBtn;
    /**
     * Button for creating the offer.
     */
    private Button submitBtn;
    /**
     * The request that the offer belongs to.
     */
    private Request request;

    /**
     * The function Calls init().
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_offer_activity_layout);
        init();
    }

    /**
     * The function initializes all widgets and variables including buttons' listeners.
     */
    private void init()
    {
        request = (Request) getIntent().getSerializableExtra(ConstantsManager.REQUEST);

        offerDescriptionET = findViewById(R.id.coal_offer_description_edit_text);
        offerPriceET = findViewById(R.id.coal_offer_price_edit_text);
        cancelBtn = findViewById(R.id.cancel_button);
        submitBtn = findViewById(R.id.submit_button);

        cancelBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    /**
     * The function ends activity if cancelBtn is clicked.
     * The function calls uploadOffer() if submitBtn is clicked and areFieldsValid() returns true (offer's fields are valid).
     * @param view
     */
    @Override
    public void onClick(View view)
    {
        if(view.getId() == cancelBtn.getId())
        {
            Intent intent = new Intent();
            intent.putExtra(ConstantsManager.OFFER, (Offer) null);
            setResult(RESULT_OK, intent);
            finish();
        }
        else if(view.getId() == submitBtn.getId())
        {
            if(areFieldsValid())
            {
                uploadOffer();
            }
        }
    }

    /**
     * The function creates a new object of the current offer (with fields specified).
     * The function calls attemptOfferUpload(offer).
     */
    private void uploadOffer()
    {
        /**
         * Checking if the request still exists
         */
        FirebaseManager.getDBReference().collection(ConstantsManager.REQUESTS_COLLECTION)
                .whereEqualTo(ConstantsManager.REQUEST_REQUEST_ID_FIELD, request.getRequestId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                    {
                        if(queryDocumentSnapshots == null || queryDocumentSnapshots.isEmpty())
                        {
                            Intent intent = new Intent();
                            intent.putExtra(ConstantsManager.OFFER, (Offer) null);
                            setResult(RESULT_OK, intent);
                            finish();
                            return;
                        }
                        Offer offer = new Offer(UserManager.getCurrentUser().getUserId(), request.getRequestId(), "", Float.valueOf(offerPriceET.getText().toString()),
                                offerDescriptionET.getText().toString(), System.currentTimeMillis(), ConstantsManager.statesCodes[0],
                                ConstantsManager.statesCodes[1], ConstantsManager.statesCodes[1]);

                        attemptOfferUpload(offer);
                    }
                });
    }

    /**
     *
     * @param offer - desired offer.
     * The function takes the offer object and tries to upload it to firebase database.
     */
    private void attemptOfferUpload(Offer offer)
    {
        if(offer == null)
        {
            return;
        }
        FirebaseManager.getDBReference().collection(ConstantsManager.OFFERS_COLLECTION).add(offer.getOfferInfo())
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>()
                {
                    @Override
                    public void onSuccess(DocumentReference documentReference)
                    {
                        offer.setOfferId(documentReference.getId());
                        documentReference.update(offer.getOfferInfo());
                        Intent intent = new Intent();
                        intent.putExtra(ConstantsManager.OFFER, offer);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
    }

    /**
     *
     * @param string
     * @return whether the string only contains numbers.
     */
    private boolean containNumbers(String string)
    {
        return string.matches("[0-9.]*");
    }

    /**
     *
     * @return whether the offer's description and price fields are valid.
     */
    private boolean areFieldsValid()
    {
        if(offerDescriptionET.getText().toString().isEmpty())
        {
            offerDescriptionET.setError("Invalid description");
            return false;
        }
        else if(offerPriceET.getText().toString().isEmpty() || !containNumbers(offerPriceET.getText().toString()))
        {
            offerPriceET.setError("Invalid price");
            return false;
        }
        return true;
    }
}
