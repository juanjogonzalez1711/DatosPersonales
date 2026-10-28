package com.example.datospersonales;

// Importaciones de librerías necesarias del sistema y AndroidX
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.os.LocaleListCompat;
import java.util.Locale;

// Actividad principal que hereda de AppCompatActivity
public class MainActivity extends AppCompatActivity {

    // Declaración de los componentes visuales de la interfaz
    private Spinner spinnerIdioma, spinnerEstadoCivil;
    private EditText etNombre, etApellidos, etEdad;
    private RadioGroup rgGenero;
    private SwitchCompat switchHijos;
    private TextView tvMensajes;
    private Button btnMostrar, btnReiniciar;

    // Identificador para el canal de notificaciones en Android 8.0+
    private static final String CHANNEL_ID = "canal_datos_personales";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Asigna la vista XML a esta actividad

        initViews();                     // Enlaza variables Java con los IDs de la vista XML
        configurarSpinners();            // Llena desplegables y gestiona el cambio dinámico de idioma
        crearCanalNotificacion();        // Crea el canal del sistema para notificaciones
        solicitarPermisoNotificaciones(); // Pide permisos en tiempo de ejecución (Android 13+)

        // Asignación de eventos de click mediante expresiones Lambda
        btnMostrar.setOnClickListener(v -> validarYMostrarInformacion());
        btnReiniciar.setOnClickListener(v -> limpiarFormulario());
    }

    // Mapea las variables de Java con los elementos definidos en activity_main.xml
    private void initViews() {
        spinnerIdioma = findViewById(R.id.spinnerIdioma);
        spinnerEstadoCivil = findViewById(R.id.spinnerEstadoCivil);
        etNombre = findViewById(R.id.etNombre);
        etApellidos = findViewById(R.id.etApellidos);
        etEdad = findViewById(R.id.etEdad);
        rgGenero = findViewById(R.id.rgGenero);
        switchHijos = findViewById(R.id.switchHijos);
        tvMensajes = findViewById(R.id.tvMensajes);
        btnMostrar = findViewById(R.id.btnMostrar);
        btnReiniciar = findViewById(R.id.btnReiniciar);
    }

    // Carga los valores de los desplegables desde strings.xml y maneja la internacionalización
    private void configurarSpinners() {
        // Carga de opciones para Estado Civil
        String[] estados = {
                getString(R.string.estado_soltero),
                getString(R.string.estado_casado),
                getString(R.string.estado_separado),
                getString(R.string.estado_visudo),
                getString(R.string.estado_otro)
        };
        ArrayAdapter<String> adapterEstado = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, estados);
        spinnerEstadoCivil.setAdapter(adapterEstado);

        // Cargar las opciones de idioma disponibles
        String[] idiomas = {
                getString(R.string.idioma_es),
                getString(R.string.idioma_en),
                getString(R.string.idioma_fr)
        };
        ArrayAdapter<String> adapterIdioma = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, idiomas);
        spinnerIdioma.setAdapter(adapterIdioma);

        // Obtiene el idioma actualmente activo en la app
        LocaleListCompat locales = AppCompatDelegate.getApplicationLocales();
        Locale primaryLocale = !locales.isEmpty() ? locales.get(0) : null;
        String currentLang = (primaryLocale != null) ? primaryLocale.getLanguage() : Locale.getDefault().getLanguage();

        // Selecciona la posición por defecto según el idioma del sistema
        int positionActual;
        if ("en".equalsIgnoreCase(currentLang)) {
            positionActual = 1;
        } else if ("fr".equalsIgnoreCase(currentLang)) {
            positionActual = 2;
        } else {
            positionActual = 0;
        }

        spinnerIdioma.setSelection(positionActual, false);
        final int finalPositionActual = positionActual;

        // Escucha el cambio de opción para aplicar el nuevo idioma
        spinnerIdioma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != finalPositionActual) {
                    switch (position) {
                        case 1:
                            cambiarIdioma("en");
                            break;
                        case 2:
                            cambiarIdioma("fr");
                            break;
                        case 0:
                        default:
                            cambiarIdioma("es");
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // Aplica el cambio de locale/idioma en tiempo de ejecución (requisito I+D+I)
    private void cambiarIdioma(String codigoIdioma) {
        LocaleListCompat appLocales = LocaleListCompat.forLanguageTags(codigoIdioma);
        AppCompatDelegate.setApplicationLocales(appLocales);
    }

    // Solicita el permiso POST_NOTIFICATIONS obligatorio para Android 13+ (API 33+)
    private void solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    // Comprueba el formulario y construye el mensaje para la notificación
    private void validarYMostrarInformacion() {
        String nombre = etNombre.getText().toString().trim();
        String apellidos = etApellidos.getText().toString().trim();
        String edadStr = etEdad.getText().toString().trim();

        // Muestra error en rojo si falta algún campo de texto
        if (nombre.isEmpty() || apellidos.isEmpty() || edadStr.isEmpty()) {
            tvMensajes.setText(R.string.error_faltan_campos);
            return;
        }

        tvMensajes.setText(""); // Limpia mensajes de error previos

        // Validación y parseo numérico de la edad
        int edad;
        try {
            edad = Integer.parseInt(edadStr);
        } catch (NumberFormatException e) {
            tvMensajes.setText(R.string.error_faltan_campos);
            return;
        }

        // Determina la condición de mayoría de edad
        String mayorEdadStr = (edad >= 18) ? getString(R.string.notif_mayor_edad) : getString(R.string.notif_menor_edad);

        // Evalúa el género seleccionado en el RadioGroup
        String generoStr = "";
        int selectedRadioId = rgGenero.getCheckedRadioButtonId();
        if (selectedRadioId == R.id.rbHombre) {
            generoStr = getString(R.string.notif_hombre);
        } else if (selectedRadioId == R.id.rbMujer) {
            generoStr = getString(R.string.notif_mujer);
        }

        // Obtiene estado civil e hijos seleccionados
        String estadoCivilStr = spinnerEstadoCivil.getSelectedItem() != null ? spinnerEstadoCivil.getSelectedItem().toString() : "";
        String hijosStr = switchHijos.isChecked() ? getString(R.string.notif_con_hijos) : getString(R.string.notif_sin_hijos);

        // Construcción de la cadena formateada final
        String textoNotificacion = apellidos + ", " + nombre + ", " + mayorEdadStr + ", " + generoStr + " " + estadoCivilStr.toLowerCase() + " " + hijosStr;

        // Dispara la notificación del sistema
        lanzarNotificacion(textoNotificacion);
    }

    // Muestra la notificación en la barra del sistema mediante NotificationCompat
    private void lanzarNotificacion(String mensaje) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(mensaje)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje)) // Soporta texto largo
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }

    // Registra el canal de notificación requerido por Android 8.0+
    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Canal Datos Personales";
            String description = "Canal de notificaciones";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    // Resetea todos los campos del formulario a sus valores por defecto
    private void limpiarFormulario() {
        etNombre.setText("");
        etApellidos.setText("");
        etEdad.setText("");
        rgGenero.clearCheck();
        spinnerEstadoCivil.setSelection(0);
        switchHijos.setChecked(false);
        tvMensajes.setText("");
    }
}