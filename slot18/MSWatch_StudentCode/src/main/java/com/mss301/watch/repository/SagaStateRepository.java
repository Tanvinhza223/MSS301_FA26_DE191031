package com.mss301.watch.repository;
import com.mss301.watch.entity.SagaState;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface SagaStateRepository extends JpaRepository<SagaState, String> { List<SagaState> findAllByOrderByCreatedAtDesc(Pageable pageable); }

