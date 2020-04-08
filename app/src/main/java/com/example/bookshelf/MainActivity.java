package com.example.bookshelf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookSelectedInterface {

    FragmentManager fm;

    EditText inputEditText;
    RequestQueue requestQueue;

    ArrayList<Book> books;
    boolean twoPane;
    BookDetailsFragment bookDetailsFragment;

    int id;
    String title;
    String author;
    String imgURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        twoPane = findViewById(R.id.container2) != null;

        requestQueue = Volley.newRequestQueue(this);

        inputEditText = findViewById(R.id.inputEditText);

        findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://kamorris.com/lab/abp/booksearch.php?search=" + inputEditText.getText().toString();
                Log.w("what it is", "on click = " + url);
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {

                                for(int i=0; i < response.length(); i++){

                                    try {
                                        JSONArray allBooks = new JSONArray(response);
                                        JSONObject row = allBooks.getJSONObject(i);
                                        id = row.getInt("book_id");
                                        Log.w("what it is", "id = " + id);
                                        title = row.getString("title");
                                        Log.w("what it is", "title = " + title);
                                        author = row.getString("author");
                                        Log.w("what it is", "author = " + author);
                                        imgURL = row.getString("cover_url");
                                        Log.w("what it is", "imgURL= " + imgURL);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                        Book book = null;
                                        book.id = id;
                                        book.title = title;
                                        book.author = author;
                                        book.coverURL = imgURL;
                                        books.add(book);

                                }
                                fm = getSupportFragmentManager();

                                fm.beginTransaction()
                                        .replace(R.id.container1, BookListFragment.newInstance(books))
                                        .commit();


                                if (twoPane) {
                                    bookDetailsFragment = new BookDetailsFragment();
                                    fm.beginTransaction()
                                            .replace(R.id.container2, bookDetailsFragment)
                                            .commit();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, "Error finding books.", Toast.LENGTH_LONG).show();
                            }
                        });
                requestQueue.add(jsonArrayRequest);
            }
        });

    }

    /*
    Generate an arbitrary list of "books" for testing
     */

    @Override
    public void bookSelected(int index) {

        if (twoPane)
            /*
            Display selected book using previously attached fragment
             */
            bookDetailsFragment.displayBook(books.get(index));
        else {
            /*
            Display book using new fragment
             */
            fm.beginTransaction()
                    .replace(R.id.container1, BookDetailsFragment.newInstance(books.get(index)))
                    // Transaction is reversible
                    .addToBackStack(null)
                    .commit();
        }
    }
}