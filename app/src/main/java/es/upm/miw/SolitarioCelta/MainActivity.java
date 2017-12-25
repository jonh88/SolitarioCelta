package es.upm.miw.SolitarioCelta;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity {

    JuegoCelta juego;
    private final String GRID_KEY = "GRID_KEY";
    private int numFichasInit;
    private boolean boardModified;
    private TextView txtNumFichas;
    private Chronometer crono;
    private String timePlayed;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        juego = new JuegoCelta();
        mostrarTablero();
        crono = (Chronometer) findViewById(R.id.chronometer);
        txtNumFichas = (TextView)findViewById(R.id.txtNumFichas);
        txtNumFichas.setText(String.valueOf(juego.numeroFichas()));
    }

    public void onResume() {
        super.onResume();
        numFichasInit = juego.numeroFichas();
        boardModified = false;
        initTimer();
    }

    private void initTimer(){
        crono.setBase(SystemClock.elapsedRealtime());
        crono.start();
    }

    private void stopTimer(){
        crono.stop();
        timePlayed = crono.getText().toString();
    }

    /**
     * Se ejecuta al pulsar una ficha
     * Las coordenadas (i, j) se obtienen a partir del nombre, ya que el botón
     * tiene un identificador en formato pXY, donde X es la fila e Y la columna
     *
     * @param v Vista de la ficha pulsada
     */
    public void fichaPulsada(View v) {
        String resourceName = getResources().getResourceEntryName(v.getId());
        int i = resourceName.charAt(1) - '0';   // fila
        int j = resourceName.charAt(2) - '0';   // columna

        juego.jugar(i, j);

        if (this.numFichasInit > juego.numeroFichas()) {
            this.boardModified = true;
        }

        mostrarTablero();
        txtNumFichas.setText(String.valueOf(juego.numeroFichas()));
        if (juego.juegoTerminado()) {
            stopTimer();
            saveResult();
            new AlertDialogFragment().show(getFragmentManager(), "ALERT_DIALOG");
        }
    }

    public void mostrarTablero() {
        RadioButton button;
        String strRId;
        String prefijoIdentificador = getPackageName() + ":id/p"; // formato: package:type/entry
        int idBoton;

        for (int i = 0; i < JuegoCelta.TAMANIO; i++)
            for (int j = 0; j < JuegoCelta.TAMANIO; j++) {
                strRId = prefijoIdentificador + Integer.toString(i) + Integer.toString(j);
                idBoton = getResources().getIdentifier(strRId, null, null);
                if (idBoton != 0) { // existe el recurso identificador del botón
                    button = (RadioButton) findViewById(idBoton);
                    button.setChecked(juego.obtenerFicha(i, j) == JuegoCelta.FICHA);
                }
            }
    }

    /**
     * Guarda el estado del tablero (serializado)
     *
     * @param outState Bundle para almacenar el estado del juego
     */
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(GRID_KEY, juego.serializaTablero());
        super.onSaveInstanceState(outState);
    }

    /**
     * Recupera el estado del juego
     *
     * @param savedInstanceState Bundle con el estado del juego almacenado
     */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String grid = savedInstanceState.getString(GRID_KEY);
        juego.deserializaTablero(grid);
        mostrarTablero();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.opciones_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.opcAjustes:
                startActivity(new Intent(this, SCeltaPrefs.class));
                return true;
            case R.id.opcAcercaDe:
                startActivity(new Intent(this, AcercaDe.class));
                return true;
            case R.id.opcReiniciarPartida:
                AlertDialogFragment dialogFragment = new AlertDialogFragment();
                dialogFragment.show(getFragmentManager(), "reset");
                return true;
            case R.id.opcGuardarPartida:
                showSaveDialog();
                return true;
            case R.id.opcRecuperarPartida:
                Intent goToSavedGames = new Intent(this, SavedGames.class);
                goToSavedGames.putExtra("boardModified", this.boardModified);
                goToSavedGames.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivityForResult(goToSavedGames, 1);
                return true;
            case R.id.opcMejoresResultados:
                Intent goToBestScores = new Intent(this, BestScores.class);
                goToBestScores.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(goToBestScores);
                return true;
            default:
                Toast.makeText(
                        this,
                        getString(R.string.txtSinImplementar),
                        Toast.LENGTH_SHORT
                ).show();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {
                String board = data.getExtras().getString("board");
                this.juego.deserializaTablero(board);

            }
            mostrarTablero();
        }
    }

    private void showSaveDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.txtInputDialogTitle);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        builder.setView(input)
                .setPositiveButton(R.string.txtInputDialogOK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveGame(input.getText().toString());
                    }
                })
                .setNegativeButton(R.string.txtInputDialogCancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        builder.show();
    }

    private void saveGame(String savedGameName) {

        String savedGame = savedGameName + "|" + this.juego.serializaTablero() + "\n";
        String filename = "games.txt";
        FileOutputStream outputStream;
        try {
            //MODE_PRIVATE -> ELIMINA CONTENIDO ANTERIOR VS MODE_APPEND
            outputStream = openFileOutput(filename, Context.MODE_APPEND);
            outputStream.write(savedGame.getBytes());
            outputStream.close();
            this.boardModified = false;
            Toast.makeText(getApplicationContext(), R.string.txtSavedGame, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("MIW", e.getMessage());
        }
    }

    private void saveResult() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        StringBuilder stringBuilder = new StringBuilder(60);
        stringBuilder.append(sharedPreferences.getString("nombreJugador", "User default"));
        stringBuilder.append("|");
        stringBuilder.append(simpleDateFormat.format(new Date()));
        stringBuilder.append("|");
        stringBuilder.append(Integer.toString(this.juego.numeroFichas()));
        stringBuilder.append("|");
        stringBuilder.append(timePlayed);
        stringBuilder.append("\n");

        String filename = "bestScores.dat";
        FileOutputStream outputStream;
        try {
            //MODE_PRIVATE -> ELIMINA CONTENIDO ANTERIOR VS MODE_APPEND
            outputStream = openFileOutput(filename, Context.MODE_APPEND);
            outputStream.write(stringBuilder.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e("MIW", e.getMessage());
        }

    }

    public void setBoardModified(boolean state) {
        this.boardModified = state;
    }

}