package com.realestate.repository;

import com.realestate.entity.Message;
import com.realestate.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    Page<Message> findBySenderOrReceiverOrderByCreatedAtDesc(
            User sender, User receiver, Pageable pageable);
    
    @Query("SELECT m FROM Message m WHERE " +
           "(m.sender = ?1 AND m.receiver = ?2) OR (m.sender = ?2 AND m.receiver = ?1) " +
           "ORDER BY m.createdAt ASC")
    Page<Message> findConversation(User user1, User user2, Pageable pageable);
    
    long countByReceiverAndIsReadFalse(User receiver);
}