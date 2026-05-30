package tools;



import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Cargador de plantillas de noticias desde un fichero JSON externo.
 * <p>
 * Lee el archivo {@code news_templates.json} y construye la lista de
 * {@link NewsTemplate} que el {@link NewsGenerator} usará para generar
 * eventos de mercado durante la partida. El archivo se busca primero
 * como recurso del classpath ({@code /main/resources/files/news_templates.json})
 * y, si no se encuentra, como ruta relativa al directorio de trabajo.
 * </p>
 *
 * <p>Formato esperado del JSON:</p>
 * <pre>{@code
 * {
 *   "templates": [
 *     {
 *       "titulo":     "Titular con {empresa} y {pct}",
 *       "cuerpo":     "Cuerpo con {marca} y {sector}",
 *       "sector":     "NAVAL",
 *       "impactoMin": -0.10,
 *       "impactoMax":  0.10
 *     }
 *   ]
 * }
 * }</pre>
 */
public class NewsTemplateLoader {

    /**
     * Carga y devuelve todas las plantillas de noticias definidas en el JSON.
     * <p>
     * Si ocurre cualquier error de lectura o parseo, se imprime la traza
     * en {@code System.err} y se devuelve una lista vacía en lugar de
     * propagar la excepción.
     * </p>
     *
     * @return lista de {@link NewsTemplate} cargadas; vacía si hubo un error
     */
    public static List<NewsTemplate> load() {

        List<NewsTemplate> templates = new ArrayList<>();

        try {
            // Intentar cargar desde classpath (dentro del jar/resources)
        	InputStream is = NewsTemplateLoader.class.getResourceAsStream("/main/resources/files/news_templates.json");

            // Si no está en classpath, cargar desde disco
            if (is == null) {
                File file = new File(
                        System.getProperty("user.dir")
                        + "/main/resources/files/news_templates.json"
                );
                is = new java.io.FileInputStream(file);
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(is);
            JsonNode array = root.get("templates");

            for (JsonNode node : array) {

                String titulo     = node.get("titulo").asText();
                String cuerpo     = node.get("cuerpo").asText();
                Sector sector     = Sector.valueOf(
                                        node.get("sector").asText()
                                    );
                double impactoMin = node.get("impactoMin").asDouble();
                double impactoMax = node.get("impactoMax").asDouble();

                templates.add(new NewsTemplate(
                        titulo, cuerpo, sector,
                        impactoMin, impactoMax
                ));
            }

            is.close();

        } catch (Exception e) {
            System.err.println("[NewsTemplateLoader] "
                    + "Error cargando templates: " + e.getMessage());
            e.printStackTrace();
        }

        return templates;
    }
}