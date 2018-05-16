package com.example.lzw.team20.activity;
import com.example.lzw.team20.CreateCode;
import com.example.lzw.team20.R;
import com.example.lzw.team20.WebService;
import com.example.lzw.team20.activity.chatting.ChatClient;
import com.example.lzw.team20.activity.chatting.HTTPUtils;

import common.User;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;



/**
 * Created by Myh on 2018/3/27.
 */



/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView name;
    private EditText password;
    private View mProgressView;
    private View mLoginFormView;
    private ImageButton imageButton;
    private EditText code;
    private String info;
    private static Handler handler;
    private boolean cancel = false;
    private View focusView = null;
    public final static int Login_info=1;
    private final Handler msgHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case R.string.msg_not_network:
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_not_network), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("登录");
        setSupportActionBar(toolbar);*/

        // Set up the login form.
        name = (AutoCompleteTextView) findViewById(R.id.name);
        populateAutoComplete();
        imageButton=(ImageButton) findViewById(R.id.btn_image);
        code=(EditText) findViewById(R.id.verifi_code);
        imageButton.setImageBitmap(CreateCode.createRandomBitmap());
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                imageButton.setImageBitmap(CreateCode.createRandomBitmap());
            }
        });

        password = (EditText) findViewById(R.id.password);
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == 1 || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mRegisterButton=(Button)findViewById(R.id.button_register);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivityForResult(intent,Login_info);
                finish();
            }
        });


        Button mSignInButton = (Button) findViewById(R.id.button_sign_in);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(code.getText().toString().equalsIgnoreCase(CreateCode.code)){
                    attemptLogin();
                }
                else{
                    Toast.makeText(LoginActivity.this,"验证码错误",Toast.LENGTH_LONG).show();
                }
            }
        });

        Button mRetrieveButton = (Button) findViewById(R.id.button_retrieve_password);
        mRetrieveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,RetrieveActivity.class);
                startActivity(intent);
            }
        });


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(name, "Contacts permissions are needed for providing phone completions.", Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }

    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {


        if (mAuthTask != null) {
            return;
        }
        // Reset errors.
        name.setError(null);
        name.setError(null);
        // Store values at the time of the login attempt.
        String nameStr = name.getText().toString();
        String passwordStr = password.getText().toString();

        cancel=false;
        // Check for a valid email address.
        if (TextUtils.isEmpty(nameStr)) {
            name.setError(getString(R.string.hint_prompt_name));
            focusView = name;
            cancel = true;
        }else {
            // Check for a valid password.
            if (TextUtils.isEmpty(passwordStr)){
                password.setError(getString(R.string.hint_prompt_password));
                focusView = name;
                cancel = true;
            }else{
                password.setError(null);
                focusView = password;
                cancel = true;
                new Thread(new LoginThread()).start();
            }
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(nameStr, passwordStr);
            mAuthTask.execute((Void) null);
        }
    }

    public class LoginThread implements Runnable {
        @Override
        public void run() {
            info= WebService.executeLogin(name.getText().toString(),password.getText().toString());

            if (!TextUtils.isEmpty(info)){
                try {
                    JSONObject json=new JSONObject(info);
                    String result=json.getString("result");
                    if (result.equals("false")){
                        Looper.prepare();
                        Toast.makeText(LoginActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }else {
                        //连接聊天服务器
                        //解析json获取用户id
                        JSONObject userdata=json.getJSONObject("data");
                        String userid=userdata.getString("userid");
                        User user=new User();
                        user.setAccount(userid);
                        user.setOperation("login");
                        //在后台注册此线程
                        boolean b=new ChatClient(LoginActivity.this).sendLoginInfo(user);
                        //未连接到服务器
                        //TODO:重传
                        if(!b){
                            Message msg = msgHandler.obtainMessage();
                            msg.arg1 = R.string.msg_not_network;
                            msgHandler.sendMessage(msg);
                            //Looper.prepare();
                            //Snackbar.make(,"连接聊天服务器失败",Snackbar.LENGTH_SHORT).show();
                            //Looper.loop();
                        }

                        HTTPUtils.cookie=WebService.getCookie();
                        Log.d("cookie",HTTPUtils.cookie);
                        JSONObject data=json.getJSONObject("data");
                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        intent.putExtra("data",data.toString());
                        startActivity(intent);
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //连接聊天服务器
    private boolean loginChattingService(String a){
        User user=new User();
        user.setAccount(a);
        user.setOperation("login");
        //在后台注册此线程
        boolean b=new ChatClient(this).sendLoginInfo(user);
        return b;
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        name.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                password.setError(getString(R.string.error_invalid_password));
                password.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    @Override
    public void  onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode==Login_info)
        {
            if (resultCode==RegisterActivity.Register)
            {
                Bundle bundle=data.getExtras();
                name.setText(bundle.getString("name"));
                password.setText(bundle.getString("password"));

            }
        }
    }
    private void saveInfo(){
        SharedPreferences sharedPreferences=getSharedPreferences("userInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("userTel",name.getText().toString());
        editor.putString("password",password.getText().toString());
        //editor.commit();
        editor.apply();
    }

}
