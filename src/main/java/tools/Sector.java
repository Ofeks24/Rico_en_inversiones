package tools;

public enum Sector {
	NAVAL,        // Naviera & Astilleros Dorado
    INDUSTRIA,    // Talleres Mécanique Royale
    BOTANICA,     // Compañía Botánica del Atlante
    AGRICULTURA,  // Molinos Reunidos del Valle
    MANUFACTURA,  // Fábrica de Velas & Jabones Santa Águeda
    GLOBAL;
	
	public static Sector fromString(String value) {
        if (value == null || value.isBlank()) return GLOBAL;
        try {
            return Sector.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("[Sector] Valor desconocido en BD: '"
                    + value + "' → usando GLOBAL como fallback");
            return GLOBAL;
        }
    }
}

