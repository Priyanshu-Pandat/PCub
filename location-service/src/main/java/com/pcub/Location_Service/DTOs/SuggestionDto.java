package com.pcub.Location_Service.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuggestionDto {
    private String description;
    private String placeId;
    private String mainText;
    private String secondaryText;
}
