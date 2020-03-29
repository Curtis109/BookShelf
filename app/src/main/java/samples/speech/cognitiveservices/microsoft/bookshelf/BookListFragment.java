package samples.speech.cognitiveservices.microsoft.bookshelf;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;

public class BookListFragment extends Fragment {

    ListView bookList;

    private static final String ID_KEY = "id";
    int id = -1;
    public String selectedBook;

    ChosenBookInterface parentActivity;

    public BookListFragment(){}

    public static BookListFragment newInstance (int id){
        BookListFragment fragment = new BookListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ID_KEY, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if(bundle != null){
            id = bundle.getInt("id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View bookListView = inflater.inflate(R.layout.book_text_view, container, false);

        bookList = bookListView.findViewById(R.id.ListBook);
        getBooks();
        return bookListView;
    }

    public void getBooks(){

        Resources res = getResources();
        String[] titles = res.getStringArray(R.array.bookTitles);
        int listSize = titles.length;
        String[] authors = res.getStringArray(R.array.bookAuthors);

        ArrayList<HashMap<String, String>> bookListArray = new ArrayList<HashMap<String, String>>();
        HashMap<String,String> hm = new HashMap<String, String>();
        for(int i=0; i < listSize; i++){
            hm.put(titles[i], authors[i]);
            bookListArray.add(hm);
        }
        //bookListArray.add(hm);

        ArrayAdapter<HashMap<String, String>> bookListAdapter =
                new ArrayAdapter<HashMap<String, String>>(getActivity(),
                        android.R.layout.simple_list_item_1, bookListArray);

        bookList.setAdapter(bookListAdapter);
        bookList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
                selectedBook = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    interface ChosenBookInterface{
        public void sendBook(String thisBook);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        try{
            parentActivity = (ChosenBookInterface) getActivity();
        }catch (ClassCastException e) {
            throw new ClassCastException("Error retrieving data. Please try again.");
        }
    }

    public int getFragmentId(){return id;}

}



