package org.example.shareit.requests;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findAllByRequester_IdOrderByCreatedDesc(int requesterId);

    Page<Request> findAllByRequester_IdNotOrderByCreatedDesc(int requesterId, Pageable pageable);
}
