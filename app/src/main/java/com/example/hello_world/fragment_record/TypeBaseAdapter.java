package com.example.hello_world.fragment_record;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hello_world.Database.TypeIn;
import com.example.hello_world.R;

import java.util.List;

public class TypeBaseAdapter extends BaseAdapter {

    Context context;
    List<TypeIn>myData;
    int selectPosition = 0;

    public TypeBaseAdapter(Context context,List<TypeIn>myData){
        this.context=context;
        this.myData=myData;
    }
    @Override
    public int getCount() {
        return myData.size();
    }

    @Override
    public Object getItem(int i) {
        return myData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.item_record,viewGroup,false);

        ImageView imageView = view.findViewById(R.id.item_record_image);
        TextView textView = view.findViewById(R.id.item_record_text);

        TypeIn typeIn = myData.get(i);
        textView.setText(typeIn.getTypename());
        if (selectPosition == i){
            imageView.setImageResource(typeIn.getFocusImageID());
        }
        else{
            imageView.setImageResource(typeIn.getImageID());
        }

        return view;
    }
}
