package es.upm.miw.SolitarioCelta;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class BestScores extends Activity {

    private ListView lvResultados;
    private String [] scores;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_scores);

        lvResultados = (ListView)findViewById(R.id.lvResultados);

        this.scores = readScores();

        orderBestScores(scores);

        this.adapter = new ArrayAdapter<String>(
                getApplicationContext(),
                android.R.layout.simple_list_item_1,
                this.scores
                );

        lvResultados.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_borrar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.opcDeleteScores:
                showDeleteScoresDialog();
                return true;
            default:
                return false;
        }
    }

    private void showDeleteScoresDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.borrarScoresDialogTitle)
                .setMessage(R.string.borrarScoresDialog)
                .setPositiveButton(R.string.sobreescribirDialogOK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteScores();
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

    private void deleteScores(){
        try{
            FileOutputStream fileOutputStream = openFileOutput("bestScores.dat", Context.MODE_PRIVATE);
            fileOutputStream.close();
            lvResultados.setAdapter(null);
            Toast.makeText(getApplicationContext(),"Puntuaciones borradas",Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Log.e("MIW", e.getMessage());
        }
    }

    private String [] readScores(){

        ArrayList<String> result = new ArrayList<String>();

        try{
            BufferedReader bReader = new BufferedReader(new InputStreamReader(openFileInput("bestScores.dat")));

            String line = bReader.readLine();
            while (line!=null){
                result.add(line);
                line = bReader.readLine();
            }
            return result.toArray(new String[0]);
        }catch (Exception e){
            Log.e("MIW", e.getMessage());
            return null;
        }
    }

    private void orderBestScores(String [] scores){

        String score;
        for (int i = 0; i < scores.length-1; i++){
            for (int j=i+1; j < scores.length; j++){
                if (Integer.parseInt(scores[i].split("\\|")[2]) > Integer.parseInt(scores[j].split("\\|")[2])){
                    score = scores[i];
                    scores[i] = scores[j];
                    scores[j]= score;
                }
            }
        }
    }
}
