package dk.lyngby;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Server {

    public static void main(String[] args) throws Exception {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = getHTML("https://api.dataforsyningen.dk/postnumre/2800");
        Postnummer postnummer = gson.fromJson(jsonString, Postnummer.class);
        System.out.println(postnummer);

    }

    public static String getHTML(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        InputStreamReader isr = new InputStreamReader(conn.getInputStream());
        try( var reader = new BufferedReader(isr)) {
            for (String line; (line = reader.readLine()) != null; ) {
                result.append(line);
            }
        }
        return result.toString();
    }

    static class Postnummer {
        String nr;
        String navn;
        private List<Kommuner> kommuner;

        public Postnummer(String nr, String navn, List<Kommuner> kommuner) {
            this.nr = nr;
            this.navn = navn;
            this.kommuner = kommuner;
        }

        @Override
        public String toString() {
            return "Postnummer{" +
                    "nr='" + nr + '\'' +
                    ", navn='" + navn + '\'' +
                    ", kommuner=" + kommuner +
                    '}';
        }
    }

    static class Kommuner {
        String href;
        String kode;
        String navn;

        public Kommuner(String href, String kode, String navn) {
            this.href = href;
            this.kode = kode;
            this.navn = navn;
        }

        @Override
        public String toString() {
            return "Kommuner{" +
                    "href='" + href + '\'' +
                    ", kode='" + kode + '\'' +
                    ", navn='" + navn + '\'' +
                    '}';
        }
    }
}
