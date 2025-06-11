package com.pcub.Location_Service.service;

import com.pcub.Location_Service.DTOs.ResolvedLocationDto;
import com.pcub.Location_Service.DTOs.SuggestionDto;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface GoogleMapApisService {
    List<SuggestionDto> getPlaceSuggestions(String input) ;
    ResolvedLocationDto resolvePlaceIdToCoordinates(String placeId) ;
}
