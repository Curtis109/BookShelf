package samples.speech.cognitiveservices.microsoft.bookshelf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaSync;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import javax.security.auth.callback.Callback;

import edu.temple.audiobookplayer.AudiobookService;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookSelectedInterface {

    private static final String BOOKS_KEY = "books";
    private static final String SELECTED_BOOK_KEY = "selectedBook";

    Intent serviceIntent;
    boolean connected;
    boolean isPlayingCheck;
    AudiobookService audiobookService;
    AudiobookService.MediaControlBinder mediaControlBinder;
    AudiobookService.BookProgress progress;

    SeekBar progressSeekbar;
    int maxValue;
    int currentProgress;
    Runnable runnable;
    Message message;

    FragmentManager fm;

    boolean twoPane;
    BookListFragment bookListFragment;
    BookDetailsFragment bookDetailsFragment;

    ArrayList<Book> books;
    RequestQueue requestQueue;
    Book selectedBook;

    EditText searchEditText;
    TextView playingNow;
    Button pauseButton;
    Button stopButton;


    private final String SEARCH_API = "https://kamorris.com/lab/abp/booksearch.php?search=";

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            connected = true;
            mediaControlBinder = (AudiobookService.MediaControlBinder) service;
            mediaControlBinder.setProgressHandler(handler);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mediaControlBinder = null;
            connected = false;
        }
    };
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {

            if (mediaControlBinder.isPlaying()) {
                updateSeekbar(((AudiobookService.BookProgress) message.obj).getProgress());
            }
            return false;
        }
    });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playingNow = findViewById(R.id.nowPlaying);
        searchEditText = findViewById(R.id.searchEditText);

        serviceIntent = new Intent(MainActivity.this, AudiobookService.class);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
        /*
        Perform a search
         */
        findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchBooks(searchEditText.getText().toString());
            }
        });

        pauseButton = (Button) findViewById(R.id.buttonPause);
        pauseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mediaControlBinder.pause();
                isPlayingCheck = mediaControlBinder.isPlaying();
                if(isPlayingCheck){
                    pauseButton.setText("Pause");
                }
                else{
                    pauseButton.setText("Unpause");
                }
            }
        });

        stopButton = (Button) findViewById(R.id.buttonStop);
        stopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mediaControlBinder.stop();
                progressSeekbar.setProgress(0);
                stopService(serviceIntent);
            }
        });

        progressSeekbar = (SeekBar) findViewById(R.id.seekBar);
        progressSeekbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(fromUser){
                            mediaControlBinder.seekTo(progress);
                        }

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );

        /*
        If we previously saved a book search and/or selected a book, then use that
        information to set up the necessary instance variables
         */
        if (savedInstanceState != null) {
            books = savedInstanceState.getParcelableArrayList(BOOKS_KEY);
            selectedBook = savedInstanceState.getParcelable(SELECTED_BOOK_KEY);
        }
        else
            books = new ArrayList<Book>();

        twoPane = findViewById(R.id.container2) != null;
        fm = getSupportFragmentManager();

        requestQueue = Volley.newRequestQueue(this);

        /*
        Get an instance of BookListFragment with an empty list of books
        if we didn't previously do a search, or use the previous list of
        books if we had previously performed a search
         */
        bookListFragment = BookListFragment.newInstance(books);

        fm.beginTransaction()
                .replace(R.id.container1, bookListFragment)
                .commit();

        /*
        If we have two containers available, load a single instance
        of BookDetailsFragment to display all selected books.

        If a book was previously selected, show that book in the book details fragment
        *NOTE* we could have simplified this to a single line by having the
        fragment's newInstance() method ignore a null reference, but this way allow
        us to limit the amount of things we have to change in the Fragment's implementation.
         */
        if (twoPane) {
            if (selectedBook != null)
                bookDetailsFragment = BookDetailsFragment.newInstance(selectedBook);
            else
                bookDetailsFragment = new BookDetailsFragment();

            fm.beginTransaction()
                    .replace(R.id.container2, bookDetailsFragment)
                    .commit();
        } else {
            if (selectedBook != null) {
                fm.beginTransaction()
                        .replace(R.id.container1, BookDetailsFragment.newInstance(selectedBook))
                        // Transaction is reversible
                        .addToBackStack(null)
                        .commit();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        connected = true;
    }

    /*
        Fetch a set of "books" from from the web service API
         */
    private void fetchBooks(String searchString) {
        /*
        A Volloy JSONArrayRequest will automatically convert a JSON Array response from
        a web server to an Android JSONArray object
         */
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(SEARCH_API + searchString, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() > 0) {
                    books.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject bookJSON;
                            bookJSON = response.getJSONObject(i);
                            books.add(new Book (bookJSON.getInt(Book.JSON_ID),
                                    bookJSON.getString(Book.JSON_TITLE),
                                    bookJSON.getString(Book.JSON_AUTHOR),
                                    bookJSON.getString(Book.JSON_COVER_URL),
                                    bookJSON.getInt(Book.JSON_DURATION)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    updateBooksDisplay();
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.search_error_message), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonArrayRequest);
    };

    private void updateBooksDisplay() {
        /*
        Remove the BookDetailsFragment from the container after a search
        if it is the currently attached fragment
         */
        if (fm.findFragmentById(R.id.container1) instanceof BookDetailsFragment)
            fm.popBackStack();
        bookListFragment.updateBooksDisplay(books);
    }

    @Override
    public void bookSelected(int index) {
        selectedBook = books.get(index);
        if (twoPane)
            /*
            Display selected book using previously attached fragment
             */
            bookDetailsFragment.displayBook(selectedBook);
        else {
            /*
            Display book using new fragment
             */
            fm.beginTransaction()
                    .replace(R.id.container1, BookDetailsFragment.newInstance(selectedBook))
                    // Transaction is reversible
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save previously searched books as well as selected book
        outState.putParcelableArrayList(BOOKS_KEY, books);
        outState.putParcelable(SELECTED_BOOK_KEY, selectedBook);
    }
    public void playSelectedBook(Book book){
        //Toast.makeText(MainActivity.this, String.valueOf(id), Toast.LENGTH_SHORT).show();

        if(connected){
            mediaControlBinder.play(book.getId());
            maxValue = book.getDuration();
            progressSeekbar.setMax(maxValue);
            playingNow.setText("Now Playing: " + book.getTitle());
            startService(serviceIntent);
        }
        else{
            Toast.makeText(MainActivity.this, "You are not connected to the Audio Stream Service.", Toast.LENGTH_SHORT).show();
        }

    }
    public void updateSeekbar(int newProgress){

        progressSeekbar.setProgress(newProgress);
    }
}


