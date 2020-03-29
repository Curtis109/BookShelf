package samples.speech.cognitiveservices.microsoft.bookshelf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class BookAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<HashMap<String,String>> bookList;
    private Context context;


    public BookAdapter(ArrayList<HashMap<String,String>> list, Context context){
        this.bookList = list;
        this.context = context;
    }

    @Override
    public int getCount(){
        return bookList.size();
    }

    @Override
    public Object getItem(int pos){
        return bookList.get(pos);
    }

    @Override
    public long getItemId(int pos){
        return 0;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent){
        View view = convertView;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.book_text_view, null);

        }
        return convertView;
    }
}
