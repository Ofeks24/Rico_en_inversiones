package tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.Timer;

public class LocalAI {

    public static String generarNoticia(String prompt) {

        try {
        	
            ProcessBuilder pb = new ProcessBuilder(
                    "ai/llama-cli.exe",
                    "-m", "ai/models/qwen.gguf",
                    "-p", prompt,
                    "-n", "200",
                    "-t", "8",
                    "--temp", "0.95",
                    "--no-display-prompt"
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
            
            return limpiarSalida(output.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "No se pudo generar la noticia.";
    }

    // ============================================
    // LIMPIAR TEXTO GENERADO
    // ============================================

    private static String limpiarSalida(String texto) {

        texto = texto.replaceAll("(?s)^.*?\\n\\n", "");

        return texto.trim();
    }
}