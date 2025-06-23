package com.spring.eac.ai.model;

import java.util.Objects;

/**
 * Record representing detailed information about a capital city.
 * Includes demographic, geographic, and cultural information about the capital.
 *
 * @param stateOrCountry The state or country name
 * @param capital The name of the capital city
 * @param population The population of the capital city
 * @param region The geographic region where the capital is located
 * @param language The primary language spoken in the capital
 * @param currency The official currency used in the capital
 */
public record CapitalWithInfo(
        String stateOrCountry,
        String capital,
        Long population,
        String region,
        String language,
        String currency
) {
    // Compact constructor for validation
    public CapitalWithInfo {
        Objects.requireNonNull(stateOrCountry, "State or country name cannot be null");
        Objects.requireNonNull(capital, "Capital name cannot be null");
        Objects.requireNonNull(population, "Population cannot be null");
        Objects.requireNonNull(region, "Region cannot be null");
        Objects.requireNonNull(language, "Language cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");

        if (population < 0) {
            throw new IllegalArgumentException("Population cannot be negative");
        }

        // Trim strings to ensure consistent formatting
        stateOrCountry = stateOrCountry.trim();
        capital = capital.trim();
        region = region.trim();
        language = language.trim();
        currency = currency.trim();

        // Validate that essential fields are not empty
        /*
        if (stateOrCountry.isEmpty()) {
            throw new IllegalArgumentException("State or country name cannot be empty");
        }
        if (capital.isEmpty()) {
            throw new IllegalArgumentException("Capital name cannot be empty");
        }
        */
    }

    /**
     * Creates a formatted string representation of the capital information.
     *
     * @return A formatted string containing all capital information
     */
    public String toFormattedString() {
        return String.format("""
                The capital of %s is %s.
                The city has a population of %d.
                The city is located in %s.
                The primary language spoken is %s.
                The currency used is %s.""",
                stateOrCountry, capital, population, region, language, currency);
    }

    /**
     * Static factory method for creating a CapitalWithInfo instance.
     *
     * @param stateOrCountry The state or country name
     * @param capital The capital city name
     * @param population The population count
     * @param region The geographic region
     * @param language The primary language
     * @param currency The official currency
     * @return A new validated CapitalWithInfo instance
     */
    public static CapitalWithInfo of(
            String stateOrCountry,
            String capital,
            Long population,
            String region,
            String language,
            String currency
    ) {
        return new CapitalWithInfo(
                stateOrCountry,
                capital,
                population,
                region,
                language,
                currency
        );
    }
}