package com.pcub.Ride_Service.modals;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DynamicPricingConditions {
    private boolean isRainy;
    private boolean isZoneSurcharge;
    private boolean isNight;
     private boolean isPeakHour;

}
