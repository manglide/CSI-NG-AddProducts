package ng.com.addproducts;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//import im.delight.android.location.SimpleLocation;

public class Dashboard extends AppCompatActivity   {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private static int REQ_CODE_SPEECH_INPUT = 100;
    private static final String TITLE = "title";
    private static final String REVIEWED_TITLE = "product_id";
    private Button btnSelect;
    public static ImageView ivImage1;
    public static ImageView ivImage2;
    //public static ImageView ivImage3;
    //public static ImageView ivImage4;
    //public static ImageView ivImage5;
    public static Bitmap img1 = null;
    public static Bitmap img2 = null;
    //public static Bitmap img3 = null;
    //public static Bitmap img4 = null;
    //public static Bitmap img5 = null;
    public static String path_img1 = null;
    public static String path_img2 = null;
    //public static String path_img3 = null;
    //public static String path_img4 = null;
    //public static String path_img5 = null;
    private View cView;
    private String userChoosenTask;
    public static View rootView;
    public static DBHandler jdbc = null;
    private static SimpleCursorAdapter dataAdapter;
    private static SimpleCursorAdapter review_dataAdapter;

    private static Activity dshb = null;
    private static View addProductsProgressView;
    private static View reviewProductsProgressView;
    private static View addProductFormView;
    private static EditText full_about_product = null;
    private static EditText full_about_manufacturer = null;
    private static EditText full_about_manufacturer_address = null;
    private static EditText full_about_ingredients = null;
    /* Position */
    private static final int MINIMUM_TIME = 10000;  // 10s
    private static final int MINIMUM_DISTANCE = 50; // 50m
    //private LocationManager mLocationManager = null;
    //private LocationListener mLocationListener = null;
    private String mProviderName;
    //private static SimpleLocation location = null;
    //private static Double Latitude = null, Longitude = null;
    /* GPS Constant Permission */
    //private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    //private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    private static String user_logged_in = null;

    private static String unique_identifier_of_product = null;

    private static Toolbar toolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        jdbc = new DBHandler(this);
        dshb = this;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Switch sync_button = (Switch) findViewById(R.id.syncbutton);
        int state = jdbc.getSync();
        if(state == 0) {
            sync_button.setChecked(false);
            final TextView stat = (TextView)toolbar.findViewById(R.id.syncstatus);
            stat.setText("");
        } else if(state == 1) {
            sync_button.setChecked(true);
            if(jdbc.getAllProductsToSync()>0) {
                final TextView stat = (TextView)toolbar.findViewById(R.id.syncstatus);
                //stat.setText("Synchronizing in Progress...");
            } else {
                final TextView stat = (TextView)toolbar.findViewById(R.id.syncstatus);
                //stat.setText("Synchronizing Complete.");
            }
        }
        sync_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(jdbc.setSync(1)>0){
                        syncHandler(jdbc,toolbar);
                    } else {
                        Toast.makeText(getApplicationContext(),"Sync couldn't be started",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(jdbc.setSync(0)>0){
                        Toast.makeText(getApplicationContext(),"Sync Stopped",Toast.LENGTH_LONG).show();
                        final TextView stat = (TextView)toolbar.findViewById(R.id.syncstatus);
                        stat.setText("");
                    } else {
                        Toast.makeText(getApplicationContext(),"Sync couldn't be stopped",Toast.LENGTH_LONG).show();
                    }
                }
            }

        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        Intent intent = getIntent();
        user_logged_in = intent.getStringExtra("email");

        /********* Set GPS ************/
        // construct a new instance of SimpleLocation
        //location = new SimpleLocation(this);
        // if we can't access the location yet
        //if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            //SimpleLocation.openSettings(this);
        //}
        /********* Finished ************/
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);



        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(1).select();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "", Snackbar.LENGTH_LONG)
                        .setAction("Log Out", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DBHandler mv = new DBHandler(Dashboard.this);
                                String s_email = getIntent().getStringExtra("email");
                                String s_passw = getIntent().getStringExtra("password");
                                String s_time = getIntent().getStringExtra("time");
                                User g_ = new User(s_email,s_passw,s_time);
                                //mv.deleteUser(g_);
                                //mv.dropTABLE("product_review");
                                finish();
                            }
                        }).show();

            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /********************************************** Handle Camera ****************************************************************************/
    /********************************************** Handle Camera ****************************************************************************/
    /********************************************** Handle Camera ****************************************************************************/
    /********************************************** Handle Camera ****************************************************************************/

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                    Toast.makeText(ApplicationName.getAppContext(),"You must grant necessary permission to proceed",Toast.LENGTH_LONG).show();
                }
                break;
            case Utility.MY_PERMISSIONS_REQUEST_READ_PHONE_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ApplicationName.getAppContext(),"Permissions Granted",Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    /**
     * Showing google speech input dialog
     * */
    private static void promptSpeechInput(Activity activity) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");
        try {
            activity.startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(ApplicationName.getAppContext(), "Speech Not Supported", Toast.LENGTH_SHORT).show();
        }
    }

    private void onCaptureImageResult(Intent data) {
        String path = null;
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
            //path = destination.getCanonicalPath();
            if (Build.VERSION.SDK_INT < 11)
                path = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());

                // SDK >= 11 && SDK < 19
            else if (Build.VERSION.SDK_INT < 19)
                path = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());

                // SDK > 19 (Android 4.4)
            else
                path = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assignViews(cView.getParent(),cView.getId(),thumbnail, path);
    }

    private void onSelectFromGalleryResult(Intent data) {

        String path = null;
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(this.getApplicationContext().getContentResolver(), data.getData());

                //path = data.getData().getPath();
                if (Build.VERSION.SDK_INT < 11)
                    path = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());

                    // SDK >= 11 && SDK < 19
                else if (Build.VERSION.SDK_INT < 19)
                    path = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());

                    // SDK > 19 (Android 4.4)
                else
                    path = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        assignViews(cView.getParent(), cView.getId(),bm, path);

    }

    public static void assignViews(ViewParent parent, int id, Bitmap bm, String path) {
        int ID = id;
        if(ID == R.id.take_image_from_camera_one) {
            //(RelativeLayout) ((ViewGroup) parent.getParent()).getParent();
            ivImage1 = (ImageView)((ViewGroup) parent.getParent()).findViewById(R.id.product_image_one);
            img1 = bm;
            ivImage1.setImageBitmap(bm);
            path_img1 = path;
            //TextView tv = (TextView)rootView.findViewById(R.id.image_one_path);
            //tv.setText(path);
        } else if(ID == R.id.take_image_from_camera_two) {
            ivImage2 = (ImageView)((ViewGroup) parent.getParent()).findViewById(R.id.product_image_two);
            img2 = bm;
            ivImage2.setImageBitmap(bm);
            path_img2 = path;
        } /*else if(ID == R.id.take_image_from_camera_three) {
            ivImage3 = (ImageView)rootView.findViewById(R.id.product_image_three);
            img3 = bm;
            ivImage3.setImageBitmap(bm);
            path_img3 = path;
        } else if(ID == R.id.take_image_from_camera_four) {
            ivImage4 = (ImageView)rootView.findViewById(R.id.product_image_four);
            img4 = bm;
            ivImage4.setImageBitmap(bm);
            path_img4 = path;
        } else if(ID == R.id.take_image_from_camera_five) {
            ivImage5 = (ImageView)rootView.findViewById(R.id.product_image_five);
            img5 = bm;
            ivImage5.setImageBitmap(bm);
            path_img5 = path;
        }*/
    }

    public void selectImage(View view) {
        cView = view;
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(Dashboard.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /********************************************** Handle Camera ****************************************************************************/
    /********************************************** Handle Camera ****************************************************************************/
    /********************************************** Handle Camera ****************************************************************************//********************************************** Handle Camera ****************************************************************************/
    /********************************************** Handle Camera ****************************************************************************/
    /********************************************** Handle Camera ****************************************************************************/


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements AdapterView.OnItemSelectedListener {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
            if(pos == 0 && parent.getSelectedItem().toString().equalsIgnoreCase("Select Product Category")) {
                // Let us De-Activate the Submit Button
                Toast.makeText(parent.getContext(), "Please Make a Valid Selection", Toast.LENGTH_LONG).show();
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if(getArguments().getInt(ARG_SECTION_NUMBER) == 4) {
                rootView = inflater.inflate(R.layout.add_product, container, false);
                addProductsProgressView = (View)rootView.findViewById(R.id.dashboard_progress);

                final Spinner spinner = (Spinner) rootView.findViewById(R.id.product_categories);
                final TextView rx1 = (TextView)rootView.findViewById(R.id.competitor_one);
                final TextView rx2 = (TextView)rootView.findViewById(R.id.competitor_two);

                // All Products Drop Down List
                final Spinner all_products = (Spinner) rootView.findViewById(R.id.product_name);
                List<String> all_pr = jdbc.getAllGlobalProducts();
                //Toast.makeText(ApplicationName.getAppContext(),String.valueOf(all_pr.size()),Toast.LENGTH_LONG).show();
                ArrayAdapter<String> adapterAll = new ArrayAdapter<String>(rootView.getContext(),
                        android.R.layout.simple_spinner_item, all_pr);
                adapterAll.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                all_products.setAdapter(adapterAll);
                all_products.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedProductText = (String) parent.getItemAtPosition(position);
                        if(selectedProductText.equalsIgnoreCase(" ... Select Product ... ")) {
                            ;
                        } else {
                            // Notify the selected item text and Get its category from the DB
                            List<String> cscd_all = jdbc.getProductCompetitions(selectedProductText);
                            String val = cscd_all.get(0);
                            String s[] = val.split("_");
                            String all_cat = s[s.length-s.length];
                            String comp_1 = s[s.length-(s.length-1)];
                            String comp_2 = s[s.length-(s.length-2)];

                            List<String> r = new ArrayList<String>();
                            r.clear();
                            r.add(all_cat);

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(rootView.getContext(),
                                    android.R.layout.simple_spinner_item, r);
                            // Specify the layout to use when the list of choices appears
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            // Apply the adapter to the spinner
                            spinner.setAdapter(adapter);
                            // Set onItemListener
                            //spinner.setOnItemSelectedListener(this);
                            adapter.notifyDataSetChanged();

                            rx1.setText(comp_1);
                            rx1.setFocusable(false);

                            rx2.setText(comp_2);
                            rx2.setFocusable(false);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                // All Products Drop Down List End
                // Set Images
                ivImage1 = (ImageView)rootView.findViewById(R.id.product_image_one);
                ivImage2 = (ImageView)rootView.findViewById(R.id.product_image_two);
                //ivImage3 = (ImageView)rootView.findViewById(R.id.product_image_three);
                //ivImage4 = (ImageView)rootView.findViewById(R.id.product_image_four);
                //ivImage5 = (ImageView)rootView.findViewById(R.id.product_image_five);
                // Submit Button Handler
                Button btnx = (Button)rootView.findViewById(R.id.submit_new_product);
                // Get Add Product Scroll View Container
                addProductFormView = (ScrollView)rootView.findViewById(R.id.add_product_form_scv);
                // Progress Bar

                final TextView product_manufacturer_V = (TextView) rootView.findViewById(R.id.manufacturer);
                final TextView price_V = (TextView)rootView.findViewById(R.id.price);
                final TextView competitor_one_man_V = (TextView)rootView.findViewById(R.id.competitor_one_man);
                final TextView competitor_two_man_V = (TextView)rootView.findViewById(R.id.competitor_two_man);


                //addProductsProgressView = rootView.findViewById(R.id.dashboard_progress);
                btnx.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            String product_title = all_products.getSelectedItem().toString();
                            if(product_title.equalsIgnoreCase(" ... Select Product ... ")) {
                                Toast.makeText(ApplicationName.getAppContext(),"You're yet to select a Product",Toast.LENGTH_LONG).show();
                            } else {
                                String product_category = spinner.getSelectedItem().toString();
                                String product_manufacturer = product_manufacturer_V.getText().toString();
                                String price = price_V.getText().toString();
                                Double main_price = Double.parseDouble(price);
                                String competitor_one = rx1.getText().toString();
                                String competitor_one_man = competitor_one_man_V.getText().toString();
                                String competitor_two = rx2.getText().toString();
                                String competitor_two_man = competitor_two_man_V.getText().toString();
                                if(product_title.equalsIgnoreCase("")
                                        || product_category.equalsIgnoreCase("")
                                        || product_manufacturer.equalsIgnoreCase("")
                                        || img1 == null || img2 == null || price.equalsIgnoreCase("") //|| img3 == null || img4 == null || img5 == null
                                        || competitor_one.equalsIgnoreCase("") || competitor_one_man.equalsIgnoreCase("")
                                        ) {
                                    Toast.makeText(rootView.getContext(),"Missing Fields",Toast.LENGTH_LONG).show();
                                } else {
                                    boolean result=Utility2.checkPermission(getContext());
                                    if(result) {
                                        // Proceed To Insert - But Show Progress View First
                                        showProgressProducts(true,addProductsProgressView);

                                        TabLayout tab_host = (TabLayout) getActivity().findViewById(R.id.tabs);

                                        AddProducts addProductsTask = new AddProducts(addProductsProgressView, tab_host);

                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                                        String date = sdf.format(new Date());
                                        TelephonyManager telephonyManager = (TelephonyManager)ApplicationName.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
                                        String deviceID = telephonyManager.getDeviceId();
                                        String uuid = md5(deviceID+"~"+date);

                                        //path_img3,path_img4, path_img5,
                                        addProductsTask.execute(product_title,product_category,
                                                product_manufacturer, path_img1, path_img2, competitor_one,competitor_one_man,
                                                competitor_two, competitor_two_man,getContext(),uuid, main_price);
                                    }
                                }
                            }
                        } catch (NullPointerException ne) {
                            Toast.makeText(ApplicationName.getAppContext(),ne.getMessage().toString(),Toast.LENGTH_LONG).show();
                        }

                    }
                });
                return rootView;
            } else if(getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                // Review A Product Tab
                rootView = inflater.inflate(R.layout.add_product_extended, container, false);
                // Get Progress View
                reviewProductsProgressView = (View)rootView.findViewById(R.id.review_product_progress);
                // Get Product Name
                final Spinner spinner = (Spinner) rootView.findViewById(R.id.products);
                // Get Data
                List<String> cs = jdbc.getProductsForReview();
                //Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(rootView.getContext(),
                        android.R.layout.simple_spinner_item, cs);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);
                // Set New Adapter1
                final List<String> csc = new ArrayList<String>();

                Spinner spinner1 = (Spinner) rootView.findViewById(R.id.category);

                final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(rootView.getContext(),
                        android.R.layout.simple_spinner_item, csc);
                // Specify the layout to use when the list of choices appears
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinner1.setAdapter(adapter1);
                // Set New Adapter
                // Set onItemListener
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedItemText = (String) parent.getItemAtPosition(position);
                        // If user change the default selection
                        // First item is disable and it is used for hint
                        if(position > 0){
                            // Notify the selected item text and Get its category from the DB
                            List<String> cscd = jdbc.getProductCategoryByTitle(selectedItemText);
                            // Modified on 19th August 2017
                            //unique_identifier_of_product = jdbc.getUniqueIdentifier(selectedItemText);
                            boolean result=Utility2.checkPermission(getContext());
                            if(result) {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                                String date = sdf.format(new Date());
                                TelephonyManager telephonyManager = (TelephonyManager) ApplicationName.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
                                String deviceID = telephonyManager.getDeviceId();
                                unique_identifier_of_product = md5(deviceID + "~" + date);
                            }
                            csc.clear();
                            csc.addAll(cscd);
                            adapter1.notifyDataSetChanged();
                            //Toast.makeText(rootView.getContext(), "Category is : " + cate, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                /********* Fetch Data **************/
                /********* Fetch Data **************/
                /********* Fetch Data **************/
                // Get Selected Product


                // Get Product Category
                final Spinner product_category = (Spinner) rootView.findViewById(R.id.category);

                // Get our Text Input Box for About Product
                full_about_product = (EditText)rootView.findViewById(R.id.full_about_product);
                // Get manufacturer
                full_about_manufacturer = (EditText)rootView.findViewById(R.id.about_manufacturer);
                // Get Manufacturer Address
                full_about_manufacturer_address = (EditText)rootView.findViewById(R.id.address_of_manufacturer);
                // Get Product Ingredients
                full_about_ingredients = (EditText)rootView.findViewById(R.id.ingredients);
                // Get the Price Text Value
                final TextView price_V = (TextView)rootView.findViewById(R.id.price);
                // Set Images
                ivImage1 = (ImageView)rootView.findViewById(R.id.product_image_one);
                ivImage2 = (ImageView)rootView.findViewById(R.id.product_image_two);
                // Get Review Submit Button
                Button rvBtn = (Button) rootView.findViewById(R.id.submit_new_product_review);

                rvBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(spinner.getSelectedItem() != null && product_category.getSelectedItem() != null) {
                            String product = spinner.getSelectedItem().toString();
                            String product_category_txt = product_category.getSelectedItem().toString();

                            String aboutproduct = full_about_product.getText().toString();
                            String manufacturer = full_about_manufacturer.getText().toString();
                            String address = full_about_manufacturer_address.getText().toString();
                            String ingredients = full_about_ingredients.getText().toString();
                            String price = price_V.getText().toString();

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                            String date = sdf.format(new Date());
                            TelephonyManager telephonyManager = (TelephonyManager)ApplicationName.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
                            String deviceID = telephonyManager.getDeviceId();
                            String uuid = md5(deviceID+"~"+date);

                            if(product.equalsIgnoreCase("") || product_category_txt.equalsIgnoreCase("") || ingredients.equalsIgnoreCase("") || aboutproduct.equalsIgnoreCase("") || address.equalsIgnoreCase("") ||
                            img1 == null || img2 == null || manufacturer.equalsIgnoreCase("") || price.equalsIgnoreCase("")) {
                                Toast.makeText(ApplicationName.getAppContext(),"Incomplete Product Definition", Toast.LENGTH_LONG).show();
                            } else {

                                    // Proceed To Insert - But Show Progress View First
                                    //showProgressProducts(true,reviewProductsProgressView);

                                    TabLayout tab_host = (TabLayout) getActivity().findViewById(R.id.tabs);
                                    // path_img1, path_img2,
                                    UpdateProducts updateProductsTask = new UpdateProducts(reviewProductsProgressView, tab_host);
                                    updateProductsTask.execute(product,product_category_txt,aboutproduct,manufacturer,address,ingredients,
                                            path_img1, path_img2, price, uuid, getContext());

                            }
                        }
                    }
                });

                /********* Fetch Data **************/
                /********* Fetch Data **************/
                /********* Fetch Data **************/
                /********* Fetch Data **************/
                return rootView;
            } else if(getArguments().getInt(ARG_SECTION_NUMBER) == 3) {
                rootView = inflater.inflate(R.layout.list_products, container, false);
                Cursor cursor = jdbc.getAllProducts();
                AskAdapter adapter = new AskAdapter(cursor, getContext());
                // Get the List View Instantiated
                final ListView listView = (ListView) rootView.findViewById(R.id.list_of_products);
                listView.setAdapter(adapter);
                return rootView;
            } else if(getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                View rootView = inflater.inflate(R.layout.reviewed_products, container, false);
                Cursor cursor = jdbc.getAllProduct();
                // Set Button Delete
                Button deleteBtn = (Button)rootView.findViewById(R.id.review_delete_button);
                //instantiate custom adapter
                AddProductAdapter adapter = new AddProductAdapter(cursor, getContext());
                // Get the List View Instantiated
                final ListView listView = (ListView) rootView.findViewById(R.id.list_of_products_reviewed);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        //String my_id = (String)view.getTag();
                        //Toast.makeText(ApplicationName.getAppContext(),"Longitude " + longitude + " and LAT is " + latitude, Toast.LENGTH_LONG).show();
                    }
                });
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                adapter.notifyDataSetInvalidated();
                return rootView;
            } else {
                return rootView;
            }

        }
    }

    public static final String md5(final String toEncrypt) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(toEncrypt.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02X", bytes[i]));
            }
            return sb.toString().toLowerCase();
        } catch (Exception exc) {
            return ""; // Impossibru!
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            //return 4;
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                /*
                case 0:
                    return "Add A Product";
                case 1:
                    return "Review A Product";
                case 2:
                    return "List of Products";
                case 3:
                    return "Reviewed Products";
                    */
                case 0:
                    return "Add Product";
                case 1:
                    return "All Products";
            }
            return null;
        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void showProgressProducts(final boolean show, final View viewp) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = ApplicationName.getAppContext().getResources().getInteger(android.R.integer.config_shortAnimTime);

            viewp.setVisibility(show ? View.GONE : View.VISIBLE);
            viewp.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    viewp.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            viewp.setVisibility(show ? View.VISIBLE : View.GONE);
            viewp.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    viewp.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            viewp.setVisibility(show ? View.VISIBLE : View.GONE);
            viewp.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // make the device update its location
        //location.beginUpdates();

        // ...
    }

    @Override
    protected void onPause() {
        // stop location updates (saves battery)
        //location.endUpdates();

        // ...

        super.onPause();
    }

    private void syncHandler(final DBHandler conn, final View rv) {
        Toast.makeText(getApplicationContext(),"Sync Started",Toast.LENGTH_LONG).show();
        // Lets Get the Total Number of Items to be Synchronized
        final TextView stat = (TextView)rv.findViewById(R.id.syncstatus);
        stat.setText("Syncing...");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                conn.synchronize(rv);
                handler.postDelayed(this, 3000);
            }
        }, 5000);
    }
}
