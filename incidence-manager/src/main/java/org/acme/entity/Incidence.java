package org.acme.entity;

import io.quarkus.hibernate.panache.PanacheRepository;
import jakarta.data.repository.Find;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

import java.util.List;

@Entity
public class Incidence {

    @Id
    @GeneratedValue
    public Long id;

    @Lob
    @Column
    public String message;

    @Lob
    @Column
    public String response;

    @Column
    public String userEmail;

    @Column
    public Long bookingId;

    public interface Repo extends PanacheRepository.Stateless<Incidence, Long> {

        @Find
        List<Incidence> findByUserEmail(String userEmail);

    }

}
