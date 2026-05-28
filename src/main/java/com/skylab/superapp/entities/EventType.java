package com.skylab.superapp.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "event_types")
public class EventType extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name ="name")
    private String name;

    // Bu etkinligi yoneten Keycloak takim/grup adi (yetki sahipligi).
    // null ise konvansiyona dusulur (sahip takim == name). Explicit ownership = denetlenebilir.
    @Column(name = "owner_group")
    private String ownerGroup;

}
