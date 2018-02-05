package com.pjanczyk.chip8emulator.ui.programs;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pjanczyk.chip8emulator.R;
import com.pjanczyk.chip8emulator.model.Program;
import com.pjanczyk.chip8emulator.model.ProgramInfo;

import java.util.ArrayList;
import java.util.List;

public class ProgramAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SUBHEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private final List<Object> items;
    private final ItemClickListener itemClickListener;

    public ProgramAdapter(List<ProgramGroup> groups,
                          ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;

        items = new ArrayList<>();
        for (ProgramGroup group : groups) {
            items.add(group);
            items.addAll(group.programs);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SUBHEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_subheader, parent, false);
            return new SubheaderViewHolder(view);
        } else { // VIEW_TYPE_ITEM
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_two_line, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof SubheaderViewHolder) {
            SubheaderViewHolder holder = (SubheaderViewHolder) viewHolder;
            ProgramGroup item = (ProgramGroup) items.get(position);
            holder.textViewTitle.setText(item.name);
        } else {
            ItemViewHolder holder = (ItemViewHolder) viewHolder;
            ProgramInfo program = (ProgramInfo) items.get(position);

            holder.textViewName.setText(program.getName());
            holder.textViewDetails.setText(program.getAuthor());

            holder.itemView.setOnClickListener(v -> {
                ProgramInfo p = (ProgramInfo) items.get(holder.getAdapterPosition());
                itemClickListener.onItemClicked(p);
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);

        if (item instanceof ProgramGroup) {
            return VIEW_TYPE_SUBHEADER;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface ItemClickListener {
        void onItemClicked(ProgramInfo program);
    }

    private static class SubheaderViewHolder extends RecyclerView.ViewHolder {
        public final TextView textViewTitle;

        public SubheaderViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.text_subheader);
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        public final TextView textViewName;
        public final TextView textViewDetails;

        public ItemViewHolder(View view) {
            super(view);
            textViewName = view.findViewById(R.id.text_primary);
            textViewDetails = view.findViewById(R.id.text_secondary);
        }
    }
}
