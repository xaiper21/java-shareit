package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;


public interface ItemRepository extends JpaRepository<Item, Long> {

    @EntityGraph(attributePaths = "owner")
    Collection<Item> findByOwnerId(Long ownerId);

    @EntityGraph(attributePaths = "owner")
    @Query("SELECT i FROM Item i LEFT JOIN FETCH i.comments LEFT JOIN FETCH i.bookings WHERE i.owner.id = :ownerId")
    Collection<Item> findByOwnerIdFetch(@Param("ownerId") Long ownerId);

    @Query(" select i from Item i " +
            "where i.available = true " +
            " and (upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')))")
    Collection<Item> search(String text);


}
