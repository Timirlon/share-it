package org.example.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findAllByOwnerId(int ownerId, Pageable pageable);

    @Query("""
        SELECT i FROM Item i
        WHERE (UPPER(i.name) LIKE UPPER(CONCAT('%', :text, '%'))
                OR UPPER(i.description) LIKE UPPER(CONCAT('%', :text, '%')))
                        AND i.available IS TRUE""")
    List<Item> findAllByText(String text, Pageable pageable);
}
