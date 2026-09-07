/**
 * EXERCISE — Chapter 9: Life and Death of an Object
 *
 * Problem: Build an Immutable HttpRequest using the Builder Pattern
 * (Real-world: this is close to what OkHttp and Spring WebClient do)
 * -----------------------------------------------------------------
 *
 * Extend the HttpRequest builder from 01_BuilderPattern.java with:
 *
 * 1. QueryParam support: add addQueryParam(String key, String value)
 *    The final URL should append "?key1=val1&key2=val2"
 *    If the URL already has "?", use "&" separator.
 *
 * 2. A Cookie class (immutable record): name, value, maxAgeSeconds
 *    Add addCookie(Cookie c) to the builder.
 *    Cookies appear in the "Cookie" header as "name=value; name2=value2".
 *
 * 3. Validation in build():
 *    - URL must start with "http://" or "https://"
 *    - timeoutMs must be between 100ms and 300_000ms (5 minutes)
 *    - Content-Type header must be set if body is not null
 *
 * 4. A static factory method: HttpRequest.get(String url) and HttpRequest.post(String url, String body)
 *    that return pre-configured builders.
 *
 * 5. A copy(HttpRequest original) static method on Builder that creates a
 *    Builder initialized with all settings from an existing request
 *    (for creating variants of a base request).
 *
 * Test all five features.
 */
import java.util.*;

public class Exercise {

    record Cookie(String name, String value, int maxAgeSeconds) {
        Cookie { // compact constructor — validate
            if (name == null || name.isBlank())  throw new IllegalArgumentException("cookie name required");
            if (value == null)                    throw new IllegalArgumentException("cookie value required");
        }
    }

    static final class HttpRequest {
        private final String method;
        private final String url;
        private final String body;
        private final int    timeoutMs;
        private final Map<String, String> headers;
        private final List<Cookie> cookies;

        private HttpRequest(Builder b) {
            this.method    = b.method;
            this.url       = buildUrl(b);
            this.body      = b.body;
            this.timeoutMs = b.timeoutMs;
            this.headers   = Collections.unmodifiableMap(new LinkedHashMap<>(b.headers));
            this.cookies   = Collections.unmodifiableList(new ArrayList<>(b.cookies));
        }

        private static String buildUrl(Builder b) {
            if (b.queryParams.isEmpty()) return b.url;
            StringBuilder sb = new StringBuilder(b.url);
            sb.append(b.url.contains("?") ? "&" : "?");
            b.queryParams.forEach((k, v) ->
                sb.append(k).append("=").append(v).append("&"));
            sb.setLength(sb.length() - 1); // trim trailing &
            return sb.toString();
        }

        @Override public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(method).append(" ").append(url).append("\n");
            headers.forEach((k, v) -> sb.append("  ").append(k).append(": ").append(v).append("\n"));
            if (!cookies.isEmpty()) {
                sb.append("  Cookie: ");
                cookies.forEach(c -> sb.append(c.name()).append("=").append(c.value()).append("; "));
                sb.append("\n");
            }
            if (body != null) sb.append("  Body: ").append(body);
            return sb.toString();
        }

        static class Builder {
            private String method;
            private String url;
            private String body           = null;
            private int    timeoutMs      = 5_000;
            private Map<String, String> headers     = new LinkedHashMap<>();
            private Map<String, String> queryParams = new LinkedHashMap<>();
            private List<Cookie>        cookies     = new ArrayList<>();

            Builder(String method, String url) {
                this.method = method;
                this.url    = url;
            }

            // TODO: implement addQueryParam, addCookie, header, body, timeoutMs, build()
            // TODO: implement validation in build()
            // TODO: static factory methods get() and post()
            // TODO: static copy() method

            HttpRequest build() {
                // TODO: validate URL format, timeoutMs range, Content-Type if body set
                return new HttpRequest(this);
            }
        }
    }

    public static void main(String[] args) {

        System.out.println("=== Builder Pattern ===\n");

        // Test 1: factory method + query params
        // HttpRequest req = HttpRequest.Builder.get("https://api.example.com/search")
        //     .addQueryParam("q", "java builder pattern")
        //     .addQueryParam("page", "1")
        //     .header("Authorization", "Bearer abc123")
        //     .build();
        // System.out.println(req);

        // Test 2: POST with cookies
        // HttpRequest post = HttpRequest.Builder.post("https://api.example.com/login",
        //         "{\"user\":\"alice\",\"pass\":\"secret\"}")
        //     .header("Content-Type", "application/json")
        //     .addCookie(new Cookie("session", "xyz789", 3600))
        //     .build();
        // System.out.println(post);

        // Test 3: copy and modify
        // HttpRequest.Builder variant = HttpRequest.Builder.copy(req)
        //     .addQueryParam("page", "2");
        // System.out.println(variant.build());

        // Test 4: validation failures
        // new HttpRequest.Builder("GET", "ftp://bad.url").build();     // bad scheme
        // new HttpRequest.Builder("GET", "https://ok.com").timeoutMs(50).build(); // too low
        // new HttpRequest.Builder("POST", "https://ok.com").body("data").build(); // no Content-Type
    }
}
