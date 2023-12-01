package org.todo.spring.todolist.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.todo.spring.todolist.model.Tasks;
import org.todo.spring.todolist.repositories.TasksRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class TodoController {

    // Appel du repo de mon model Tasks
    private TasksRepository repo;

    // Constructeur
    public TodoController(TasksRepository repo) {
        this.repo = repo;
        log.info("Ctr TasksRepository");
    }

    // Appel de mon objet Logger pour mes différents besoins de log dans mes routes
    private static final Logger log = LoggerFactory.getLogger(TasksRepository.class);

    /**
     * Route GET pour fetch toutes mes tasks
     * 
     * @return ResponseEntity avec le json de toutes mes tasks, et code 200
     */
    @GetMapping("/tasks")
    public ResponseEntity<List<Tasks>> getAllTasks() {
        try {
            // Grâce à mon repo, je récupère toutes les tasks avec la méthode findAll
            List<Tasks> tasks = repo.findAll();
            // Si ma liste est vide, je renvoie un 404 NO CONTENT
            if (tasks.isEmpty()) {
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NO_CONTENT);
            }
            // Si j'ai bien des tasks, je renvoie la liste les contenant, avec un code 200
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch (Exception e) {
            // Log et renvoi d'une erreur avec code 500 si problème
            log.error("Une erreur s'est produite lors de la récupération des tâches : ", e);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Route POST pour enregistrer une nouvelle task
     * 
     * @param taskIn La task envoyée par le client
     * @return ResponseEntity avec une string indiquant la création de la tâche, et
     *         code 201
     */
    @PostMapping("/newtask")
    public ResponseEntity<String> postNewTask(@RequestBody Tasks taskIn) {
        // Je contrôle que la task envoyée n'est pas null
        if (taskIn == null) {
            // Log et renvoi d'un 400
            log.error("L'objet Tâche reçu est null");
            return new ResponseEntity<>("L'objet Tâches ne peut pas être null.", HttpStatus.BAD_REQUEST);
        }
        try {
            // Depuis mon repo, j'utilise la méthode save pour enregistrer la task, et
            // renvoie un code 201 pour indiquer la création en base de données de la task
            repo.save(taskIn);
            return new ResponseEntity<>("Tâche créée ! (ᵔᴥᵔ)", HttpStatus.CREATED);
        } catch (Exception e) {
            // Log et renvoi d'une erreur avec code 500 si problème
            log.error("Une erreur est survenue lors de la création de la tâche : ", e);
            return new ResponseEntity<>("Une erreur est survenue lors de la création de la tâche.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Route DELETE pour supprimer une task
     * 
     * @param id L'ID de la task à supprimer
     * @return ResponseEntity avec une string indiquant la suppression de la tâche,
     *         et code 200
     */
    @DeleteMapping("/task/{id}")
    public ResponseEntity<String> deleteTasks(@PathVariable Long id) {
        try {
            // Je cherche la task selon son id
            Optional<Tasks> task = repo.findById(id);
            if (task.isPresent()) {
                // Suppression de cette dernière si elle existe bien, et renvoi d'un 200 après
                // suppression
                repo.deleteById(id);
                return new ResponseEntity<>("Tâche supprimée ! \\(°Ω°)/", HttpStatus.OK);
            } else {
                // Si elle n'existe pas (isPresent a renvoyé false), je renvoi un 404
                return new ResponseEntity<>("La tâche n'existe pas ! ¯\\(°_o)/¯", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            // Log et renvoi d'une erreur avec code 500 si problème
            log.error("Une erreur est survenue lors de la suppression de la tâche : ", e);
            return new ResponseEntity<>("Une erreur est survenue lors de la suppression de la tâche.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Route PATCH pour terminer une task
     * 
     * @param id L'ID de la task que l'on souhaite mettre en status terminé
     * @return ResponseEntity avec une string indiquant l'id de la task mis à jour,
     *         et code 200
     */
    @PatchMapping("/task/{id}")
    public ResponseEntity<String> updateTasks(@PathVariable Long id) {
        try {
            // Je cherche la task selon son id
            Optional<Tasks> task = repo.findById(id);
            if (task.isPresent()) {
                Tasks completeTask = task.get();
                // Je vérifie si cette dernière a été complétée, et renvoie un code 200 si ce
                // n'est pas le cas en précisant qu'elle est terminée
                if (completeTask.isCompleted()) {
                    return new ResponseEntity<>("La tâche " + id + " est déjà complétée.", HttpStatus.OK);
                } else {
                    // Si elle n'est pas terminée, je la passe à true et renvoie un 200
                    completeTask.setCompleted(true);
                    repo.save(completeTask);
                    return new ResponseEntity<>("La tâche " + id + " est maintenant complétée ! ヽ(´▽`)/",
                            HttpStatus.OK);
                }
                // Cas où la tâche n'existe pas, renvoi également d'un 200
            } else {
                // Si elle n'existe pas (isPresent a renvoyé false), je renvoi un 404
                return new ResponseEntity<>("La tâche " + id + " n'existe pas ! ¯\\(°_o)/¯", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            // Log et renvoi d'une erreur avec code 500 si problème
            log.error("Une erreur est survenue lors de la mise à jour de la tâche : ", e);
            return new ResponseEntity<>("Une erreur est survenue lors de la mise à jour de la tâche.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
