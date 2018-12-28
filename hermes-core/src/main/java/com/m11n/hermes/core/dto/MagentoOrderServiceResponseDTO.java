package com.m11n.hermes.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MagentoOrderServiceResponseDTO {
    private String result;
    private String message;

    public boolean isSuccess() {
        return result.equalsIgnoreCase("success");
    }
}
