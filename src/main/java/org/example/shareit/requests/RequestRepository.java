package org.example.shareit.requests;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findAllByRequestor_IdOrderByCreatedDesc(int requestorId);

    Page<Request> findAllByRequestor_IdNotOrderByCreatedDesc(int requestorId, Pageable pageable);
}
