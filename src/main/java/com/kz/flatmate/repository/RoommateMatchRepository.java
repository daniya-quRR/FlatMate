package com.kz.flatmate.repository;

import com.kz.flatmate.model.RoommateMatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoommateMatchRepository extends JpaRepository<RoommateMatch, Long> {
    Optional<RoommateMatch> findByUserOneIdAndUserTwoId(Long userOneId, Long userTwoId);
    List<RoommateMatch> findByUserOneIdOrUserTwoId(Long userOneId, Long userTwoId);
}
