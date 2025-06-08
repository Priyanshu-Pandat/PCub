package com.pcub.Ride_Service.entity;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.pcub.Ride_Service.modals.*;


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

    private Long riderId;
    private Long driverId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "pickup_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "pickup_longitude"))
    })
    private Location pickupLocation;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "drop_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "drop_longitude"))
    })
    private Location dropLocation;

    private String pickupAddress;
    private String dropAddress;

    @Enumerated(EnumType.STRING)
    private RideStatus status;
    private Double fare;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
