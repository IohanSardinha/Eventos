package br.com.sardinha.iohan.eventos;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class APIHelper {

    private String s = "";

    public APIHelper(Context context)
    {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest;
        stringRequest = new StringRequest(Request.Method.GET, "http://api-eventos.herokuapp.com/users", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                s = response;
                System.out.println(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                s = error.toString();
                System.out.println(s);
            }
        });
        queue.add(stringRequest);
    }

    public String getUsers()
    {
        return s;
    }

}
