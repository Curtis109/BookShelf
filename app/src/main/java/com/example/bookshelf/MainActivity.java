package com.example.bookshelf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookSelectedInterface {

    FragmentManager fm;

    boolean twoPane;
    BookDetailsFragment bookDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        twoPane = findViewById(R.id.container2) != null;

        fm = getSupportFragmentManager();

        fm.beginTransaction()
                .replace(R.id.container1, BookListFragment.newInstance(getTestBooks()))
        .commit();

        /*
        If we have two containers available, load a single instance
        of BookDetailsFragment to display all selected books
         */
        if (twoPane) {
            bookDetailsFragment = new BookDetailsFragment();
            fm.beginTransaction()
                    .replace(R.id.container2, bookDetailsFragment)
                    .commit();
        }
    }

    /*
    Generate an arbitrary list of "books" for testing
     */
    private ArrayList<HashMap<String, String>> getTestBooks() {
        ArrayList<HashMap<String, String>> books = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> book;
        for (int i = 0; i < 10; i++) {
            book = new HashMap<String, String>();
            book.put("title", "Book" + i);
            book.put("author", "Author" + i);
            books.add(book);
        }
        return books;
    };

    @Override
    public void bookSelected(int index) {

        if (twoPane)
            /*
            Display selected book using previously attached fragment
             */
            bookDetailsFragment.displayBook(getTestBooks().get(index));
        else {
            /*
            Display book using new fragment
             */
            fm.beginTransaction()
                    .replace(R.id.container1, BookDetailsFragment.newInstance(getTestBooks().get(index)))
                    // Transaction is reversible
                    .addToBackStack(null)
                    .commit();
        }
    }
}
