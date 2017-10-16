package com.dharmshala.marulohar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dharmshala.marulohar.connection.ConnectionActivity;
import com.dharmshala.marulohar.model.User;
import com.dharmshala.marulohar.utils.BaseActivity;
import com.dharmshala.marulohar.utils.CustomTypefaceSpan;
import com.dharmshala.marulohar.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.Locale;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PICK_IMAGE_REQUEST = 234;
    Context mContext;
    String textName, fbLastname, url, firstName, lastname, email;
    ImageView userProfileImage;
    TextView tvFirstName, tvEmail;
    //    Uri myUri;
    StorageReference storageReference;
    String Storage_Path = "All_Image_Uploads/";
    User user;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private FloatingActionButton fab;
    private Uri filePath;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mFirebaseDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Allowing Strict mode policy for Nougat support
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        mContext = this;
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        language();
        initToolbar();

        firstName = com.dharmshala.marulohar.utils.PreferenceManager.getusername(mContext);
        lastname = com.dharmshala.marulohar.utils.PreferenceManager.getLastname(mContext);
        email = com.dharmshala.marulohar.utils.PreferenceManager.getEmail(mContext);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        initNavigationDrawer();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(mContext.getText(R.string.app_label));
        try {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            TextView toolbarTitle = (TextView) f.get(toolbar);
            toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Bold.ttf"));
            toolbarTitle.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
            toolbarTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) getResources().getDimension(R.dimen.font_extra_large));
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }
    }

    private void language() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration config = getBaseContext().getResources().getConfiguration();

        String lang = settings.getString("LANG", "");
        if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }
    }

    private void initNavigationDrawer() {
        MenuItem menu_home = navigationView.getMenu().findItem(R.id.menu_home);
        MenuItem menu_invitation = navigationView.getMenu().findItem(R.id.menu_invitation);
        MenuItem menu_god = navigationView.getMenu().findItem(R.id.menu_god);
        MenuItem menu_about = navigationView.getMenu().findItem(R.id.menu_about);
        MenuItem menu_news = navigationView.getMenu().findItem(R.id.menu_news);
        MenuItem menu_gallery = navigationView.getMenu().findItem(R.id.menu_gallery);
        MenuItem menu_contact_us = navigationView.getMenu().findItem(R.id.menu_contact_us);
        MenuItem menu_logout = navigationView.getMenu().findItem(R.id.menu_logout);
        applyFontToMenuItem(menu_home);
        applyFontToMenuItem(menu_invitation);
        applyFontToMenuItem(menu_god);
        applyFontToMenuItem(menu_about);
        applyFontToMenuItem(menu_news);
        applyFontToMenuItem(menu_gallery);
        applyFontToMenuItem(menu_contact_us);
        applyFontToMenuItem(menu_logout);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.menu_home:
                        Toast.makeText(mContext, mContext.getText(R.string.nav_home), Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_invitation:
                        Toast.makeText(mContext, mContext.getText(R.string.nav_invitation), Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_god:
                        Toast.makeText(mContext, mContext.getText(R.string.nav_god), Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_about:
                        Toast.makeText(mContext, mContext.getText(R.string.nav_about), Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_news:
                        Toast.makeText(mContext, mContext.getText(R.string.nav_news), Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_gallery:
                        Toast.makeText(mContext, mContext.getText(R.string.nav_gallery), Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_contact_us:
                        Toast.makeText(mContext, mContext.getText(R.string.nav_contact_us), Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_logout:
                        LogOutUser();
                }
                return true;
            }

        });
        View header = navigationView.getHeaderView(0);
        tvEmail = (TextView) header.findViewById(R.id.tv_email);
        tvFirstName = (TextView) header.findViewById(R.id.tv_firstName);
        userProfileImage = (ImageView) header.findViewById(R.id.ivUser);

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
        tvFirstName.setText(firstName + mContext.getString(R.string.space) + lastname);
        tvEmail.setText(email + mContext.getString(R.string.space));

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void LogOutUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure want to Logout").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(MainActivity.this, ConnectionActivity.class);
                        startActivity(intent);
                        com.dharmshala.marulohar.utils.PreferenceManager.setloginstatus(false, mContext);
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Logout");
        alertDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem item = menu.findItem(R.id.action_change_language);
        item.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeLangDialog();
            }
        });
        return true;
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Raleway-SemiBold.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    public void showChangeLangDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.language_dialog, null);
        dialogBuilder.setView(dialogView);

        final Spinner spn = (Spinner) dialogView.findViewById(R.id.spnDialog);
        dialogBuilder.setTitle(getResources().getString(R.string.lang_dialog_title));
        dialogBuilder.setPositiveButton(mContext.getText(R.string.change), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int langpos = spn.getSelectedItemPosition();
                switch (langpos) {
                    case 0: //English
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").commit();
                        setLangRecreate("en");
                        return;
                    case 1: //Hindi
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "hi").commit();
                        setLangRecreate("hi");
                        return;
                    default: //By default set to english
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", "en").commit();
                        setLangRecreate("en");
                        return;
                }
            }
        });
        dialogBuilder.setNegativeButton(mContext.getText(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void setLangRecreate(String langval) {
        Configuration config = getBaseContext().getResources().getConfiguration();
        Locale locale = new Locale(langval);
        Locale.setDefault(locale);
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            recreate();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
//            Glide.with(mContext).load(filePath).into(userProfileImage);
            userProfileImage.setImageURI(filePath);
            uploadFile();
        }
    }

    public String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void uploadFile() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            StorageReference storageReference2nd =
                    storageReference.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(filePath));
            storageReference.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(filePath))
                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(mContext).load(uri.toString())
                            .error(R.drawable.ic_profile)
                            .placeholder(R.drawable.ic_profile).into(userProfileImage);
                }
            });
            storageReference2nd.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Utils.showToast(MainActivity.this, "File Uploaded ");
                            user = new User(firstName, email, taskSnapshot.getDownloadUrl().toString());
                            mFirebaseDatabaseReference.child("ImagesData").child(mFirebaseAuth.getUid()).setValue(user);
//                            userProfileImage.setClickable(false);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Utils.showToast(MainActivity.this, exception.getMessage());

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");

                        }
                    });
        } else {
            Utils.showToast(MainActivity.this, "Please Select Image or Add Image Name");
        }
    }
}
