package jp.fkmsoft.demotwitterlogin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterListener;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;


public class MainActivity extends Activity {

    private static final String CONSUMER_KEY = "";
    private static final String CONSUMER_SECRET = "";
    private static final String CALLBACK_URI = "twittercallback://callback";

    private AsyncTwitter mTwitter;
    private RequestToken mRequestToken;

    private TextView mMessageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTwitter = new AsyncTwitterFactory().getInstance();
        mTwitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        mTwitter.addListener(mListener);

        mMessageText = (TextView) findViewById(R.id.text_message);
        findViewById(R.id.button_login).setOnClickListener(mClickListener);

        addMessage(getString(R.string.msg_end_on_create));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        addMessage(getString(R.string.msg_start_new_intent));

        Uri uri = intent.getData();
        String verifier = uri.getQueryParameter("oauth_verifier");
        if (verifier != null) {
            mTwitter.getOAuthAccessTokenAsync(mRequestToken, verifier);
        }
    }

    private void addMessage(String message) {
        mMessageText.setText(mMessageText.getText().toString() + "\n" + message);
    }

    private final TwitterListener mListener = new TwitterAdapter() {
        @Override
        public void gotOAuthRequestToken(RequestToken token) {
            mRequestToken = token;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mRequestToken.getAuthorizationURL()));
            startActivity(intent);
        }

        @Override
        public void gotOAuthAccessToken(AccessToken token) {
            addMessage(getString(R.string.msg_called_oauth_access_token));
        }
    };

    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.button_login:
                addMessage(getString(R.string.msg_start_oauth_request_token_request));
                mTwitter.getOAuthRequestTokenAsync(CALLBACK_URI);
                break;
            }
        }
    };

}
