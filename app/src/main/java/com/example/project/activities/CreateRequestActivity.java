package com.example.project.activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.adevinta.leku.LocationPickerActivity;
import com.adevinta.leku.LocationPickerActivityKt;
import com.example.project.broadcasts.SendNotificationReceiver;
import com.example.project.dialogs.ImagePickerActionDialog;
import com.example.project.managers.ConstantsManager;
import com.example.project.managers.DateAndTimeManager;
import com.example.project.managers.DialogsManager;
import com.example.project.managers.FirebaseManager;
import com.example.project.managers.ImageManager;
import com.example.project.managers.MapsManager;
import com.example.project.managers.UserManager;
import com.example.project.objects.Category;
import com.example.project.objects.OfflineRequest;
import com.example.project.objects.OnlineRequest;
import com.example.project.objects.Request;
import com.example.project.objects.Subcategory;
import com.example.project.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Create a request activity.
 */
public class CreateRequestActivity extends AppCompatActivity implements View.OnClickListener
{
    /**
     * A button for canceling the creation of new request.
     */
    private Button cancelBtn;
    /**
     * A button for creating a new request.
     */
    private Button submitBtn;
    /**
     * Request description edit text.
     */
    private EditText requestDescriptionET;
    /**
     * Request title edit text.
     */
    private EditText requestTitleET;
    /**
     * Request date text view (day,month,year).
     */
    private TextView dateTV;
    /**
     * Request time text view (hour, minute).
     */
    private TextView timeTV;
    /**
     * A button for picking an image for the request.
     */
    private Button pickImageBtn;
    /**
     * A button for picking a location for the request.
     */
    private Button pickLocationBtn;
    /**
     * A button for picking a date for the request.
     */
    private Button pickDateBtn;
    /**
     * A button for picking a time for the request.
     */
    private Button pickTimeBtn;
    /**
     * An adapter of the category spinner.
     */
    private ArrayAdapter<String> categoryAdapter;
    /**
     * An adapter of the subcategory spinner.
     */
    private ArrayAdapter<String> subcategoryAdapter;
    /**
     * Category spinner.
     */
    private Spinner categorySpinner;
    /**
     * Subcategory spinner.
     */
    private Spinner subcategorySpinner;
    /**
     * The calendar contains all request time and date details (being updated while filling the fields).
     */
    private Calendar calendar;
    /**
     * Current chosen request location.
     */
    private LatLng latLng;
    /**
     * Checked - user has chosen an offline request (enable to choose a location).
     * Unchecked - user has chosen an online request.
     */
    private CheckBox locationCB;
    /**
     * Checked - user has chosen an online request (enable to choose a link).
     * Unchecked - user has chosen an online request without a link.
     */
    private CheckBox linkCB;
    /**
     * The request link edit text.
     */
    private EditText linkET;
    /**
     * The request image uri - from gallery.
     */
    private Uri imageUri;
    /**
     * The request image bitmap - from camera.
     */
    private Bitmap imageBitmap;
    /**
     * A dialog for the user to choose between gallery and camera when picking an image for the request.
     */
    private ImagePickerActionDialog imagePickerActionDialog;
    /**
     * Request image view.
     */
    private ImageView requestIV;
    /**
     * Current subcategory id.
     */
    private String currSubcategoryId;
    /**
     * Current category id.
     */
    private String currCategoryId;
    /**
     * The current subcategory list (changes when picking new category).
     */
    private ArrayList<Subcategory> currSubcategoryList;
    /**
     * Progress dialog.
     */
    private ProgressDialog progressDialog;
    /**
     * A button for viewing the current chosen location of request.
     */
    private Button viewLocationBtn;


    /**
     * Calls init().
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_request_activity_layout);
        init();
    }

    /**
     * Initializes all variables and widgets including lists, spinners, adapters, widgets and variables.
     * Calls initButtonListeners() and setCategoryOnItemClickListener().
     */
    private void init()
    {
        imagePickerActionDialog = new ImagePickerActionDialog(CreateRequestActivity.this);
        linkET = findViewById(R.id.cral_request_link_edit_text);
        linkET.setText("");
        initButtonListeners();
        requestDescriptionET = findViewById(R.id.request_description_edit_text);
        requestTitleET = findViewById(R.id.cral_request_title_edit_text);
        dateTV = findViewById(R.id.cral_offfer_date_text_view);
        timeTV = findViewById(R.id.cral_offer_time_text_view);
        categorySpinner = findViewById(R.id.cral_category_spinner);
        subcategorySpinner = findViewById(R.id.cral_subcategory_spinner);
        requestIV = findViewById(R.id.cral_request_image_image_view);
        categoryAdapter = new ArrayAdapter(CreateRequestActivity.this, R.layout.dropdown_item_layout, FirebaseManager.categoriesStrings);
        subcategoryAdapter = new ArrayAdapter(CreateRequestActivity.this, R.layout.dropdown_item_layout, new ArrayList<>());
        categorySpinner.setAdapter(categoryAdapter);
        subcategorySpinner.setAdapter(subcategoryAdapter);
        setCategoryOnItemClickListener();
        calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        latLng = null;
        currSubcategoryId = "";
        currCategoryId = "";
        currSubcategoryList = new ArrayList<>();
        progressDialog = new ProgressDialog(CreateRequestActivity.this);

    }

    /**
     *
     * @param index
     * Updates the subcategory list and spinner by given index - new category.
     */
    private void setSubcategorySpinner(int index)
    {
            subcategoryAdapter.clear();
            subcategoryAdapter.addAll(FirebaseManager.subcategoriesStrings.get(index));
            subcategoryAdapter.notifyDataSetChanged();
            currSubcategoryList = FirebaseManager.subcategories.get(index);
            setSubcategoryByTitle(subcategoryAdapter.getItem(0));
            setSubcategoryOnItemClickListener();
    }

    /**
     * Calls setSubcategoryByTitle(subcategoryAdapter.getItem(i)).
     */
    private void setSubcategoryOnItemClickListener()
    {
        subcategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                setSubcategoryByTitle(subcategoryAdapter.getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });
    }

    /**
     *
     * @param subcategoryTitle
     * Sets currSubcategoryId by the title of the current clicked subcategory.
     */
    private void setSubcategoryByTitle(String subcategoryTitle)
    {
        for(Subcategory subcategory : currSubcategoryList)
        {
            if(subcategory.getTitle().equals(subcategoryTitle))
            {
                currSubcategoryId = subcategory.getSubcategoryId();
                return;
            }
        }
    }

    /**
     * Calls setCategoryByTitle(categoryAdapter.getItem(i)) and setSubcategorySpinner(i).
     */
    private void setCategoryOnItemClickListener()
    {
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                setCategoryByTitle(categoryAdapter.getItem(i));
                setSubcategorySpinner(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });
    }

    /**
     *
     * @param categoryTitle
     * Sets currCategoryId by the title of the current clicked category.
     */
    private void setCategoryByTitle(String categoryTitle)
    {
        for(Category category : FirebaseManager.categories)
        {
            if(category.getTitle().equals(categoryTitle))
            {
                currCategoryId = category.getCategoryId();
                return;
            }
        }
    }

    /**
     * Initializes all buttons and their listeners.
     */
    private void initButtonListeners()
    {
        pickTimeBtn = findViewById(R.id.cral_pick_offer_time_button);
        pickDateBtn = findViewById(R.id.cral_pick_offer_date_button);
        pickImageBtn = findViewById(R.id.cral_pick_request_image_button);
        pickLocationBtn = findViewById(R.id.cral_pick_location_button);
        cancelBtn = findViewById(R.id.cancel_button);
        submitBtn = findViewById(R.id.submit_button);
        pickLocationBtn = findViewById(R.id.cral_pick_location_button);
        locationCB = findViewById(R.id.cral_request_location_check_box);
        linkCB = findViewById(R.id.link_check_box);
        viewLocationBtn = findViewById(R.id.view_request_location_button);

        cancelBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        pickLocationBtn.setOnClickListener(this);
        pickImageBtn.setOnClickListener(this);
        pickDateBtn.setOnClickListener(this);
        pickTimeBtn.setOnClickListener(this);
        locationCB.setOnClickListener(this);
        linkCB.setOnClickListener(this);
        viewLocationBtn.setOnClickListener(this);
    }

    /**
     *
     * @return whether the filled request's fields are valid.
     */
    private boolean areFieldsValid()
    {
        if(requestTitleET.getText().toString().isEmpty())
        {
            requestTitleET.setError("Invalid title");
            return false;
        }
        else if(requestDescriptionET.getText().toString().isEmpty())
        {
            requestDescriptionET.setError("Invalid description");
            return false;
        }
        else if(dateTV.getText().toString().isEmpty())
        {
            showTimeErrorDialog();
            return false;
        }
        else if(timeTV.getText().toString().isEmpty())
        {
            showTimeErrorDialog();
            return false;
        }
        else if(calendar.getTimeInMillis() < System.currentTimeMillis())
        {
            showTimeErrorDialog();
            return false;
        }
        else if(categorySpinner.getSelectedItem() == null)
        {
            return false;
        }
        else if(subcategorySpinner.getSelectedItem() == null)
        {
            return false;
        }
        else if(locationCB.isChecked() && latLng == null)
        {
            showLocationErrorDialog();
            return false;
        }
        else if(linkCB.isChecked() && linkET.getText().toString().isEmpty())
        {
            linkET.setError("No link attached");
            return false;
        }
        else if(linkCB.isChecked() && !URLUtil.isValidUrl(linkET.getText().toString()))
        {
            linkET.setError("Invalid link");
            return false;
        }
        return true;
    }


    /**
     *
     * @param view
     * The function handles all buttons in case they get clicked.
     * Calls finish(), uploadRequest(), handleLocationPicker(), handleImagePicker(), handleDatePicker(), handleTimePicker().
     */
    @Override
    public void onClick(View view)
    {
        if(view.getId() == cancelBtn.getId())
        {
            finish();
        }
        else if(view.getId() == submitBtn.getId())
        {
            if(areFieldsValid())
            {
                uploadRequest();
            }
        }
        else if(view.getId() == pickLocationBtn.getId())
        {
            if(locationCB.isChecked() && MapsManager.isServicesOK(CreateRequestActivity.this, this))
            {
                handleLocationPicker();
            }
        }
        else if(view.getId() == locationCB.getId())
        {
            if(locationCB.isChecked())
            {
                linkCB.setChecked(false);
                linkET.setText("");
                linkET.setEnabled(false);
                linkET.setError(null);
            }
            else
            {
                latLng = null;
            }
        }
        else if(view.getId() == linkCB.getId())
        {
            if(linkCB.isChecked())
            {
                locationCB.setChecked(false);
                latLng = null;
                linkET.setEnabled(true);
            }
            else
            {
                linkET.setText("");
                linkET.setEnabled(false);
                linkET.setError(null);
            }
        }
        else if(view.getId() == pickImageBtn.getId())
        {
            handleImagePicker();
        }
        else if(view.getId() == pickDateBtn.getId())
        {
            handleDatePicker();
        }
        else if(view.getId() == pickTimeBtn.getId())
        {
            handleTimePicker();
        }
        else if(view.getId() == viewLocationBtn.getId())
        {
            if(latLng != null && locationCB.isChecked())
            {
                /**
                 * Starts new activity with the chosen request location details.
                 */
                Intent intent = new Intent(CreateRequestActivity.this, MapActivity.class);
                intent.putExtra(ConstantsManager.LAT_LNG, latLng);
                startActivity(intent);
            }
        }
    }


    /**
     * Creates the request - online / offline.
     * Calls attemptRequestUpload(request).
     */
    private void uploadRequest()
    {
        Request request = null;
        if(locationCB.isChecked())
        {
                request = new OfflineRequest(requestTitleET.getText().toString(), UserManager.getCurrentUser().getUserId(),
                        currSubcategoryId, currCategoryId,
                        requestDescriptionET.getText().toString(), calendar.getTimeInMillis(), "", "", ConstantsManager.statesCodes[0], latLng);
        }
        else
            {
                request = new OnlineRequest(requestTitleET.getText().toString(), UserManager.getCurrentUser().getUserId(),
                        currSubcategoryId, currCategoryId,
                        requestDescriptionET.getText().toString(), calendar.getTimeInMillis(), "", "", ConstantsManager.statesCodes[0], linkET.getText().toString());
        }
        progressDialog.show();
        attemptRequestUpload(request);
    }

    /**
     *
     * @param request
     * Tries to upload the request object to firebase database.
     * Also Calls planNotification(...), uploadUrlImage(...) or uploadBitmapImage(...).
     */
    private void attemptRequestUpload(Request request)
    {
        if(request == null)
        {
            progressDialog.dismiss();
            return;
        }

        FirebaseManager.getDBReference().collection(ConstantsManager.REQUESTS_COLLECTION)
                .add(request.getRequestInfo()).addOnSuccessListener(new OnSuccessListener<DocumentReference>()
        {
            @Override
            public void onSuccess(DocumentReference documentReference)
            {
                request.setRequestId(documentReference.getId());
                if(imageUri == null && imageBitmap == null)
                {
                    documentReference.update(request.getRequestInfo());
                    progressDialog.dismiss();
                    updateUI();
                }
                else
                {
                    if(imageUri != null)
                    {
                        uploadUrlImage(imageUri, ConstantsManager.REQUESTS_IMAGES_PREFIX, documentReference, request);
                    }
                    else if(imageBitmap != null)
                    {
                        uploadBitmapImage(imageBitmap, ConstantsManager.REQUESTS_IMAGES_PREFIX, documentReference, request);
                    }
                }
                planNotification(documentReference);
            }
        });
    }

    /**
     * Uploads a bitmap image (camera).
     *
     * @param bitmap            the bitmap
     * @param prefix            the prefix of the request images' folder path
     * @param documentReference the document reference
     * @param request           the request
     *
     * Calls getDownloadUrl(...)
     */
    public void uploadBitmapImage(Bitmap bitmap, String prefix, DocumentReference documentReference, Request request)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference imageRef = FirebaseManager.getFSReference().child(prefix).child(request.getRequestId());

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                getDownloadUrl(imageRef, request, documentReference);
            }
        });

    }

    /**
     * Uploads an url image (gallery).
     *
     * @param imageUri          the image uri
     * @param prefix            the prefix of the request images' folder path
     * @param documentReference the document reference
     * @param request           the request
     *
     * Calls getDownloadUrl(...)
     */
    public void uploadUrlImage(Uri imageUri, String prefix, DocumentReference documentReference, Request request)
    {
        StorageReference imageRef = FirebaseManager.getFSReference().child(prefix).child(request.getRequestId());
        imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                getDownloadUrl(imageRef, request, documentReference);
            }
        });
    }

    /**
     *
     * @param storageReference
     * @param request
     * @param documentReference
     * Gets the download url form fire storage after uploading the image.
     * Calls setUserImageUrl(...)
     */
    private void getDownloadUrl(StorageReference storageReference, Request request, DocumentReference documentReference)
    {
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri)
            {
                setUserImageUrl(uri, request, documentReference);
            }
        });
    }

    /**
     *
     * @param uri
     * @param request
     * @param documentReference
     *
     * Updates request image to uploaded one, updates in database.
     * Calls updateUI().
     */
    private void setUserImageUrl(Uri uri,Request request, DocumentReference documentReference)
    {
        request.setRequestImage(uri.toString());
        documentReference.update(request.getRequestInfo());
        progressDialog.dismiss();
        updateUI();
    }


    /**
     * The function is creating an alarm manager (Service) that notifies and sends a broadcast to the "SendNotificationReceiver".
     * @param documentReference
     */
    private void planNotification(DocumentReference documentReference)
    {
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                if(documentSnapshot != null && documentSnapshot.exists())
                {
                    Intent intent = new Intent(CreateRequestActivity.this, SendNotificationReceiver.class);

                    intent.putExtra(ConstantsManager.REQUEST_REQUEST_ID_FIELD, documentReference.getId());
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(CreateRequestActivity.this, ConstantsManager.NOTIFICATION_PENDING_INTENT_CODE, intent, 0);

                    AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarm.set(AlarmManager.RTC_WAKEUP, documentSnapshot.getLong(ConstantsManager.REQUEST_MILLISECONDS_FIELD), pendingIntent);
                }
            }
        });
    }


    /**
     * Closes activity.
     */
    private void updateUI()
    {
        finish();
    }

    /**
     * Opens a time picker dialog for the request's time field (part of the milliseconds) based on (24 Hour view - NOT AM/PM).
     */
    private void handleTimePicker()
    {
        Calendar c = Calendar.getInstance();
        int HOUR = c.get(Calendar.HOUR_OF_DAY);
        int MINUTE = c.get(Calendar.MINUTE);


        TimePickerDialog timePickerDialog = new TimePickerDialog(CreateRequestActivity.this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute)
            {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                timeTV.setText(DateAndTimeManager.getTimeString(calendar.getTimeInMillis()));
            }
        }, HOUR, MINUTE, true);
        timePickerDialog.show();
    }

    /**
     * Opens a date picker dialog for the request's date field (part of the milliseconds).
     */
    private void handleDatePicker()
    {
        Calendar c = Calendar.getInstance();

        int YEAR = c.get(Calendar.YEAR);
        int MONTH = c.get(Calendar.MONTH);
        int DAY = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateRequestActivity.this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                dateTV.setText(DateAndTimeManager.getDateString(calendar.getTimeInMillis()));
            }
        }, YEAR, MONTH, DAY);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    /**
     * Pops a dialog notifying the user that the time he chose for the request isn't valid (not future time).
     */
    private void showTimeErrorDialog()
    {
        DialogInterface.OnClickListener dialogInterface = new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        };
        DialogsManager.showNeutralAlertDialog(CreateRequestActivity.this,
                "Invalid period of time","Your date field or time field are invalid",
                dialogInterface);
    }

    /**
     * Pops a dialog notifying the user that the location he chose for the request isn't valid.
     */
    private void showLocationErrorDialog()
    {
        DialogInterface.OnClickListener dialogInterface = new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        };
        DialogsManager.showNeutralAlertDialog(CreateRequestActivity.this,
                "Invalid location","Location is invalid",
                dialogInterface);
    }


    /**
     * Opens a diloag, enabling the user to choose whether to pick the picture from the camera or gallery.
     */
    private void handleImagePicker()
    {
        imagePickerActionDialog.showDialog();
    }


    /**
     * Starts a new activity (Google place picker is deprecated and can't be use therefore I used other library) - 'com.adevinta.android:leku:9.1.4'
     */
    private void handleLocationPicker()
    {
        Intent intent = new LocationPickerActivity.Builder()
                .withGeolocApiKey(getString(R.string.api_key))
                .withGooglePlacesApiKey(getString(R.string.api_key))
                .build(CreateRequestActivity.this);

        startActivityForResult(intent, ConstantsManager.PICK_LOCATION_REQUEST_CODE);
    }


    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     *
     * Handles the picked location from the location picker activity.
     * Handles the request image from both camera and gallery.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ConstantsManager.PICK_LOCATION_REQUEST_CODE)
        {
            if(resultCode == this.RESULT_OK)
            {
                double latitude = data.getDoubleExtra(LocationPickerActivityKt.LATITUDE, 0);
                double longitude = data.getDoubleExtra(LocationPickerActivityKt.LONGITUDE, 0);
                latLng = new LatLng(latitude, longitude);
            }
        }

        else if(requestCode == ConstantsManager.CAMERA_REQUEST_CODE)
        {
            if(resultCode == this.RESULT_OK)
            {
                imageBitmap = (Bitmap) data.getExtras().get("data");
                ImageManager.setImageViewBitmap(CreateRequestActivity.this, imageBitmap, requestIV, R.drawable.gallery_icon);
                imageUri = null;
                imagePickerActionDialog.dismiss();
            }
        }

        else if(requestCode == ConstantsManager.GALLERY_REQUEST_CODE)
        {
            if(resultCode == this.RESULT_OK)
            {
                imageUri = data.getData();

                ImageManager.setImageViewURI(CreateRequestActivity.this, imageUri, requestIV, R.drawable.gallery_icon);
                imageBitmap = null;
                imagePickerActionDialog.dismiss();
            }
        }
    }
}
