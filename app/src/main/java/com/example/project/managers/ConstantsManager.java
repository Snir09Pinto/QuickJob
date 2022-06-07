package com.example.project.managers;

import android.Manifest;

import androidx.fragment.app.Fragment;

import com.example.project.R;
import com.example.project.fragments.SearchCategoriesFragment;
import com.example.project.fragments.ChatsListFragment;
import com.example.project.fragments.HomeFragment;
import com.example.project.fragments.MapFragment;
import com.example.project.fragments.MultiOffersFragment;
import com.example.project.fragments.multiRequests.MultiRequestsFragment;
import com.example.project.fragments.ProfileFragment;


public class ConstantsManager
{
    //**************************************FIREBASE******************************************

    //CLASS - USER
    public static final String USER = "user";
    public static final String USER_USERNAME_FIELD = "username";
    public static final String USER_PASSWORD_FIELD = "password";
    public static final String USER_USER_ID_FIELD = "userId";
    public static final String USER_EMAIL_FIELD = "email";
    public static final String USER_PHONE_FIELD = "phone";
    public static final String USER_USER_IMAGE_FIELD = "userImage";
    public static final String USER_DESCRIPTION_FIELD = "description";
    public static final String USER_IS_ONLINE = "isOnline";

    //CLASS - SUBCATEGORY
    public static final String SUBCATEGORY = "subcategory";
    public static final String SUBCATEGORY_TITLE_FIELD = "title";
    public static final String SUBCATEGORY_SUBCATEGORY_ID_FIELD = "subcategoryId";
    public static final String SUBCATEGORY_SUBCATEGORY_IMAGE_FIELD = "subcategoryImage";
    public static final String SUBCATEGORY_CATEGORY_ID_FIELD = "categoryId";


    //CLASS - CATEGORY
    public static final String CATEGORY = "category";
    public static final String CATEGORY_TITLE_FIELD = "title";
    public static final String CATEGORY_CATEGORY_ID_FIELD = "categoryId";
    public static final String CATEGORY_CATEGORY_IMAGE_FIELD = "categoryImage";

    //CLASS - MESSAGE
    public static final String MESSAGE = "message";
    public static final String MESSAGE_MILLISECONDS_FIELD = "milliseconds";
    public static final String MESSAGE_MESSAGE_FIELD = "message";
    public static final String MESSAGE_SENDER_ID_FIELD = "senderId";
    public static final String MESSAGE_RECEIVER_ID_FIELD = "receiverId";
    public static final String MESSAGE_READ_FIELD = "read";


    //CLASS - OFFER
    public static final String OFFER = "offer";
    public static final String OFFER_REFERENCE = "offerReference";
    public static final String OFFER_DESCRIPTION_FIELD = "description";
    public static final String OFFER_STATE_FIELD = "state";
    public static final String OFFER_MILLISECONDS_FIELD = "milliseconds";
    public static final String OFFER_USER_OF_OFFER_INPUT_FIELD = "userOfOfferInput";
    public static final String OFFER_USER_OF_REQUEST_INPUT_FIELD = "userOfRequestInput";
    public static final String OFFER_USER_ID_FIELD = "userId";
    public static final String OFFER_PRICE_FIELD = "price";
    public static final String OFFER_OFFER_ID_FIELD = "offerId";
    public static final String OFFER_REQUEST_ID_FIELD = "requestId";

    //CLASS - REQUEST
    public static final String REQUEST = "request";
    public static final String REQUEST_TITLE_FIELD = "title";
    public static final String REQUEST_USER_ID_FIELD = "userId";
    public static final String REQUEST_SUBCATEGORY_ID_FIELD = "subcategoryId";
    public static final String REQUEST_CATEGORY_ID_FIELD = "categoryId";
    public static final String REQUEST_DESCRIPTION_FIELD = "description";
    public static final String REQUEST_MILLISECONDS_FIELD = "milliseconds";
    public static final String REQUEST_REQUEST_ID_FIELD = "requestId";
    public static final String REQUEST_REQUEST_IMAGE_FIELD = "requestImage";
    public static final String REQUEST_STATE_FIELD = "state";
    public static final String REQUEST_TYPE_FIELD = "type";

    //REQUEST CONSTANTS
    public static final int ONLINE_REQUEST_TYPE = 0;
    public static final int OFFLINE_REQUEST_TYPE = 1;

    public static final String ONLINE_REQUEST_TYPE_STRING = "Online";
    public static final String OFFLINE_REQUEST_TYPE_STRING = "Offline";

    public static final String ONLINE_REQUEST_COLOR = "#1dbf73";

    public static final String OFFLINE_REQUEST_COLOR = "#215523";

    //SUBCLASS - OFFLINE REQUEST
    public static final String OFFLINE_REQUEST_LATITUDE = "latitude";
    public static final String OFFLINE_REQUEST_LONGITUDE = "longitude";

    //SUBCLASS - ONLINE REQUEST
    public static final String REQUEST_LINK_FIELD = "link";

    //CLASS - REVIEW
    public static final String REVIEW_REVIEW_ID_FIELD = "reviewId";
    public static final String REVIEW_DESCRIPTION_FIELD = "description";
    public static final String REVIEW_RATING_FIELD = "rating";
    public static final String REVIEW_MILLISECONDS_FIELD = "milliseconds";
    public static final String REVIEW_OFFER_ID_FIELD = "offerId";

    //COLLECTIONS
    public static final String USERS_COLLECTION = "users";
    public static final String SUBCATEGORIES_COLLECTION = "subcategories";
    public static final String CATEGORIES_COLLECTION = "categories";
    public static final String REQUESTS_COLLECTION = "requests";
    public static final String MESSAGES_COLLECTION = "messages";
    public static final String OFFERS_COLLECTION = "offers";
    public static final String REVIEWS_COLLECTION = "reviews";
    public static final String CHATS_COLLECTION = "chats";

    //STORAGE PREFIXES
    public static final String REQUESTS_IMAGES_PREFIX = "requestsImages";
    public static final String USERS_IMAGES_PREFIX = "usersImages";
    public static final String CATEGORIES_IMAGES_PREFIX = "categoriesImages";
    public static final String SUBCATEGORIES_IMAGES_PREFIX = "subcategoriesImages";

    //**************************************FIREBASE******************************************

    //REQUEST CODES
    public static final int SEARCH_REQUEST_CODE = 3332;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    public static final int ERROR_DIALOG_REQUEST_CODE = 326;
    public static final int CREATE_OFFER_REQUEST_CODE = 42142;
    public static final int GALLERY_REQUEST_CODE = 0;
    public static final int CAMERA_REQUEST_CODE = 1;
    public static final int PICK_LOCATION_REQUEST_CODE = 123;
    public static final int NOTIFICATION_PENDING_INTENT_CODE = 556;
    public static final int POST_A_REVIEW_REQUEST_CODE = 1111;


    //ARRAYS
    public static String [] tabOptions;
    public static int [] statesCodes;
    public static int [] statesColors1;
    public static int [] statesColors2;
    public static String [] permissions;
    public static Fragment[] menuFragments;
    public static Fragment[] requestTabFragments;
    public static Fragment[] offerTabFragments;
    public static int[] filters;
    public static void initConstArrays()
    {
        filters = new int[]{0,1,2,3,4};
        tabOptions = new String[]{"Pending", "Processing", "Delivered"};
        statesCodes = new int[]{-1, 0, 1}; // PENDING: -1, PROCESSING: 0, DELIVERED: 1
        statesColors1 = new int[]{R.color.star_orange_color, R.color.black, R.color.light_green};
        statesColors2 = new int[]{R.color.dark_green, R.color.black, R.color.light_green};
        permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        menuFragments = new Fragment[]{new HomeFragment(), new SearchCategoriesFragment(), new ChatsListFragment(), new MapFragment(), new ProfileFragment()};

        requestTabFragments = new Fragment[]{MultiRequestsFragment.newInstance(ConstantsManager.statesCodes[0]), MultiRequestsFragment.newInstance(ConstantsManager.statesCodes[1]), MultiRequestsFragment.newInstance(ConstantsManager.statesCodes[2])};
        offerTabFragments = new Fragment[]{MultiOffersFragment.newInstance(ConstantsManager.statesCodes[0]), MultiOffersFragment.newInstance(ConstantsManager.statesCodes[1]), MultiOffersFragment.newInstance(ConstantsManager.statesCodes[2])};
    }

    //FORMATS
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String TIME_FORMAT = "HH:mm";
    public static final String DATE_AND_TIME_FORMAT = "dd/MM/yyyy HH:mm";
    public static final String DETAILED_TIME_FORMAT = "Time Left: %dd %dh %dm %ds";

    //GPS
    public static final float DEFAULT_ZOOM = 15f;
    public static final String LAT_LNG = "latlng";

    //USER MANAGER
    public static final String spFilePath = "userInfo";


    //DUMMY VALUES
    public static final String DUMMY_VALUE = "DUMMY";

    //VIEW PAGER
    public static final int REQUESTS_ADAPTER_CODE = 0;
    public static final int OFFERS_ADAPTER_CODE = 1;

    //NOTIFICATION
    public static final int PENDING_INTENT_REQUEST_CODE = 5943;

    //ADAPTERS
    public static final int MSG_TYPE_RECEIVED = 500;
    public static final int MSG_TYPE_SENT = 600;
    public static final int VERTICAL_VIEW = 0, HORIZONTAL_VIEW = 1;

    //BROADCASTS
    public static final String NOTIFICATION_CHANNEL ="notificationChannel";



}
