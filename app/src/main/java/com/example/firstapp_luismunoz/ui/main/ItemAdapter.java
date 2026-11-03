package com.example.firstapp_luismunoz.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapp_luismunoz.R;
import com.example.firstapp_luismunoz.model.Item;

/** Displays Items; forwards button taps to a listener. Has no database access. */
public class ItemAdapter extends ListAdapter<Item, ItemAdapter.ViewHolder> {

    public interface Listener {
        void onIncrement(Item item);

        void onDecrement(Item item);

        void onDelete(Item item);
    }

    private static final DiffUtil.ItemCallback<Item> DIFF = new DiffUtil.ItemCallback<Item>() {
        @Override
        public boolean areItemsTheSame(@NonNull Item a, @NonNull Item b) {
            return a.getId() == b.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Item a, @NonNull Item b) {
            return a.equals(b);
        }
    };

    private final Listener listener;

    public ItemAdapter(Listener listener) {
        super(DIFF);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = getItem(position);
        holder.nameText.setText(holder.itemView.getContext().getString(R.string.item_name_format, item.getName()));
        holder.quantityText.setText(holder.itemView.getContext().getString(R.string.item_quantity_format, item.getQuantity()));
        holder.incrementButton.setOnClickListener(v -> listener.onIncrement(item));
        holder.decrementButton.setOnClickListener(v -> listener.onDecrement(item));
        holder.deleteButton.setOnClickListener(v -> listener.onDelete(item));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameText;
        final TextView quantityText;
        final Button incrementButton;
        final Button decrementButton;
        final Button deleteButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.item_name_text);
            quantityText = itemView.findViewById(R.id.item_quantity_text);
            incrementButton = itemView.findViewById(R.id.button_increment);
            decrementButton = itemView.findViewById(R.id.button_decrement);
            deleteButton = itemView.findViewById(R.id.button_delete);
        }
    }
}
