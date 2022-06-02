package dam2.islasfelipinas.ut3_korrichmalik_whatip;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private TextView tv ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tvIPAddress);
    }

    /** El metodo checkConnection se comprueba el movil esta connectado a internet mediante wifi o con datos  creando
     * un objeto de la clase ConnectivityManager llamando al metodo  getSystemService() que devuelve un objeto  ConnectivityManager
     * es necesario añadir  android.permission.INTERNET y  android.permission.ACCESS_NETWORK_STATE que lo pide metodo getActiveNetworkInfo
     * creamos un objeto de  NetworkInfo  usando metodo getActiveNetworkInfo() que devlueve un objeto de tipo NetworkInfo
     * luego se comprueba llamando al metodo  isConnected() que es un metodo booleano
     * @return boolean
     */
    public boolean checkConnection(){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        /**
         * getActiveNetworkInfo() necesita el permiso ACCESS_NETWORK_STATE
         */
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            connected = true;
        }
        return connected;
    }

    /**
     * el metodo getIPAddress response al evento click del button getMyIpAddress
     * en este metodo se ejecuta  el la clase AsyncTask si el movil tiene acceso a red
     * si no se meustra un alert dialog
     *
     */
    public void getIPAddress(View view) {
        MyTask myTask = new MyTask();
        if (checkConnection()) {
            myTask.execute();
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Connection Error");
            alert.setMessage("No internet connection . Please turn on your  data or wifi.");
            alert.setPositiveButton("Ok", null);
            alert.create().show();
        }
    }


    /**
     *   la clase MyTask se extiende de la clase AsyncTask  en el metodo doInBackGround que se va ejecutar en segundo plano o en el worker thread
     *   se crea un objeto Document de la libreria Jsoup   usando el metodo connect(url) de la clase Jsoup y llamando al metodo get se va establecer la conexion con el url
     *   y se descarga la pagina html y se guarda en el objeto Document
     *   luego se crea un objeto de la clase Elements para hacer busqueda por etiquetas o attributos en este ejemplo se busca por attributo id porque es un identificador unico
     *   guardamos el valor el un variable String llamando al metodo .text() del la clase Eelements
     *   y hace return a la variable para que luego se recupera y se muestra en el textview  en metodo onPostExecute() porque se ejecuta en el ui thread
     *
     */
    public class MyTask extends AsyncTask<Void,Void,String>{

        /**
         *
         * se ejecuta antes del doinbackground()
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * inicia el proceso en segundo plano
         */
        @Override
        protected String doInBackground(Void... voids) {
            String  result = new String();
            String ip;
            /**
             * la pagina html se representa en tipo document con Jsoup
             */
            Document document = null;

            try {
                /**
                 * aqui se connecta a url y se recupera la pagina html
                 */
                document = Jsoup.connect("https://www.myip.com/").get();
                /**
                 *aqui se hace busqueda por  id en este caso <span id="ip">62.117.198.148</span>
                 */
                Elements tag = document.select("#ip");

                ip =  tag.text();

                result= "Your IP address is: "+ip;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        /**
         *  se ejecuta despues del doinbackground()
         */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            tv.setVisibility(View.VISIBLE);
            tv.setText(s);
        }


    }

}