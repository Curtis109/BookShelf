package samples.speech.cognitiveservices.microsoft.bookshelf;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements BookListFragment.ChosenBookInterface{

    BookDetailsFragment selector;
    BookListFragment selection;
    public static final String colorName = "dataToPass";
    private final int REQUEST_CODE = 1111;

    public static final String KEY_NAME = "title";
    public static final String KEY_AUTHOR = "author";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selector = new BookDetailsFragment();
        selection = new BookListFragment();
        selection = BookListFragment.newInstance(0);
        selector = BookDetailsFragment.newInstance(0);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container2, selector)
                .add(R.id.ListBook, selector)
                .commit();


        /*
        Resources res = getResources();
        String[] titles = res.getStringArray(R.array.bookTitles);
        int listSize = titles.length;
        String[] authors = res.getStringArray(R.array.bookAuthors);

        ArrayList<HashMap<String, String>> bookList = new ArrayList<HashMap<String, String>>();
        HashMap<String,String> hm = new HashMap<String, String>();
        for(int i=0; i < listSize; i++){
            hm.put(titles[i], authors[i]);
        }
        bookList.add(hm);*/
    }
    public void sendBook(String givenBook){
        selector.changeDetails(givenBook);
    }

}
