package com.example.ecomm.mcp.catalog;

/** {@code GET /products/{id}/pitch}: the marketing copy the service generated for one product. */
public record Pitch(String productId, String notification) {
}
