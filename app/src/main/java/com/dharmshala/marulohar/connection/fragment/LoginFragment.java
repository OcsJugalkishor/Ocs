package com.dharmshala.marulohar.connection.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dharmshala.marulohar.MainActivity;
import com.dharmshala.marulohar.R;
import com.dharmshala.marulohar.connection.ConnectionActivity;
import com.dharmshala.marulohar.core.login.LoginContract;
import com.dharmshala.marulohar.core.login.LoginPresenter;
import com.dharmshala.marulohar.utils.Fireplace;
import com.dharmshala.marulohar.utils.PreferenceManager;
import com.dharmshala.marulohar.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class LoginFragment extends Fragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, LoginContract.View {
    private static final String TAG = ConnectionActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;
    public static LoginFragment loginfragment;
    boolean loggedIn;
    Context mContext;
    Activity mActivity;
    String fbuser_mail = "", fbuser_firstname = "", fbuser_lastname = "", fbuser_name = "", fbuser_id = "";
    String facebook_id, first_name, last_name, email, picture;
    private EditText edEmail, edPassword;
    private TextView tvRegister;
    private Button btnLogin, btnGmailLogin;
    private LoginButton btnFacebookLogin;
    private GoogleApiClient mGoogleApiClient;
    private String accessToken;
    private CallbackManager callbackManager;
    //Firebase
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;
    private LoginPresenter mLoginPresenter;
    private ProgressDialog mProgressDialog;

    public LoginFragment() {
    }

    public LoginFragment getInstance() {
        return loginfragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        getFbKeyHash(mActivity.getPackageName());

        /*Facebook Login*/
        FacebookSdk.sdkInitialize(getContext());
        loggedIn = AccessToken.getCurrentAccessToken() != null;

        AppEventsLogger.activateApp(mContext);

        facebook_id = first_name = last_name = email = picture = "";
        /*Gmail Login*/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
//        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
//                .enableAutoManage(getActivity(), this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();

        /*Firebase Auth*/
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_login, container, false);
        bindViews(fragmentView);
        return fragmentView;
    }

    private void bindViews(View view) {
        btnLogin = (Button) view.findViewById(R.id.btnLogin);
        btnGmailLogin = (Button) view.findViewById(R.id.btnGmailLogin);
        btnFacebookLogin = (LoginButton) view.findViewById(R.id.btnFacebookLogin);
        tvRegister = (TextView) view.findViewById(R.id.tvRegister);
        edEmail = (EditText) view.findViewById(R.id.edEmail);
        edPassword = (EditText) view.findViewById(R.id.edPassword);

    }

    public void getFbKeyHash(String packageName) {
        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = getActivity().getPackageManager().getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("Testing...YourKeyHash :", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException ignored) {

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        mLoginPresenter = new LoginPresenter(this);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(getString(R.string.loading));
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setIndeterminate(true);
        btnLogin.setOnClickListener(this);
        btnGmailLogin.setOnClickListener(this);
        btnFacebookLogin.setOnClickListener(this);
        btnFacebookLogin.setFragment(this);
        tvRegister.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvRegister:
                getFragmentManager().beginTransaction().replace(R.id.container, new RegisterFragment(), "RegisterFragment")
                        .addToBackStack("LoginFragment").commit();
                break;
            case R.id.btnLogin:
                onLogin(view);
                break;
            case R.id.btnGmailLogin:
                googleSignIn();
                break;
            case R.id.btnFacebookLogin:
                connectFacebook();
                logoutFacebook();
                break;
        }
    }

    private void onLogin(View view) {
        String emailId = edEmail.getText().toString();
        String password = edPassword.getText().toString();
        mLoginPresenter.login(getActivity(), emailId, password);
        mProgressDialog.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void logoutFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return;
        }
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance().logOut();
            }
        }).executeAsync();
    }

    private void connectFacebook() {
        try {
            if (AccessToken.getCurrentAccessToken() != null) {
                System.out.println("Current user AccessToken " + AccessToken.getCurrentAccessToken());
                LoginManager.getInstance().logOut();
            }
            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "user_photos", "user_birthday"));
            btnFacebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {
                    accessToken = loginResult.getAccessToken().getToken();
                    Log.d("AccessToken___", "AccessToken___" + accessToken);
                    Profile profile = Profile.getCurrentProfile();
                    System.out.println("Profile " + profile);
                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            System.out.println("Testing..fblogin..JSON Result" + object);
                            try {
                                JSONObject imageObject = object.getJSONObject("picture").getJSONObject("data");
                                String url = imageObject.getString("url");
                                fbuser_mail = object.optString("email");
                                fbuser_firstname = object.optString("first_name");
                                fbuser_lastname = object.optString("last_name");
                                fbuser_name = object.optString("name");
                                fbuser_id = object.optString("id");

                                if (mActivity != null) {
                                    Intent intent = new Intent(mActivity, MainActivity.class);
//                                    intent.putExtra("type", "facebook");
//                                    intent.putExtra("facebook_id", fbuser_id);
//                                    intent.putExtra("first_name", fbuser_firstname);
//                                    intent.putExtra("last_name", fbuser_lastname);
                                    intent.putExtra("url", url);
                                    ((ConnectionActivity) mActivity).startActivity(intent);
//                                    startActivity(intent);
                                }

                                Log.d("-----------", "FirstName" + fbuser_firstname + "LastName____" + fbuser_lastname
                                        + "Url" + url);


                                btnFacebookLogin.setVisibility(View.GONE);
                                btnGmailLogin.setVisibility(View.GONE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,first_name,last_name,email,picture");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

                @Override
                public void onCancel() {
                    // code for cancellation
                    Toast.makeText(getActivity(), "Facebook login cancel", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onError(FacebookException exception) {
                    //  code to handle error
                    Toast.makeText(getActivity(), "facebook login failed", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();


        }
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        if (callbackManager != null) {
            getActivity().finish();
            callbackManager.onActivityResult(requestCode, responseCode, data);
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("OnFailedRequest", "onConnectionFailed:" + connectionResult);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            final GoogleSignInAccount gmailAccount = result.getSignInAccount();
            if (gmailAccount == null) {
                Fireplace.e("Error Google Login", "Account of user is null");
            } else {
                if (gmailAccount.getId() == null || gmailAccount.getEmail() == null || gmailAccount.getDisplayName() == null) {
                    Fireplace.e("Error Google Account", gmailAccount.toString());
                } else {
                    try {
//                        syncGoogleAccount();
                        accessToken = gmailAccount.getIdToken();
                        Fireplace.d("GmailAccount", "" + gmailAccount.getId() +
                                "--" + accessToken +
                                "--Profil_eName--" + gmailAccount.getDisplayName() +
                                "--Email_ID--" + gmailAccount.getEmail() +
                                "--Profile_Image_--" + gmailAccount.getPhotoUrl());

                        System.out.println(gmailAccount.getDisplayName());
                        firebaseAuthWithGoogle(gmailAccount);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount gmailAccount) {
        Log.d(TAG, "firebaseAuthWithGooogleID:" + "  " + gmailAccount.getId());
        Log.d(TAG, "firebaseAuthWithGooogleGETOKEN:" + "  " + gmailAccount.getIdToken());
        Log.d(TAG, "firebaseAuthWithGooogleNAME:" + "  " + gmailAccount.getDisplayName());
        Log.d(TAG, "firebaseAuthWithGoooglePhoto:" + "  " + gmailAccount.getPhotoUrl());
        AuthCredential credential = GoogleAuthProvider.getCredential(gmailAccount.getIdToken(), null);
        mDatabaseReference.child("Google").child("email").push().setValue(gmailAccount.getEmail());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Utils.showToast(getActivity(), "Authentication failed");
                        } else {
                            Log.d("Logged gmail :", "Successfully logged into google");
                            if (mActivity != null) {
                                Intent intent = new Intent(mActivity, MainActivity.class);
//                                intent.putExtra("first_name", gmailAccount.getDisplayName());
//                                intent.putExtra("email", gmailAccount.getEmail());
//                                intent.putExtra("picture", gmailAccount.getPhotoUrl());
                                startActivity(intent);
                            }
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        }
                    }
                });
    }

    @Override
    public void onLoginSuccess(String message) {
        mProgressDialog.dismiss();
        Toast.makeText(getActivity(), "Logged in successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        PreferenceManager.setloginstatus(true, mContext);
        startActivity(intent);
    }

    @Override
    public void onLoginFailure(String message) {
        mProgressDialog.dismiss();
        Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
    }

}
