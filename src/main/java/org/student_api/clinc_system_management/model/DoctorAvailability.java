package org.student_api.clinc_system_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "doctor_availability")
public class DoctorAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id ;

    @ManyToOne
    @JoinColumn(name = "doctor_id" , nullable = false)
    private Doctor doctor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime ;
    @Column(nullable = false)
    private LocalTime endTime ;
}
