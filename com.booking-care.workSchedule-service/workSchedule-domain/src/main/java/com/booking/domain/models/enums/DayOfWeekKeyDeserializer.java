package com.booking.domain.models.enums;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

import java.io.IOException;

public class DayOfWeekKeyDeserializer extends KeyDeserializer {

    @Override
    public DayOfWeek deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        return DayOfWeek.valueOf(key.toUpperCase()); // "monday" => "MONDAY"
    }
}

