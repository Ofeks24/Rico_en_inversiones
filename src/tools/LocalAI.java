package tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LocalAI {

    public static String generarNoticia(String prompt) {

        try {

            ProcessBuilder pb = new ProcessBuilder(
                    "ai/client/llama-cli.exe",
                    "-m", "ai/models/tinyllama.gguf",
                    "-p", prompt,
                    "-n", "200",
                    "-t", "8",
                    "--temp", "0.95"
            );

            pb.redirectErrorStream(true);

            Process process = pb.start();

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            StringBuilder output = new StringBuilder();

            String line;

            while ((line = br.readLine()) != null) {
                output.append(line).append("\n");
            }

            process.waitFor();

            return output.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "No se pudo generar la noticia.";
    }
}