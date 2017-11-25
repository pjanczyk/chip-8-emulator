package com.pjanczyk.chip8emulator.ui.programs;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pjanczyk.chip8emulator.R;
import com.pjanczyk.chip8emulator.model.Program;

import java.util.ArrayList;
import java.util.List;

public class ProgramsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SUBHEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private final List<ProgramGroup> groups;
    private final ProgramsActivity activity;

    private final List<Object> items;

    public ProgramsRecyclerViewAdapter(List<ProgramGroup> groups, ProgramsActivity activity) {
        this.groups = groups;
        this.activity = activity;

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
            holder.titleView.setText(item.name);

        } else {
            final ItemViewHolder holder = (ItemViewHolder) viewHolder;
            Program item = (Program) items.get(position);
            holder.item = item;
            holder.nameView.setText(item.getName());
            holder.detailsView.setText(item.getAuthor());

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != activity) {
                        activity.onProgramSelected(holder.item);
                    }
                }
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

    public class SubheaderViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView titleView;

        public SubheaderViewHolder(View view) {
            super(view);
            this.view = view;
            titleView = (TextView) view.findViewById(R.id.text_subheader);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView nameView;
        public final TextView detailsView;
        public Program item;

        public ItemViewHolder(View view) {
            super(view);
            this.view = view;
            nameView = (TextView) view.findViewById(R.id.text_primary);
            detailsView = (TextView) view.findViewById(R.id.text_secondary);
        }
    }
}
