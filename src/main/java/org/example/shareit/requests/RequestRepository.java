package org.example.shareit.requests;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findAllByRequestor_IdOrderByCreatedDesc(int requestorId);

    List<Request> findAllByRequestor_IdNotOrderByCreatedDesc(int requestorId);
}
