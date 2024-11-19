package ma.ensa.soaptp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ma.ensa.soaptp.R;
import ma.ensa.soaptp.beans.Compte;

public class CompteAdapter extends RecyclerView.Adapter<CompteAdapter.CompteViewHolder> {
    private List<Compte> comptes = new ArrayList<>();
    private OnEditClickListener onEditClick;
    private OnDeleteClickListener onDeleteClick;

    public interface OnEditClickListener {
        void onEditClick(Compte compte);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Compte compte);
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.onEditClick = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClick = listener;
    }

    public void updateComptes(List<Compte> newComptes) {
        comptes.clear();
        comptes.addAll(newComptes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CompteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_account, parent, false);
        return new CompteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompteViewHolder holder, int position) {
        holder.bind(comptes.get(position));
    }

    @Override
    public int getItemCount() {
        return comptes.size();
    }

    public void removeCompte(Compte compte) {
        int position = comptes.indexOf(compte);
        if (position >= 0) {
            comptes.remove(position);
            notifyItemRemoved(position);
        }
    }

    public class CompteViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvId;
        private final TextView tvSolde;
        private final Chip tvType;
        private final TextView tvDate;
        private final MaterialButton btnEdit;
        private final MaterialButton btnDelete;

        public CompteViewHolder(@NonNull View view) {
            super(view);
            tvId = view.findViewById(R.id.tvId);
            tvSolde = view.findViewById(R.id.tvSolde);
            tvType = view.findViewById(R.id.tvType);
            tvDate = view.findViewById(R.id.tvDate);
            btnEdit = view.findViewById(R.id.btnEdit);
            btnDelete = view.findViewById(R.id.btnDelete);
        }

        public void bind(Compte compte) {
            tvId.setText(String.format("Compte N° %d", compte.getId()));
            tvSolde.setText(String.format(Locale.getDefault(), "%.2f$", compte.getSolde()));
            tvType.setText(compte.getType().name());
            tvDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(compte.getDateCreation()));

            btnEdit.setOnClickListener(v -> {
                if (onEditClick != null) {
                    onEditClick.onEditClick(compte);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (onDeleteClick != null) {
                    onDeleteClick.onDeleteClick(compte);
                }
            });
        }
    }
}