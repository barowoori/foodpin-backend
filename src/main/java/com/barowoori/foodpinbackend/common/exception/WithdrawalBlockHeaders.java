package com.barowoori.foodpinbackend.common.exception;

import org.springframework.http.HttpHeaders;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public final class WithdrawalBlockHeaders {

    private static final String PREFIX = "X-Withdrawal-Blocked-";

    private WithdrawalBlockHeaders() {
    }

    public static HttpHeaders byTruckAndEvent(
            String truckId,
            String truckName,
            String eventId,
            String eventName,
            LocalDate blockedUntilDate
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(PREFIX + "Type", "TRUCK_IN_PROGRESS_EVENT");
        headers.add(PREFIX + "Truck-Id", truckId);
        headers.add(PREFIX + "Truck-Name", encodeHeaderValue(truckName));
        headers.add(PREFIX + "Truck-Name-Encoding", "URL-ENCODED-UTF-8");
        headers.add(PREFIX + "Event-Id", eventId);
        headers.add(PREFIX + "Event-Name", encodeHeaderValue(eventName));
        headers.add(PREFIX + "Event-Name-Encoding", "URL-ENCODED-UTF-8");
        headers.add(PREFIX + "Until-Date", blockedUntilDate.toString());
        return headers;
    }

    public static HttpHeaders byHostedEvent(
            String eventId,
            String eventName,
            LocalDate blockedUntilDate
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(PREFIX + "Type", "HOSTED_EVENT_IN_PROGRESS");
        headers.add(PREFIX + "Event-Id", eventId);
        headers.add(PREFIX + "Event-Name", encodeHeaderValue(eventName));
        headers.add(PREFIX + "Event-Name-Encoding", "URL-ENCODED-UTF-8");
        headers.add(PREFIX + "Until-Date", blockedUntilDate.toString());
        return headers;
    }

    private static String encodeHeaderValue(String value) {
        if (value == null) {
            return "";
        }
        String sanitizedValue = value.replaceAll("[\\r\\n]+", " ").trim();
        return URLEncoder.encode(sanitizedValue, StandardCharsets.UTF_8);
    }
}
