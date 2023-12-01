package org.todo.spring.todolist.controller;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.todo.spring.todolist.repositories.TasksRepository;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TasksRepository repo;

    @Test
    public void testGetAllTasks() throws Exception {
        given(this.repo.findAll()).willReturn(Collections.emptyList());

        this.mockMvc.perform(get("/tasks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // attendu HttpStatus.NO_CONTENT (204) si la liste est vide
    }
}
