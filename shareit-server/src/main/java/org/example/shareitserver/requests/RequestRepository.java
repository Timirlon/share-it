package org.example.shareitserver.requests;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findAllByRequesterId_OrderByCreatedDesc(int requesterId);

    Page<Request> findAllByRequesterId_NotOrderByCreatedDesc(int requesterId, Pageable pageable);
}
