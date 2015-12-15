package cy.com.morefan.util;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * 自定义RetryPolicy
 */
public class KJJsonObjectRequest extends JsonObjectRequest {


    public KJJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(15000,1,1.0f));
    }

    public KJJsonObjectRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(15000, 1, 1.0f));
    }
}
