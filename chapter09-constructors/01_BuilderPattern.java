/**
 * CONCEPT: Builder Pattern — constructing complex objects cleanly
 *
 * When a class has many optional parameters, telescoping constructors become
 * unreadable. The Builder pattern solves this elegantly.
 *
 * Why this matters:
 *   - Builder is one of the most commonly used patterns in real codebases
 *   - Libraries like OkHttp, Retrofit, Spring use builders everywhere
 *   - Lombok @Builder generates this automatically — understanding what it does matters
 *   - Combine with immutability: the built object is read-only after construction
 *
 * Interview question: "Name design patterns you use daily"
 * Builder is the most honest answer for most Java developers.
 */
class BuilderPattern {

    // --- The built class: immutable, all fields final ---
    static final class HttpRequest {
        private final String  method;
        private final String  url;
        private final String  body;
        private final int     timeoutMs;
        private final boolean followRedirects;
        private final java.util.Map<String, String> headers;

        // Private constructor — only Builder can call this
        private HttpRequest(Builder builder) {
            this.method          = builder.method;
            this.url             = builder.url;
            this.body            = builder.body;
            this.timeoutMs       = builder.timeoutMs;
            this.followRedirects = builder.followRedirects;
            this.headers         = java.util.Collections.unmodifiableMap(builder.headers);
        }

        // Getters — read only
        String  getMethod()          { return method; }
        String  getUrl()             { return url; }
        String  getBody()            { return body; }
        int     getTimeoutMs()       { return timeoutMs; }
        boolean isFollowRedirects()  { return followRedirects; }
        java.util.Map<String,String> getHeaders() { return headers; }

        @Override public String toString() {
            return method + " " + url + " timeout=" + timeoutMs + "ms"
                + (body != null ? " body=" + body.substring(0, Math.min(20, body.length())) + "..." : "")
                + " headers=" + headers;
        }

        // --- Inner Builder class ---
        static class Builder {
            // Required fields
            private final String method;
            private final String url;

            // Optional fields with sensible defaults
            private String  body            = null;
            private int     timeoutMs       = 5_000;
            private boolean followRedirects = true;
            private java.util.Map<String, String> headers = new java.util.HashMap<>();

            Builder(String method, String url) {
                if (method == null || method.isBlank()) throw new IllegalArgumentException("method required");
                if (url    == null || url.isBlank())    throw new IllegalArgumentException("url required");
                this.method = method;
                this.url    = url;
            }

            // Each setter returns `this` for chaining
            Builder body(String body)               { this.body = body; return this; }
            Builder timeoutMs(int ms)               { this.timeoutMs = ms; return this; }
            Builder followRedirects(boolean follow) { this.followRedirects = follow; return this; }
            Builder header(String key, String value){ this.headers.put(key, value); return this; }

            HttpRequest build() {
                // Validation before building
                if ("POST".equals(method) || "PUT".equals(method)) {
                    if (body == null) throw new IllegalStateException("POST/PUT requires a body");
                }
                return new HttpRequest(this);
            }
        }
    }

    public static void main(String[] args) {

        // --- Simple GET: only required fields ---
        HttpRequest get = new HttpRequest.Builder("GET", "https://api.example.com/users")
            .header("Authorization", "Bearer token123")
            .timeoutMs(3_000)
            .build();
        System.out.println("GET:  " + get);

        // --- POST with body and headers ---
        HttpRequest post = new HttpRequest.Builder("POST", "https://api.example.com/users")
            .body("{\"name\":\"Alice\",\"email\":\"alice@example.com\"}")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer token123")
            .timeoutMs(10_000)
            .followRedirects(false)
            .build();
        System.out.println("POST: " + post);

        // --- Validation: POST without body fails at build() ---
        try {
            new HttpRequest.Builder("POST", "https://api.example.com/data").build();
        } catch (IllegalStateException e) {
            System.out.println("Caught: " + e.getMessage());
        }

        // --- Reuse a builder ---
        HttpRequest.Builder baseBuilder = new HttpRequest.Builder("GET", "https://api.example.com")
            .header("Authorization", "Bearer token123")
            .timeoutMs(5_000);

        HttpRequest req1 = baseBuilder.build();
        // Note: baseBuilder is not safe to reuse after .build() in production
        // Consider making a copy constructor or using a prototype

        // TRY THIS:
        // 1. Add a retries(int n) option to the builder with validation (0-5).
        // 2. Add a validate() method that checks URL starts with https:// — throw if not.
        // 3. Make Builder implement the Prototype pattern: add a copy() method
        //    that returns a new Builder with all current settings copied.
        //    Use it to create variants of a base request.
    }
}
