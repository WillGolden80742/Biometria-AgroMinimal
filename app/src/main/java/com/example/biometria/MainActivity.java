package com.example.biometria;

import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import Model.bean.Encrypt;
import java.util.concurrent.Executor;
import ConnectionFactory.Server;
import util.Communication;


public class MainActivity extends AppCompatActivity {
    private TextView authlabel;
    private Button okButton;
    private EditText codeAuth;
    private EditText iPEditTex;
    private EditText portEditText;
    private Communication communic;
    Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        initInstances();
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                authlabel.setText("Hardware identificado!");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                authlabel.setText("Sem Hardware");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                authlabel.setText("Sem Hardware disponível");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                authlabel.setText("Sem Hardware configurado");
                break;
        }

        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt biometricPrompt = new BiometricPrompt(MainActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                int port = Integer.parseInt(String.valueOf(portEditText.getText()));
                server = new Server(iPEditTex.getText().toString(), port);
                String encryptedDeviceID = new Encrypt(getDeviceID()).getHashMd5();
                communic = new Communication("BIOMETRIC");
                communic.setParam("ANDROIDID",encryptedDeviceID);
                communic.setParam("CODE",String.valueOf(codeAuth.getText()));
                if (codeAuth.getText().length() == 0) {
                    authlabel.setText("Digite um código");
                }  else {
                    try {
                        sendID();
                    } catch (Exception ex) {
                        authlabel.setText("Erro : \n" + ex);
                    }
                }
            }


            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autentique-se")
                .setDescription("Use seu dedo para autenticar-se")
                .setNegativeButtonText("Cancelar")
                .build();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                biometricPrompt.authenticate(promptInfo);
            }
        });

    }

    private void sendID () {
        Thread netWork = new Thread(new Runnable() {
            @Override
            public void run() {
                server.outPut(communic);
                authlabel.setText((String) server.inPut().getParam("BIOMETRICREPLY"));
                server.close();
            }
        });
        netWork.start();
    }

    public void initInstances () {
        iPEditTex = findViewById(R.id.ipEditText);
        okButton = findViewById(R.id.okButton);
        authlabel = findViewById(R.id.authLabel);
        codeAuth = findViewById(R.id.codeAuth);
        portEditText = findViewById(R.id.portEditText);
    }

    public String getDeviceID() {
        return Settings.System.getString(this.getContentResolver(),Secure.ANDROID_ID);
    }

}
