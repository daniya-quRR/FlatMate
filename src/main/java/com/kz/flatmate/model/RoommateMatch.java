package com.kz.flatmate.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roommate_matches", uniqueConstraints =
        @UniqueConstraint(columnNames = {"user_one_id", "user_two_id"}))
public class RoommateMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_one_id", nullable = false)
    private Long userOneId;

    @Column(name = "user_two_id", nullable = false)
    private Long userTwoId;

    private boolean userOneConfirmed;
    private boolean userTwoConfirmed;

    public Long getId() { return id; }
    public Long getUserOneId() { return userOneId; }
    public void setUserOneId(Long userOneId) { this.userOneId = userOneId; }
    public Long getUserTwoId() { return userTwoId; }
    public void setUserTwoId(Long userTwoId) { this.userTwoId = userTwoId; }
    public boolean isUserOneConfirmed() { return userOneConfirmed; }
    public void setUserOneConfirmed(boolean userOneConfirmed) { this.userOneConfirmed = userOneConfirmed; }
    public boolean isUserTwoConfirmed() { return userTwoConfirmed; }
    public void setUserTwoConfirmed(boolean userTwoConfirmed) { this.userTwoConfirmed = userTwoConfirmed; }
    public boolean isConfirmed() { return userOneConfirmed && userTwoConfirmed; }
}
