package es.upm.miw.SolitarioCelta;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SavedGames extends Activity {

    private ListView lvPartidas;
    private String opcionElegida;
    private static String BOARD_MODIFIED="boardModified";
    private boolean boardModified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_games);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        boardModified = extras.getBoolean(BOARD_MODIFIED);

        lvPartidas = (ListView)findViewById(R.id.lvPartidas);
        String [] games = readGames();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                games
        );
        lvPartidas.setAdapter(adapter);

        lvPartidas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                opcionElegida = lvPartidas.getItemAtPosition(position).toString();
                if (boardModified){
                    showDialogDeletePreviousGame();
                }else{
                    showDialogLoadGame();
                }
            }
        });
    }

    private void showDialogDeletePreviousGame(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.sobreescribirDialogTitle)
                .setMessage(R.string.sobreescribirDialog)
                .setPositiveButton(R.string.sobreescribirDialogOK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra("board", readBoardFromFile());
                        setResult(RESULT_OK, intent);
                        finish();
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

    private void showDialogLoadGame(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.txtCargarPartidaDialogTitle)
                .setMessage(R.string.txtCargarPartidaDialog)
                .setPositiveButton(R.string.sobreescribirDialogOK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra("board", readBoardFromFile());
                        setResult(RESULT_OK, intent);
                        finish();
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

    private String [] readGames(){
        ArrayList<String> result = new ArrayList<String>();

        try{
            BufferedReader bReader;
            bReader = new BufferedReader(new InputStreamReader(openFileInput(getFileName())));
            String line, gameName  = null;
            line = bReader.readLine();
            while (line != null){
                gameName = line.split("\\|")[0];
                result.add(gameName);
                line = bReader.readLine();
            }
        }catch (Exception e){
            Log.e("MiW", e.getMessage());
        }
        
        return result.toArray(new String[0]);
    }

    private String getFileName(){
        return "games.txt";
    }

    private String readBoardFromFile(){
        try{

            BufferedReader bReader;
            bReader = new BufferedReader(new InputStreamReader(
                    openFileInput(getFileName())
            ));

            String line = bReader.readLine();
            while(line != null && !opcionElegida.equals(line.split("\\|")[0])  ){
                line = bReader.readLine();
            }

            return line.split("\\|")[1];
        }catch (Exception e){
            Log.e("MIW", e.getMessage());
            return null;
        }
    }
}
