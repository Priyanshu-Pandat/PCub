package com.pcub.Ride_Service.entity;
import com.pcub.Ride_Service.dtos.FareEstimateRequest;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.pcub.Ride_Service.modals.*;


import java.time.LocalDateTime;



import com.pcub.Ride_Service.dtos.FareEstimateRequest;

import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ride_seq")
    @SequenceGenerator(name = "ride_seq", sequenceName = "ride_sequence", allocationSize = 1)
    private Long rideId;

    private Long userId;     // Who booked the ride
    private Long driverId;   // Assigned driver

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;  // Selected vehicle type

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "pickup_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "pickup_longitude")),
            @AttributeOverride(name = "sourcePlaceId", column = @Column(name = "pickup_source_place_id")),
            @AttributeOverride(name = "destinationPlaceId", column = @Column(name = "pickup_destination_place_id")),
            @AttributeOverride(name = "promoCode", column = @Column(name = "pickup_promo_code"))
    })
    private FareEstimateRequest pickupLocation;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "drop_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "drop_longitude")),
            @AttributeOverride(name = "sourcePlaceId", column = @Column(name = "drop_source_place_id")),
            @AttributeOverride(name = "destinationPlaceId", column = @Column(name = "drop_destination_place_id")),
            @AttributeOverride(name = "promoCode", column = @Column(name = "drop_promo_code"))
    })
    private FareEstimateRequest dropLocation;

    private String pickupAddress;
    private String dropAddress;

    private Double estimatedFare;
    private Integer estimatedDistanceInKm;
    private Integer estimatedTimeInMin;

    private Double fare;               // Final fare
    private Double discountAmount;     // If any promo applied
    private String appliedPromoCode;

    @Enumerated(EnumType.STRING)
    private RideStatus status;

   // @Enumerated(EnumType.STRING)
    //private PaymentMethod paymentMethod;

    private Boolean isPaymentCompleted;

    private Boolean isCancelled;
    private String cancellationReason;
    private LocalDateTime cancelledAt;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
