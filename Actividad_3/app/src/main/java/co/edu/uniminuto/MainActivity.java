package co.edu.uniminuto;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CAMERA = 24;
    private static final int REQUEST_CODE_STORAGE = 25;
    private static final int REQUEST_CODE_LOCATION = 26;
    private static final int REQUEST_CODE_GALLERY = 27;
    private static final int REQUEST_CODE_CONTACTS = 28;

    // Declaración de los Objetos
    private Button btnCheckPermissions;
    private Button btnRequestPermissions;
    private TextView tvCamera;
    private TextView tvBiometric;
    private TextView tvExternalWS;
    private TextView tvReadExternalS;
    private TextView tvInternet;
    private TextView tvGPS;
    private TextView tvContactos;
    private TextView versionAndroid;
    private int versionSDK;
    private ProgressBar pbLevelBatt;
    private TextView tvLevelBatt;
    private TextView tvConexion;
    ConnectivityManager conexion;
    IntentFilter batFilter;
    CameraManager cameraManager;
    String cameraId;
    private Button btnOn;
    private Button btnOff;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initObject();

        //Botones a Metodo
        btnCheckPermissions.setOnClickListener(this::voidCheckPermissions);
        btnRequestPermissions.setOnClickListener(this::voidRequestPermissions);

        //Botones para la linterna
        btnOn.setOnClickListener(this::OnLigth);
        btnOff.setOnClickListener(this::OffLigth);

        //Bateria
        batFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, batFilter);
    }

    //1. Versión de Android
    @Override
    protected void onResume() {
        super.onResume();
        String versionSO = Build.VERSION.RELEASE;
        versionSDK = Build.VERSION.SDK_INT;
        versionAndroid.setText("Version SO" + versionSO + "/ SDK" + versionSDK);
    }

    //2.Linterna
    private void OnLigth(View view) {
        try {
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
        } catch (Exception e) {
            Toast.makeText(this, "No se puede encender la linterna", Toast.LENGTH_SHORT).show();
            Log.i("FLASH", e.getMessage());
        }
    }

    private void OffLigth(View view) {
        try {
            cameraManager.setTorchMode(cameraId, false);
        } catch (Exception e) {
            Toast.makeText(this, "No se puede apagar la linterna", Toast.LENGTH_SHORT).show();
            Log.i("FLASH", e.getMessage());
        }
    }

    //3.Bateria
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int levelBaterry = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            pbLevelBatt.setProgress(levelBaterry);
            tvLevelBatt.setText("Level Baterry" + levelBaterry + "%");
        }
    };

    //5. Verificación de permisos
    private void voidRequestPermissions(View view) {
        // Permisos para acceso a la cámara
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
        }

        // Permisos para la escritura en el almacenamiento
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
        }

        // Permisos para el uso del GPS del sistema
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
        }

        // Permisos para el uso de la galeria
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
        }

        // Permisos para acceder a los contactos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_CONTACTS);
        }
    }

    //Permisos
    private void voidCheckPermissions(View view) {
        //Validación del permiso si hay permiso devuelbe 0 si no 1
        int statusCamera = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        int statusWES = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int statusRES = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int statusInternet = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET);
        int statusBiometric = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.USE_BIOMETRIC);
        int statusGPS = ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION);
        int statusContactos = ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_CONTACTS);

        //Presentación en interface
        tvCamera.setText("Satatus Camera:" + statusCamera);
        tvExternalWS.setText("Satatus WES:" + statusWES);  //Falta solicitar permisos
        tvReadExternalS.setText("Satatus RES:" + statusRES);
        tvInternet.setText("Satatus Internet:" + statusInternet);
        tvBiometric.setText("Satatus Internet:" + statusBiometric);
        tvGPS.setText("Status GPS: "+statusGPS);
        tvContactos.setText("Status Contactos: "+statusContactos);
        btnRequestPermissions.setEnabled(true);

    }

    // Método para manejar la respuesta de la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de cámara concedido
                Toast.makeText(this, "Permiso de cámara concedido", Toast.LENGTH_SHORT).show();
            } else {
                // Permiso de cámara denegado
                showPermissionDeniedDialog("camera");
            }
        } else if (requestCode == REQUEST_CODE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de escritura en almacenamiento concedido
                Toast.makeText(this, "Permiso de escritura en almacenamiento concedido", Toast.LENGTH_SHORT).show();
            } else {
                // Permiso de escritura en almacenamiento denegado
                showPermissionDeniedDialog("storage writing");
            }
        } else if (requestCode == REQUEST_CODE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de ubicación concedido
                Toast.makeText(this, "Permiso de ubicación concedido", Toast.LENGTH_SHORT).show();
            } else {
                // Permiso de ubicación denegado
                showPermissionDeniedDialog("location");
            }
        } else if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de acceso a la galería concedido
                Toast.makeText(this, "Permiso de acceso a la galería concedido", Toast.LENGTH_SHORT).show();
            } else {
                // Permiso de acceso a la galería denegado
                showPermissionDeniedDialog("gallery");
            }
        }else if (requestCode == REQUEST_CODE_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de acceso a los contactos concedido
                Toast.makeText(this, "Permiso de acceso a los contactos concedido", Toast.LENGTH_SHORT).show();
            } else {
                // Permiso de acceso a los contactos denegado
                showPermissionDeniedDialog("contacts");
            }
        }
    }

    //Muestra de cuadro de dialogo informando al usuario que se ha denegado el permiso
    private void showPermissionDeniedDialog(String permissionName) {
        // Construye y muestra el cuadro de diálogo
        new AlertDialog.Builder(this)
                // Establece el título del cuadro de diálogo
                .setTitle("Box Permissions")
                // Establece el mensaje del cuadro de diálogo
                .setMessage("You denied the permissions for " + permissionName + ". This permission is necessary for certain features of the app.")
                // Configura el botón "Settings" para abrir la configuración de la aplicación
                .setPositiveButton("Settings", (dialog, which) -> {
                    // Crea un intent para abrir la configuración de la aplicación
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                    // Agrega la bandera FLAG_ACTIVITY_NEW_TASK al intent
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    // Inicia la actividad de configuración de la aplicación
                    startActivity(intent);
                    // Finaliza la actividad actual
                    finish();
                })
                // Configura el botón "Exit" para cerrar la aplicación
                .setNegativeButton("Exit", (dialog, which) -> {
                    // Descarta el cuadro de diálogo
                    dialog.dismiss();
                    // Finaliza la actividad actual
                    finish();
                })
                // Crea y muestra el cuadro de diálogo
                .create()
                .show();
    }

    //Enlace de objetos
    private void initObject(){
        btnCheckPermissions = findViewById(R.id.btnChekPermission);
        btnRequestPermissions = findViewById(R.id.btnRequestPermission);
        btnRequestPermissions.setEnabled(false);

        //Mapeo de Objetos
        tvCamera = findViewById(R.id.tvCamera);
        tvBiometric = findViewById(R.id.tvDactilar );
        tvExternalWS = findViewById(R.id.tvEws);
        tvReadExternalS = findViewById(R.id.tvRs);
        tvInternet = findViewById(R.id.tvInternet);


        //tvGPS = findViewById(R.id.tvGPS);
        tvGPS = findViewById(R.id.tvGPS);
        tvContactos = findViewById(R.id.tvContactos);

        versionAndroid = findViewById(R.id.tvVersionAndroid);
        pbLevelBatt = findViewById(R.id.pbLevelBaterry);
        tvLevelBatt = findViewById(R.id.tvLevelBaterry);
        tvConexion = findViewById(R.id.tvConexion);
        btnOn = findViewById(R.id.btnOn);
        btnOff = findViewById(R.id.btnOff);

    }

}

