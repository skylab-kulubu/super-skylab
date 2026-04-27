package com.skylab.superapp.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "tickets")
public class Ticket extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Builder.Default
    @Column(name = "is_sent")
    private boolean sent = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_type", nullable = false)
    private TicketType ticketType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "guest_first_name")
    private String guestFirstName;

    @Column(name = "guest_last_name")
    private String guestLastName;

    @Column(name = "guest_email")
    private String guestEmail;

    @Column(name = "guest_phone_number")
    private String guestPhoneNumber;

    @Column(name = "guest_birthday")
    private LocalDateTime guestBirthday;

    @Column(name = "guest_is_student")
    private Boolean guestIsStudent;

    @Column(name = "guest_university")
    private String guestUniversity;

    @Column(name = "guest_faculty")
    private String guestFaculty;

    @Column(name = "guest_department")
    private String guestDepartment;

    @Column(name = "guest_grade")
    private String guestGrade;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "custom_answers", columnDefinition = "jsonb")
    private Map<String, String> customAnswers;

    @Column(name = "guest_tc_identity_number")
    private String guestTcIdentityNumber;

    @Column(name = "guest_car_plate_number")
    private String guestCarPlateNumber;


    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @JsonManagedReference
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TicketCheckIn> checkIns;


    @Transient
    public String getTicketHolderFullName() {
        if (this.ticketType == TicketType.REGISTERED && this.owner != null) {
            return this.owner.getFirstName() + " " + this.owner.getLastName();
        } else if (this.ticketType == TicketType.GUEST) {
            return this.guestFirstName + " " + this.guestLastName;
        }
        return "Unknown User";
    }



}
