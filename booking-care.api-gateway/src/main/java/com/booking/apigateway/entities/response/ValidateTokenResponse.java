package com.booking.apigateway.entities.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidateTokenResponse {
    private boolean valid;
    private List<String> role;
}
