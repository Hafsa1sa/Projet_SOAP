package ma.ensa.soaptp;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ma.ensa.soaptp.adapter.CompteAdapter;
import ma.ensa.soaptp.beans.Compte;
import ma.ensa.soaptp.beans.TypeCompte;
import ma.ensa.soaptp.ws.SoapService;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private final CompteAdapter adapter = new CompteAdapter();
    private final SoapService soapService = new SoapService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupRecyclerView();
        setupListeners();
        loadComptes();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnDeleteClickListener(compte -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Supprimer le compte")
                    .setMessage("Voulez-vous vraiment supprimer ce compte ?")
                    .setPositiveButton("Supprimer", (dialog, which) -> {
                        ExecutorService executorService = Executors.newSingleThreadExecutor();
                        executorService.execute(() -> {
                            boolean success = soapService.deleteCompte(compte.getId());
                            runOnUiThread(() -> {
                                if (success) {
                                    adapter.removeCompte(compte);
                                } else {
                                    adapter.removeCompte(compte);
                                }
                            });
                        });
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });
    }

    private void setupListeners() {
        fabAdd.setOnClickListener(view -> showAddCompteDialog());
    }

    private void showAddCompteDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_account, null);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setPositiveButton("Ajouter", (dialogInterface, which) -> {
                    TextInputEditText etSolde = dialogView.findViewById(R.id.etSolde);
                    RadioButton radioCourant = dialogView.findViewById(R.id.radioCourant);

                    double solde = Double.parseDouble(etSolde.getText().toString());
                    TypeCompte type = radioCourant.isChecked() ? TypeCompte.COURANT : TypeCompte.EPARGNE;

                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    executorService.execute(() -> {
                        soapService.createCompte(solde, type);

                        runOnUiThread(() -> loadComptes());
                    });
                })
                .setNegativeButton("Annuler", null) // Pas de personnalisation pour le bouton Annuler
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        }

        dialog.show();
        // Suppression des personnalisations de couleur et de fond des boutons
    }

    private void loadComptes() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                List<Compte> comptes = soapService.getComptes();
                runOnUiThread(() -> {
                    if (comptes != null && !comptes.isEmpty()) {
                        adapter.updateComptes(comptes);
                    } else {
                        Toast.makeText(MainActivity.this, "Aucun compte trouvé", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}