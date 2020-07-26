package com.example.attention;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendItem> {
    private String[] dataset;

    public static class FriendItem extends RecyclerView.ViewHolder {

        public TextView textView;

        public FriendItem(View v) {
            super(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button alertButton = v.findViewById(R.id.confirm_button);
                    alertButton.setVisibility(View.VISIBLE);
                }
            });
            textView = v.findViewById(R.id.friend_name);

        }

        public void alert(View v) {
            Button cancel = v.findViewById(R.id.cancel_button);
            cancel.setVisibility(View.VISIBLE);
            Button alert = v.findViewById(R.id.confirm_button);
            alert.setVisibility(View.GONE);
            //todo countdown, then send alert (probably have a service with a thread.sleep(3) and then have cancel stop the service)
        }

        public void cancel(View v) {
            Button cancel = v.findViewById(R.id.cancel_button);
            cancel.setVisibility(View.GONE);
            //todo stop the service/countdown
        }
    }

    public FriendAdapter(String[] myDataset) {
        dataset = myDataset;
    }

    @Override
    public FriendAdapter.FriendItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);

       return new FriendItem(v);
    }

    @Override
    public void onBindViewHolder(FriendItem holder, int position) {
        holder.textView.setText(dataset[position]);
    }

    @Override
    public int getItemCount() {
        return dataset.length;
    }
}
