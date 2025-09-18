package com.realestate.repository;

import com.realestate.entity.AIQuery;
import com.realestate.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AIQueryRepository extends JpaRepository<AIQuery, Long> {
    
    Page<AIQuery> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    Page<AIQuery> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    @Query("SELECT AVG(a.responseTimeMs) FROM AIQuery a WHERE a.responseTimeMs IS NOT NULL")
    Double getAverageResponseTime();
    
    @Query("SELECT COUNT(a) FROM AIQuery a WHERE a.createdAt >= CURRENT_DATE")
    long getTodayQueriesCount();
}