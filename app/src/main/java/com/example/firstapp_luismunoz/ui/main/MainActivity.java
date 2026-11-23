package com.example.firstapp_luismunoz.ui.main;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapp_luismunoz.R;
import com.example.firstapp_luismunoz.model.Item;
import com.example.firstapp_luismunoz.ui.ViewModelFactory;
import com.example.firstapp_luismunoz.ui.login.LoginActivity;
import com.example.firstapp_luismunoz.utils.AlertSettings;
import com.example.firstapp_luismunoz.utils.Event;
import com.example.firstapp_luismunoz.utils.SmsNotifier;
import com.example.firstapp_luismunoz.utils.Validator;

/**
 * View only: renders ViewModel state and forwards user input. No database code lives here.
 * Android-only work that a ViewModel must not touch (Toasts, dialogs, the SMS permission and the
 * SMS result broadcast) stays in this class.
 */
public class MainActivity extends AppCompatActivity implements ItemAdapter.Listener {

    private MainViewModel viewModel;
    private AlertSettings alertSettings;
    private EditText nameInput;
    private EditText quantityInput;
    private Button addButton;

    /** SMS text waiting for the user to grant SEND_SMS or to enter an alert phone number. */
    private String pendingSmsMessage;

    private final ActivityResultLauncher<String> smsPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted && pendingSmsMessage != null) {
                    sendSms(pendingSmsMessage);
                } else {
                    toast(getString(R.string.sms_disabled));
                }
                pendingSmsMessage = null;
            });

    /** Receives the "sent" result from SmsNotifier, so the app reports what really happened. */
    private final BroadcastReceiver smsSentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            toast(getString(getResultCode() == Activity.RESULT_OK ? R.string.sms_sent : R.string.sms_failed));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this, new ViewModelFactory(getApplication()))
                .get(MainViewModel.class);
        alertSettings = new AlertSettings(this);

        nameInput = findViewById(R.id.input_item_name);
        quantityInput = findViewById(R.id.input_item_quantity);
        addButton = findViewById(R.id.button_add_item);

        ItemAdapter adapter = new ItemAdapter(this);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(v ->
                viewModel.addItem(nameInput.getText().toString(), quantityInput.getText().toString()));

        viewModel.getItems().observe(this, adapter::submitList);
        viewModel.isBusy().observe(this, busy -> addButton.setEnabled(!busy));
        viewModel.getMessages().observe(this, this::showIfPresent);
        viewModel.getErrors().observe(this, this::showIfPresent);
        viewModel.getItemAdded().observe(this, event -> {
            if (event.getContentIfNotHandled() != null) clearInputs();
        });
        viewModel.getLowStockAlerts().observe(this, event -> {
            String smsText = event.getContentIfNotHandled();
            if (smsText != null) sendLowStockAlert(smsText);
        });

        // Not exported: only this app's own PendingIntent can deliver the result.
        ContextCompat.registerReceiver(this, smsSentReceiver,
                new IntentFilter(SmsNotifier.ACTION_SMS_SENT), ContextCompat.RECEIVER_NOT_EXPORTED);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(smsSentReceiver);
        super.onDestroy();
    }

    // --- Menu: alert phone number and logout ---

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_alert_phone) {
            showAlertPhoneDialog();
            return true;
        }
        if (item.getItemId() == R.id.action_logout) {
            logOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Returns to the login screen and clears the back stack, so Back cannot reopen the inventory. */
    private void logOut() {
        Intent intent = new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showAlertPhoneDialog() {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        input.setHint(R.string.hint_alert_phone);
        String current = alertSettings.getPhoneNumber();
        if (current != null) input.setText(current);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.menu_alert_phone)
                .setMessage(R.string.alert_phone_explanation)
                .setView(input)
                .setPositiveButton(R.string.save, null) // set below so an invalid number keeps the dialog open
                .setNegativeButton(android.R.string.cancel, (d, w) -> pendingSmsMessage = null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String error = Validator.validatePhoneNumber(input.getText().toString());
            if (error != null) {
                input.setError(error);
                return;
            }
            alertSettings.setPhoneNumber(input.getText().toString());
            toast(getString(R.string.alert_phone_saved));
            dialog.dismiss();
            if (pendingSmsMessage != null) {
                String message = pendingSmsMessage;
                pendingSmsMessage = null;
                sendLowStockAlert(message);
            }
        }));
        dialog.show();
    }

    // --- Item callbacks from the adapter ---

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
        if (message != null) toast(message);
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // --- SMS: ask for a number, then permission, then send ---

    private void sendLowStockAlert(String message) {
        if (alertSettings.getPhoneNumber() == null) {
            pendingSmsMessage = message;
            toast(getString(R.string.alert_phone_needed));
            showAlertPhoneDialog();
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            sendSms(message);
        } else {
            pendingSmsMessage = message;
            smsPermissionLauncher.launch(Manifest.permission.SEND_SMS);
        }
    }

    private void sendSms(String message) {
        // On success the final "sent" or "failed" message comes from smsSentReceiver.
        if (!SmsNotifier.send(this, alertSettings.getPhoneNumber(), message)) {
            toast(getString(R.string.sms_failed));
        }
    }
}
