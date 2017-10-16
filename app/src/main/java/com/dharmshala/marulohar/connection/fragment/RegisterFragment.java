package com.dharmshala.marulohar.connection.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dharmshala.marulohar.MainActivity;
import com.dharmshala.marulohar.R;
import com.dharmshala.marulohar.core.registration.RegisterContract;
import com.dharmshala.marulohar.core.registration.RegisterPresenter;
import com.dharmshala.marulohar.core.users.add.AddUserContract;
import com.dharmshala.marulohar.core.users.add.AddUserPresenter;
import com.dharmshala.marulohar.model.User;
import com.dharmshala.marulohar.utils.PreferenceManager;
import com.dharmshala.marulohar.utils.Utils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class RegisterFragment extends Fragment implements View.OnClickListener, RegisterContract.View, AddUserContract.View, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = RegisterFragment.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;
    public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9+._%-+]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" +
                    "(" +
                    "." +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" +
                    ")+");
    Context mContext;
    Activity mActivity;
    boolean temp = true;
    EditText etEmail, etPassword, etCnfPassword, etFirstName, etName, etAddress;
    String userInfo;
    User user;
    String emailPass = "", FirstName = "", LastName = "";
    private TextView tvRegister;
    private ProgressDialog mProgressDialog;
    private SignInButton gmailSignIn;
    //Firebase and GoogleApiClient
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mFirebaseDatabaseReference;
    private RegisterPresenter mRegisterPresenter;
    private AddUserPresenter mAddUserPresenter;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Allowing Strict mode policy for Nougat support
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        mActivity = getActivity();
         /*Gmail Login*/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_register, container, false);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        userInfo = mFirebaseDatabaseReference.push().getKey();
        bindViews(fragmentView);
        return fragmentView;
    }

    private void bindViews(View view) {
        etEmail = (EditText) view.findViewById(R.id.etEmail);
        etPassword = (EditText) view.findViewById(R.id.etPassword);
        etCnfPassword = (EditText) view.findViewById(R.id.etCnfPassword);
        etFirstName = (EditText) view.findViewById(R.id.etFirstName);
        etName = (EditText) view.findViewById(R.id.etName);
        etAddress = (EditText) view.findViewById(R.id.etAddress);
        tvRegister = (TextView) view.findViewById(R.id.tvRegister);
        gmailSignIn = (SignInButton) view.findViewById(R.id.gmailSignIn);
        etFirstName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        etName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        etAddress.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        mRegisterPresenter = new RegisterPresenter(this);
        mAddUserPresenter = new AddUserPresenter(this);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(getString(R.string.loading));
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setIndeterminate(true);
        tvRegister.setOnClickListener(this);
        gmailSignIn.setOnClickListener(this);
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

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvRegister:
                String email_id = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String cpwd = etCnfPassword.getText().toString();
                if (!EMAIL_ADDRESS_PATTERN.matcher(email_id).matches()) {
                    Toast.makeText(getActivity(), "Invalid Email Address", Toast.LENGTH_SHORT).show();
                    temp = false;
                } else if (!password.equals(cpwd)) {
                    Toast.makeText(mContext, "Password Not matching", Toast.LENGTH_SHORT).show();
                    temp = false;
                } else if (password.length() < 6 && etCnfPassword.length() < 6) {
                    Utils.showToast(mActivity, "Minimum 6 characters password");
                } else if (!isEmpty(etFirstName) && !isEmpty(etName) && !isEmpty(etEmail)
                        && !isEmpty(etAddress) && password.length() > 6 && etCnfPassword.length() > 6) {
                    myNewUser("MaruLuharData", etFirstName.getText().toString().trim(),
                            etName.getText().toString().trim(),
                            etEmail.getText().toString().trim(),
                            etAddress.getText().toString().trim(),
                            etPassword.getText().toString().trim(),
                            etCnfPassword.getText().toString());

                    FirstName = etFirstName.getText().toString().trim();
                    LastName = etName.getText().toString().trim();
                    emailPass = etEmail.getText().toString().trim();
                    etFirstName.setText("");
                    etName.setText("");
                    etEmail.setText("");
                    etAddress.setText("");
                    etPassword.setText("");
                    etCnfPassword.setText("");
                    mRegisterPresenter.register(getActivity(), email_id, password);
                    mProgressDialog.show();
                } else {
                    if (isEmpty(etFirstName)) {
                        Utils.showToast(mActivity, "Please enter firstname!");
                    } else if (isEmpty(etName)) {
                        Utils.showToast(mActivity, "Please enter lastname!");
                    } else if (isEmpty(etEmail)) {
                        Utils.showToast(mActivity, "Please enter email!");
                    } else if (isEmpty(etAddress)) {
                        Utils.showToast(mActivity, "Please enter address!");
                    } else if (isEmpty(etPassword)) {
                        Utils.showToast(mActivity, "Please enter password!");
                    } else if (isEmpty(etCnfPassword)) {
                        Utils.showToast(mActivity, "Please enter confirm password!");
                    }
                }

                break;
            case R.id.gmailSignIn:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGooogleNAME:" + "  " + acct.getDisplayName());
        Log.d(TAG, "firebaseAuthWithGoooglePhoto:" + "  " + acct.getPhotoUrl());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseDatabaseReference.child("Google").child("email").push().setValue(acct.getEmail(), acct.getPhotoUrl());

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
                            Intent intent = new Intent(getActivity(), MainActivity.class);
//                            intent.putExtra("first_name", acct.getDisplayName());
//                            intent.putExtra("email", acct.getEmail());
//                            intent.putExtra("picture", acct.getPhotoUrl());
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Log.e(TAG, "Google Sign In failed.");
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Utils.initToast(getActivity(), "Google Play Services error.");
    }

    private void myNewUser(String userId, String firstname, String lastname, String email, String address, String password, String confirmpassword) {
        user = new User(firstname, lastname, email, address, password, confirmpassword);
        mFirebaseDatabaseReference.child("Registration").child(userId).push().setValue(user);
    }

    //check if edittext is empty
    private boolean isEmpty(EditText editText) {
        if (editText.getText().toString().trim().length() > 0)
            return false;
        return true;
    }

    @Override
    public void onAddUserSuccess(String message) {
        mProgressDialog.dismiss();
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddUserFailure(String message) {
        mProgressDialog.dismiss();
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRegistrationSuccess(FirebaseUser firebaseUser) {
        mProgressDialog.setMessage(getString(R.string.adding_user_to_db));
        mAddUserPresenter.addUser(getActivity().getApplicationContext(), firebaseUser);
        Intent intent = new Intent(mActivity, MainActivity.class);
        intent.putExtra("email", emailPass);
        intent.putExtra("firstName", FirstName);
        intent.putExtra("lastName", LastName);
        PreferenceManager.setusername(FirstName, mContext);
        PreferenceManager.setLastname(LastName, mContext);
        PreferenceManager.setEmail(emailPass, mContext);
        PreferenceManager.setloginstatus(true, mContext);
        startActivity(intent);
    }

    @Override
    public void onRegistrationFailure(String message) {
        mProgressDialog.dismiss();
        mProgressDialog.setMessage(getString(R.string.please_wait));
        Log.e(TAG, "onRegistrationFailure: " + message);
        Toast.makeText(getActivity(), "Registration failed!+\n" + message, Toast.LENGTH_LONG).show();
    }
}
