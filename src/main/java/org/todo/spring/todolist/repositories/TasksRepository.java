package org.todo.spring.todolist.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.todo.spring.todolist.model.Tasks;

@Repository
public interface TasksRepository extends JpaRepository<Tasks,Long> {

}
