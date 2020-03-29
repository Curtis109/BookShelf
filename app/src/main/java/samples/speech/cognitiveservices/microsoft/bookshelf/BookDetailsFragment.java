package samples.speech.cognitiveservices.microsoft.bookshelf;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BookDetailsFragment extends Fragment {

    private static final String ID_KEY = "id";
    int id = -1;
    TextView titleTextView;
    View myLayout;

    public BookDetailsFragment(){}

    public static BookDetailsFragment newInstance(int id){
        BookDetailsFragment fragment = new BookDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ID_KEY, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        myLayout = inflater.inflate(R.layout.book_detail_view, container, false);
        return myLayout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        titleTextView = (TextView)view.findViewById(R.id.bookDetailView);
    }

    void changeDetails(String title){
        titleTextView.setText(title);

    }
}
