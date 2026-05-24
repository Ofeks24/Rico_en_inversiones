package tools;

import java.util.*;

public class MarketService {

    private final Map<Integer, List<Candle>> histories = new HashMap<>();
    private final Map<Integer, Double>       openDay   = new HashMap<>();
    private final List<Runnable>             listeners = new ArrayList<>();
    private final Random rng = new Random();

    // ── Inicializar empresa ───────────────────────────────────
    public void initCompany(int id, double precioBase) {
        List<Candle> hist = new ArrayList<>();
        double price = precioBase;
        for (int i = 0; i < 60; i++) {
            double drift = (rng.nextDouble() * 0.04) - 0.02;
            double close = Math.max(1, price * (1 + drift));
            double body  = Math.abs(close - price);
            double high  = Math.max(price, close) + rng.nextDouble() * body * 0.5;
            double low   = Math.min(price, close) - rng.nextDouble() * body * 0.5;
            hist.add(new Candle(price, high, low, close));
            price = close;
        }
        histories.put(id, hist);
        openDay.put(id, precioBase);
    }

    // ── Tick normal (llamar desde Clock) ─────────────────────
    public void tick(List<CompanyData> companies) {
        for (CompanyData c : companies) {
            double old   = c.getValorAccion();
            double drift = (rng.nextDouble() * 0.03) - 0.015;
            double nuevo = Math.max(1, old * (1 + drift));
            c.setValorAccion(nuevo);
            pushCandle(c.getId(), old, nuevo);
        }
        notify_();
    }

    // ── Aplicar noticia ───────────────────────────────────────
    public void applyNews(NewsEvent news,
                          List<CompanyData> companies) {
        for (CompanyData c : companies) {
            boolean afecta = news.getEmpresaId() == -1          // global
                    || news.getEmpresaId() == c.getId()          // empresa exacta
                    || c.getSector() == news.getSector();        // mismo sector

            if (!afecta) continue;

            // Las noticias sectoriales que no son de la empresa exacta
            // tienen impacto reducido al 40%
            double factor = (news.getEmpresaId() == c.getId()
                             || news.getEmpresaId() == -1)
                            ? 1.0 : 0.4;

            double old   = c.getValorAccion();
            double nuevo = Math.max(1,
                    old * (1 + news.getImpacto() * factor));
            c.setValorAccion(nuevo);
            pushCandle(c.getId(), old, nuevo);
        }
        notify_();
    }

    // ── Nuevo día: resetear apertura ─────────────────────────
    public void newDay(List<CompanyData> companies) {
        for (CompanyData c : companies)
            openDay.put(c.getId(), c.getValorAccion());
    }

    // ── Consultas ─────────────────────────────────────────────
    public List<Candle> getHistory(int id) {
        return histories.getOrDefault(id, Collections.emptyList());
    }

    public double getOpenDay(int id) {
        return openDay.getOrDefault(id, 1.0);
    }

    public void addListener(Runnable r) { listeners.add(r); }

    // ── Interno ───────────────────────────────────────────────
    private void pushCandle(int id, double open, double close) {
        List<Candle> hist =
                histories.computeIfAbsent(id, k -> new ArrayList<>());
        double body = Math.abs(close - open);
        double high = Math.max(open, close)
                      + rng.nextDouble() * body * 0.5;
        double low  = Math.min(open, close)
                      - rng.nextDouble() * body * 0.5;
        hist.add(new Candle(open, high, low, close));
        if (hist.size() > 120) hist.remove(0);
    }

    private void notify_() { listeners.forEach(Runnable::run); }
}