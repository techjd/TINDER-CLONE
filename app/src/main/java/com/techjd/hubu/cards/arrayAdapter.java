package com.techjd.hubu.cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.techjd.hubu.R;

import org.w3c.dom.Text;

import java.util.List;



public class arrayAdapter extends ArrayAdapter<cards> {

    private Context mContext;

    public arrayAdapter(Context context, int resourceId, List<cards> items) {
        super(context, resourceId, items);
        this.mContext = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        cards cardItem = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.name);
        ImageView profilePicture = (ImageView) convertView.findViewById(R.id.image);

        name.setText(cardItem.getName());

        // If image url is assigned to default, it will automatically assign a default image.
        if(cardItem.getProfileImageUrl().equals("default")) {
            Glide.with(convertView.getContext()).load(R.mipmap.ic_launcher).into(profilePicture);
        }
        else {
            Glide.clear(profilePicture);
            Glide.with(convertView.getContext()).load(cardItem.getProfileImageUrl()).into(profilePicture);
        }


        return convertView;
    }
}
