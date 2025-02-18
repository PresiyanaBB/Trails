package com.trails_art.trails.services.project;

import com.trails_art.trails.models.Project;
import com.trails_art.trails.repositories.project.JdbcProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JdbcProjectService implements ProjectService {

    private final JdbcProjectRepository jdbcProjectRepository;

    public JdbcProjectService(JdbcProjectRepository jdbcProjectRepository) {
        this.jdbcProjectRepository = jdbcProjectRepository;
    }

    public List<Project> findAll() {
        return jdbcProjectRepository.findAll();
    }

    public Optional<Project> findById(UUID id) {
        return jdbcProjectRepository.findById(id);
    }

    public void create(Project project) {
        jdbcProjectRepository.create(project);
    }

    public void update(Project project, UUID id){
        jdbcProjectRepository.update(project, id);
    }

    public void delete(UUID id){
        jdbcProjectRepository.delete(id);
    }

    public int count(){
        return jdbcProjectRepository.count();
    }

    public void saveAll(List<Project> projects){
        jdbcProjectRepository.saveAll(projects);
    }

    public List<Project> findByName(String name){
        return jdbcProjectRepository.findByName(name);
    }
}
