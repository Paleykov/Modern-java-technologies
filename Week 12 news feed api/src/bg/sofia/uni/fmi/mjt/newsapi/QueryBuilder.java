package bg.sofia.uni.fmi.mjt.newsapi;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.*;

public final class QueryBuilder {
    private String q;
    private String searchIn;
    private String sources;
    private String domains;
    private String excludeDomains;
    private LocalDateTime from;
    private LocalDateTime to;
    private String language;
    private String sortBy;
    private int pageSize = 100;
    private int page = 1;
    private String category;
    private String country;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public QueryBuilder keywords(String... keywords) {
        if (keywords == null || keywords.length == 0 || Arrays.stream(keywords).allMatch(String::isBlank)) {
            throw new IllegalArgumentException("At least one keyword is required.");
        }
        this.q = String.join(" ", Arrays.stream(keywords).map(String::trim).toList());
        return this;
    }

    public QueryBuilder rawQuery(String rawQ) {
        if (rawQ == null || rawQ.isBlank()) {
            throw new IllegalArgumentException("Raw query cannot be blank.");
        }
        this.q = rawQ.trim();
        return this;
    }

    public QueryBuilder searchIn(String... fields) {
        this.searchIn = String.join(",", fields);
        return this;
    }

    public QueryBuilder sources(String... sourceIds) {
        if (sourceIds.length > 20) {
            throw new IllegalArgumentException("Maximum 20 sources are allowed.");
        }
        this.sources = String.join(",", sourceIds);
        return this;
    }

    public QueryBuilder domains(String... domains) {
        this.domains = String.join(",", domains);
        return this;
    }

    public QueryBuilder excludeDomains(String... domains) {
        this.excludeDomains = String.join(",", domains);
        return this;
    }

    public QueryBuilder from(LocalDateTime from) {
        this.from = from;
        return this;
    }

    public QueryBuilder to(LocalDateTime to) {
        this.to = to;
        return this;
    }

    public QueryBuilder language(String language) {
        this.language = language;
        return this;
    }

    public QueryBuilder sortBy(String sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    public QueryBuilder pageSize(int size) {
        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100.");
        }
        this.pageSize = size;
        return this;
    }

    public QueryBuilder page(int page) {
        if (page <= 0) {
            throw new IllegalArgumentException("Page must be >= 1.");
        }
        this.page = page;
        return this;
    }

    public QueryBuilder country(String country) {
        this.country = country;
        return this;
    }

    public QueryBuilder category(String category) {
        this.category = category;
        return this;
    }

    public void validate() {
        if (q == null || q.isBlank()) {
            throw new IllegalStateException("Query string (q) is required.");
        }
        if (sources != null && (country != null || category != null)) {
            throw new IllegalStateException("Cannot use 'sources' with 'country' or 'category'.");
        }
    }

    public Map<String, String> buildParams() {
        validate();
        Map<String, String> params = new LinkedHashMap<>();
        params.put("q", q);

        if (searchIn != null) params.put("searchIn", searchIn);
        if (sources != null) params.put("sources", sources);
        if (domains != null) params.put("domains", domains);
        if (excludeDomains != null) params.put("excludeDomains", excludeDomains);
        if (from != null) params.put("from", DATE_FORMATTER.format(from));
        if (to != null) params.put("to", DATE_FORMATTER.format(to));
        if (language != null) params.put("language", language);
        if (sortBy != null) params.put("sortBy", sortBy);
        if (category != null) params.put("category", category);
        if (country != null) params.put("country", country);

        params.put("pageSize", String.valueOf(pageSize));
        params.put("page", String.valueOf(page));
        return params;
    }

    public boolean requiresTopHeadlines() {
        return category != null || country != null;
    }

}



