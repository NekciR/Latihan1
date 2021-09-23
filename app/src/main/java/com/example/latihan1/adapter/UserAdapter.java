package com.example.latihan1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.latihan1.R;
import com.example.latihan1.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<User> {
    private Context mContext;
    private int mResource;

    private ImageView ivImage;
    private TextView tvUsername;
    private Button btnToastUsername;


    public UserAdapter(@NonNull Context context, int resource, @NonNull ArrayList<User> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        convertView = layoutInflater.inflate(mResource,parent,false);

        ivImage = convertView.findViewById(R.id.ivImage);
        tvUsername = convertView.findViewById(R.id.tvUsername);
        btnToastUsername = convertView.findViewById(R.id.btnToastUsername);
        btnToastUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, ""+getItem(position).getLogin(), Toast.LENGTH_SHORT).show();
            }
        });

        Picasso.get().load(getItem(position).getAvatar_url()).into(ivImage);
        tvUsername.setText(getItem(position).getLogin());

        return convertView;
    }

}
