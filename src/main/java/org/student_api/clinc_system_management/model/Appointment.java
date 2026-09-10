package org.student_api.clinc_system_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.student_api.clinc_system_management.role.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id ;

    @ManyToOne
    @JoinColumn(name = "patient_id" , nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id" , nullable = false)
    private Doctor doctor;

    @Column(nullable = false)
    private LocalDate date ;

    @Column(nullable = false)
    private LocalTime time ;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status ;

}
