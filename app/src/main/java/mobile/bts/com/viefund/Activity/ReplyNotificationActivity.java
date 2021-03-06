package mobile.bts.com.viefund.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mobile.bts.com.viefund.Application.BTApplication;
import mobile.bts.com.viefund.CustomFont.CustomColor;
import mobile.bts.com.viefund.Entities.AccountInfo;
import mobile.bts.com.viefund.Entities.Plan;
import mobile.bts.com.viefund.MainActivity;
import mobile.bts.com.viefund.Model.UrlModel;
import mobile.bts.com.viefund.MultiLanguage.LanguageUtils;
import mobile.bts.com.viefund.R;

public class ReplyNotificationActivity extends CustomColor {
    public static String TAG = NotificationActivity.class.getSimpleName();
    private Toolbar toolbar;
    TextView txtView_rep_title;
    TextView txtView_rep_from;
    TextView txtView_rep_to;
    EditText txtView_rep_content;
    String TokenTr;
    public static final String url = UrlModel.getUrl_Notification_Send;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageUtils.onAttach(base));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_notification);
        txtView_rep_title = (TextView) findViewById(R.id.txtView_rep_title);
        txtView_rep_content = (EditText) findViewById(R.id.txtView_rep_content);
        txtView_rep_from = (TextView) findViewById(R.id.txtView_rep_from);
        txtView_rep_to = (TextView) findViewById(R.id.txtView_rep_to);

        Intent getIntent = getIntent();
        Bundle bundle = getIntent.getBundleExtra("dataNotification");
        txtView_rep_title.setText(getString(R.string.la_mess_title)+":" + bundle.getString("dataSubject"));
        txtView_rep_from.setText(getString(R.string.la_mess_from)+": Me");
        txtView_rep_to.setText(getString(R.string.la_mess_to)+": " + bundle.getString("dataFrom"));
        TokenTr = bundle.getString("TokenTr");
        toolbar = (Toolbar) findViewById(R.id.toolbarReplyNotification);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView tw = (TextView) toolbar.findViewById(R.id.app_bar_title);
        tw.setText(getString(R.string.nav_notifications_reply) + "");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ChangeThemeToolbar(toolbar,null,(TextView)findViewById(R.id.app_bar_title));
        ChangeBackground();
        ImageView imageView = findViewById(R.id.app_bar_home);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reply, menu);
        MenuItem itemSendReply = menu.findItem(R.id.ic_send_reply);
        itemSendReply.setActionView(R.layout.ic_reply_layout);
        final ImageButton sw = (ImageButton) menu.findItem(R.id.ic_send_reply).getActionView().findViewById(R.id.img_reply_send);

        sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pDialog = new ProgressDialog(getApplicationContext());
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d(TAG, "onResponse() returned: " + response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            int result = Integer.parseInt(jObj.getString("RtnCode"));
                            if (result == 0) {
                                Toast.makeText(getApplicationContext(), "Successfully", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            } else if (result == 1) {
                                Log.d(TAG, "onResponse: " + jObj.getString("ErrorMsg"));
                                Toast.makeText(getApplicationContext(),
                                        jObj.getString("ErrorMsg"), Toast.LENGTH_LONG).show();
                                pDialog.hide();
                            }
                            pDialog.dismiss();
                        } catch (Exception ex) {
                            Toast.makeText(getApplicationContext(),
                                    ex + "", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "onResponse() returned: " + ex);
                            pDialog.dismiss();
                            pDialog.hide();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),
                                error + "", Toast.LENGTH_LONG).show();
                        pDialog.hide();
                        Log.d(TAG, "onErrorResponse() returned: " + error);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        Log.d(TAG, "getParams: " + BTApplication.getInstance().getPrefManager().getUser().getToken());
                        params.put("TokenStr", BTApplication.getInstance().getPrefManager().getUser().getToken());
                        params.put("Subject", txtView_rep_title.getText().toString());
                        params.put("Content", txtView_rep_content.getText().toString());
                        params.put("lg",BTApplication.getInstance().getPrefManager().getUser().getLang());
                        return params;
                    }

                    @Override
                    protected Map<String, String> getPostParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("TokenStr", BTApplication.getInstance().getPrefManager().getUser().getToken());
                        params.put("Subject", txtView_rep_title.getText().toString());
                        params.put("Content", txtView_rep_content.getText().toString());
                        params.put("lg",BTApplication.getInstance().getPrefManager().getUser().getLang());
                        return params;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(
                        30000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                BTApplication.getInstance().addToRequestQueue(request, TAG);
            }
        });
        return true;
    }
}
