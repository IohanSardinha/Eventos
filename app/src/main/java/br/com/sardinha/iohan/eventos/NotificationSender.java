package br.com.sardinha.iohan.eventos;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class NotificationSender {

    public void Test(Context context, final String title, final String message, final String topic)
    {
        String URL = "https://fcm.googleapis.com/fcm/send";

        StringRequest stringRequest = new StringRequest(Request.Method.POST,URL, new Response.Listener<String>()
        {

            @Override
            public void onResponse(String response) {

            }

        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("Authorization","key=AAAAL9cd1l4:APA91bFoetg87fik1lXFDMwERGjSyNaa_O-QafuKZG3xqCjkE7RxyfjGvkpNjbD0csZJs03UWym9_btuuCCXnDGORIaKPuwZrvgyhNyKskGgjw8n-sd4YtVnqySzIJv8MJtAurxp8_KL");
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                String body =
                        "{" +
                            "\"to\":" +
                                "\"/topics/"+topic+"\"" +
                            "," +
                            "\"data\":" +
                            "{" +
                                "\"extra_information\" : \"Informacao extra\"" +
                            "}," +
                            "\"notification\":" +
                            "{" +
                                "\"title\" : \""+title+"\"," +
                                "\"text\" : \""+message+"\"" +
                            "}" +
                        "}";

                return body.getBytes();
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

}
