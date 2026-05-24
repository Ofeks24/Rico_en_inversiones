package tools;



import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NewsTemplateLoader {

    /**
     * Carga las templates desde data/news_templates.json.
     * El archivo se busca primero como recurso del classpath
     * y si no, como ruta relativa al directorio de trabajo.
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