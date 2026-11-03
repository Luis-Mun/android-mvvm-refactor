package com.example.firstapp_luismunoz.ui.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapp_luismunoz.R;
import com.example.firstapp_luismunoz.model.Item;
import com.example.firstapp_luismunoz.ui.ViewModelFactory;
import com.example.firstapp_luismunoz.utils.AppConfig;
import com.example.firstapp_luismunoz.utils.Event;
import com.example.firstapp_luismunoz.utils.SmsNotifier;

/** View only: renders ViewModel state and forwards user input. No database code lives here. */
public class MainActivity extends AppCompatActivity implements ItemAdapter.Listener {

    private MainViewModel viewModel;
    private EditText nameInput;
    private EditText quantityInput;
    private Button addButton;

    /** SMS text waiting for the user to grant SEND_SMS. */
    private String pendingSmsMessage;

    private final ActivityResultLauncher<String> smsPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted && pendingSmsMessage != null) {
                    sendSms(pendingSmsMessage);
                } else {
                    Toast.makeText(this, R.string.sms_disabled, Toast.LENGTH_LONG).show();
                }
                pendingSmsMessage = null;
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication()))
                .get(MainViewModel.class);

        nameInput = findViewById(R.id.input_item_name);
        quantityInput = findViewById(R.id.input_item_quantity);
        addButton = findViewById(R.id.button_add_item);

        ItemAdapter adapter = new ItemAdapter(this);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(v -> {
            viewModel.addItem(nameInput.getText().toString(), quantityInput.getText().toString());
        });

        viewModel.getItems().observe(this, adapter::submitList);
        viewModel.isBusy().observe(this, busy -> addButton.setEnabled(!busy));
        viewModel.getMessages().observe(this, event -> {
            String message = event.getContentIfNotHandled();
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
        viewModel.getItemAdded().observe(this, event -> {
            if (event.getContentIfNotHandled() != null) clearInputs();
        });
        viewModel.getErrors().observe(this, event -> showIfPresent(event));
        viewModel.getLowStockAlerts().observe(this, event -> {
            String smsText = event.getContentIfNotHandled();
            if (smsText != null) sendLowStockAlert(smsText);
        });
    }

    @Override
    public void onIncrement(Item item) { viewModel.adjustQuantity(item, +1); }

    @Override
    public void onDecrement(Item item) { viewModel.adjustQuantity(item, -1); }

    @Override
    public void onDelete(Item item) { viewModel.deleteItem(item); }

    private void clearInputs() {
        nameInput.setText("");
        quantityInput.setText("");
    }

    private void showIfPresent(Event<String> event) {
        String message = event.getContentIfNotHandled();
        if (message != null) Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // --- SMS: platform/permission work belongs in the View layer ---

    private void sendLowStockAlert(String message) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            sendSms(message);
        } else {
            pendingSmsMessage = message;
            smsPermissionLauncher.launch(Manifest.permission.SEND_SMS);
        }
    }

    private void sendSms(String message) {
        boolean sent = SmsNotifier.send(this, AppConfig.ALERT_PHONE_NUMBER, message);
        Toast.makeText(this, sent ? R.string.sms_sent : R.string.sms_failed, Toast.LENGTH_SHORT).show();
    }
}
