package com.example.TaskManagementSystem.repositories;

import com.example.TaskManagementSystem.models.Task;
import com.example.TaskManagementSystem.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {


    @Query("SELECT t FROM Task t WHERE t.author = :user OR :user MEMBER OF t.assignees")
    List<Task> findByAuthorOrAssigneesContains(@Param("user") User user);
}
