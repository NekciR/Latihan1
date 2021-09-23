package com.example.latihan1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.latihan1.R;
import com.example.latihan1.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder> {
    private ArrayList<User> UserList;
    private OnItemClickListener mListener;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivImage;
        public TextView tvUsername;
        public Button btnToastUsername;

        public ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            btnToastUsername = itemView.findViewById(R.id.btnToastUsername);
            btnToastUsername.setVisibility(View.GONE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
    public UserRecyclerViewAdapter(ArrayList<User> list) {
        UserList = list;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row, parent, false);
        ViewHolder evh = new ViewHolder(v, mListener);
        return evh;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User currentItem = UserList.get(position);
        Picasso.get().load(currentItem.getAvatar_url()).into(holder.ivImage);
        holder.tvUsername.setText(currentItem.getLogin());
    }
    @Override
    public int getItemCount() {
        return UserList.size();
    }
}

