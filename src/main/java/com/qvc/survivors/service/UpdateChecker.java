package com.qvc.survivors.service;

/**
 * Checks GitHub Releases API for newer versions.
 * Non-blocking — runs in a daemon thread on startup.
 */
public class UpdateChecker {
    private static final String RELEASES_URL =
        "https://api.github.com/repos/schwarztim/qvc-survivors/releases/latest";
    private static final String DOWNLOAD_URL =
        "https://github.com/schwarztim/qvc-survivors/releases/latest";

    private volatile String latestVersion = null;
    private volatile boolean updateAvailable = false;
    private volatile boolean checked = false;

    public void checkAsync() {
        Thread t = new Thread(() -> {
            try {
                var client = java.net.http.HttpClient.newBuilder()
                    .connectTimeout(java.time.Duration.ofSeconds(5))
                    .build();
                var request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(RELEASES_URL))
                    .header("Accept", "application/vnd.github.v3+json")
                    .timeout(java.time.Duration.ofSeconds(10))
                    .GET()
                    .build();
                var response = client.send(request,
                    java.net.http.HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    String body = response.body();
                    int idx = body.indexOf("\"tag_name\"");
                    if (idx >= 0) {
                        int colonIdx = body.indexOf(':', idx);
                        int quoteStart = body.indexOf('"', colonIdx + 1);
                        int quoteEnd = body.indexOf('"', quoteStart + 1);
                        String tag = body.substring(quoteStart + 1, quoteEnd);
                        latestVersion = tag.startsWith("v") ? tag.substring(1) : tag;
                        updateAvailable = isNewer(latestVersion, getCurrentVersion());
                    }
                }
            } catch (Exception e) {
                // Silently fail — no internet, timeout, rate limit, etc.
            } finally {
                checked = true;
            }
        });
        t.setDaemon(true);
        t.setName("UpdateChecker");
        t.start();
    }

    private String getCurrentVersion() {
        try (var is = getClass().getResourceAsStream("/version.properties")) {
            if (is != null) {
                var props = new java.util.Properties();
                props.load(is);
                return props.getProperty("version", "0.0.0");
            }
        } catch (Exception e) { }
        return "0.0.0";
    }

    private boolean isNewer(String latest, String current) {
        try {
            String[] l = latest.split("\\.");
            String[] c = current.split("\\.");
            for (int i = 0; i < Math.max(l.length, c.length); i++) {
                int lv = i < l.length ? Integer.parseInt(l[i]) : 0;
                int cv = i < c.length ? Integer.parseInt(c[i]) : 0;
                if (lv > cv) return true;
                if (lv < cv) return false;
            }
        } catch (NumberFormatException e) { }
        return false;
    }

    public boolean isUpdateAvailable() { return updateAvailable; }
    public boolean isChecked() { return checked; }
    public String getLatestVersion() { return latestVersion; }
    public String getDownloadUrl() { return DOWNLOAD_URL; }
}
