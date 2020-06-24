package com.basicphones.contacts.contactbackup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import com.basicphones.contacts.R;

public class CustomFileListAdapter extends RecyclerView.Adapter<CustomFileListAdapter.FileListViewHolder> {

    private ArrayList<ListItem> listItems;
    private Context context;

    public CustomFileListAdapter(ArrayList<ListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }


    @Override
    public FileListViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.file_list_item, parent, false);
        return new FileListViewHolder(view);
    }

    @Override
    public void onBindViewHolder( FileListViewHolder holder, int position) {
        holder.name.setText(listItems.get(position).getName());
        holder.path.setText(listItems.get(position).getPath());
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    class FileListViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView path;

        FileListViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            path = itemView.findViewById(R.id.path);
        }
    }
}
