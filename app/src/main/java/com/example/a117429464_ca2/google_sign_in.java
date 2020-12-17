package com.example.a117429464_ca2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class google_sign_in extends Fragment implements View.OnClickListener {
    //docs for the following code
    //https://developer.android.com/training/sign-in
    //https://developers.google.com/identity/sign-in/android/sign-in
    GoogleSignInClient mGoogleSignInClient;
    static GoogleSignInAccount account;
    TextView tvEmail, tvName;
    ImageView ivProfilePic;

    com.google.android.gms.common.SignInButton signInButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_google_sign_in, container, false);

        setTitle();
        attachResources(v);
        signInButton.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this.getContext(), gso);
        return v;
    }

    private void setTitle() {
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        String title = (account != null) ? "Your Google" : "Sign into google";
        actionBar.setTitle(title);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            setTitle();
            if (account != null) {
                updateUI(true);

            } else {
                updateUI(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        account = GoogleSignIn.getLastSignedInAccount(this.getContext());
        setTitle();
        try {
            if (account != null) {
                updateUI(true);
            } else {
                updateUI(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            updateUI(true);
        } catch (ApiException | IOException e) {
            Log.e("TAG", "signInResult:failed code=" + e.getMessage());
        }
    }

    private void updateUI(Boolean visible) throws IOException {
        if (visible) {
            signInButton.setVisibility(View.INVISIBLE);
            updateResources();
        } else {
            signInButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
        }
    }

    private void updateResources() {
        //https://bumptech.github.io/glide/doc/options.html docs to Glide library
        if(account.getEmail() != null){
            Glide.with(this.getContext()).load(account.getPhotoUrl()).apply(new RequestOptions().override(700, 700)).into(ivProfilePic);
        }
        if(account.getEmail() != null){
            tvEmail.setText(account.getEmail());
        }
        if(account.getDisplayName() != null){
            tvName.setText(account.getDisplayName());
        }

    }

    private void attachResources(View v) {
        signInButton = v.findViewById(R.id.sign_in_button);
        tvEmail = v.findViewById(R.id.tvEmail);
        ivProfilePic = v.findViewById(R.id.ivProfilepic);
        tvName = v.findViewById(R.id.tvName);
    }

}
