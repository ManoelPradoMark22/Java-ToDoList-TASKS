package todolist.cursoandroid.com.todolist;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private EditText textoTarefa;
    private Button botaoAdicionar;
    private ListView listaTarefas;
    private SQLiteDatabase bancoDados;
    private ArrayAdapter<String> itensAdaptador;
    private ArrayList<String> itens;
    private ArrayList<Integer> ids;
    private AlertDialog.Builder dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            textoTarefa = (EditText) findViewById(R.id.textoId);
            botaoAdicionar = (Button) findViewById(R.id.botaoAdicionarId);
            listaTarefas = (ListView) findViewById(R.id.listViewId);

            //criar banco de dados
            bancoDados = openOrCreateDatabase("appTarefas", MODE_PRIVATE, null);

            //criar a tabela
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS tabelaTarefas(id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR)");
            recuperarTarefas();
            botaoAdicionar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String textoDigitado = textoTarefa.getText().toString();
                    salvarTarefa(textoDigitado);
                }
            });

            listaTarefas.setLongClickable(true);
            listaTarefas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("ATENÇÃO!");
                    dialog.setMessage("Deseja excluir a anotação?");
                    //qnd clicar fora da caixa nao faz nada
                    dialog.setCancelable(true);
                    dialog.setIcon(android.R.drawable.ic_delete);

                    dialog.setNegativeButton("Não",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    dialog.setPositiveButton("Sim",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int numero = position;
                                    //Log.i("ITEM: ", position + "/" + ids.get(position));
                                    removerTarefa(ids.get(numero));
                                }
                            });
                    dialog.create();
                    dialog.show();
                    return true;
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void salvarTarefa (String texto){
        try{
            if (texto.equals("")){
                Toast.makeText(MainActivity.this,"Digite uma tarefa!", Toast.LENGTH_SHORT).show();
            }else {
                //bancoDados.execSQL("INSERT INTO tabelaTarefas (tarefa) VALUES ('teste')");
                //('" + textoDigitado + "') pra colocar uma variavel
                bancoDados.execSQL("INSERT INTO tabelaTarefas (tarefa) VALUES ('" + texto + "')");
                Toast.makeText(MainActivity.this,"Tarefa salva com sucesso!", Toast.LENGTH_SHORT).show();
                //recupera na hora e mostra a tarefa no list view (se nao fizer isso so mostra depois q abrir o app de novo)
                recuperarTarefas();
                //pra caixa de texto ficar em branco (se nao fizer isso, qnd abrir o app vai ficar na caixa de texto o texto digitado antes)
                textoTarefa.setText("");
            }

            //recuperar as tarefas
            recuperarTarefas();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void recuperarTarefas(){
        try{

            //recupera as tarefas
            //ORDENAR POR ID DECRESCENTE (mostra primeiro os ultimos registrados)
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tabelaTarefas ORDER BY id DESC", null);

            //recupera os ids das colunas
            int indiceColunaId = cursor.getColumnIndex("id");
            int indiceColunaTarefa = cursor.getColumnIndex("tarefa");

            //criar o adaptador
            //ATENÇÃO nas instancias! nao esquecer!
            itens = new ArrayList<String>();
            ids = new ArrayList<Integer>();
            itensAdaptador = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_2,
                    android.R.id.text1,
                    itens
                    );
            listaTarefas.setAdapter(itensAdaptador);

            //listar as tarefas
            cursor.moveToFirst();
            while (cursor!=null){

                //Log.i("RESULTADO - ","Id Tarefa: " +cursor.getString(indiceColunaId) + ". Tarefa: " +cursor.getString(indiceColunaTarefa));
                //esse metodo add() adiciona elementos de strings dentro do array list
                itens.add(cursor.getString(indiceColunaTarefa));
                ids.add(Integer.parseInt(cursor.getString(indiceColunaId)));
                cursor.moveToNext();
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void removerTarefa(Integer id){

        try {

            bancoDados.execSQL("DELETE FROM tabelaTarefas WHERE id="+id);
            Toast.makeText(MainActivity.this,"Tarefa removida com sucesso!", Toast.LENGTH_SHORT).show();
            recuperarTarefas();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
