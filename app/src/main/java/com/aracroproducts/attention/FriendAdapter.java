package com.aracroproducts.attention;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendItem> {
    private String[] dataset;

    public static class FriendItem extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView textView;
        private Button confirmButton;
        private Button cancelButton;

        private enum State {
            NORMAL, CONFIRM, CANCEL
        }

        private State alertState = State.NORMAL;

        public FriendItem(View v) {
            super(v);

            textView = v.findViewById(R.id.friend_name);
            confirmButton = v.findViewById(R.id.confirm_button);
            cancelButton = v.findViewById(R.id.cancel_button);

            textView.setOnClickListener(this);
            confirmButton.setOnClickListener(this);
            cancelButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == textView.getId()) {
                switch (alertState) {
                    case NORMAL:
                        prompt();
                        break;
                    case CONFIRM:
                        cancel();
                        break;
                }
            } else if (v.getId() == confirmButton.getId()) {
                alert();
            } else if (v.getId() == cancelButton.getId()) {
                cancel();

            }
        }

        public void prompt() {
            confirmButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.GONE);
            alertState = State.CONFIRM;
        }

        public void alert() {
            cancelButton.setVisibility(View.VISIBLE);
            confirmButton.setVisibility(View.GONE);
            alertState = State.CANCEL;
            //todo countdown, then send alert (probably have a service with a thread.sleep(3) and then have cancel stop the service)
        }

        public void cancel() {
            cancelButton.setVisibility(View.GONE);
            confirmButton.setVisibility(View.GONE);
            alertState = State.NORMAL;
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
