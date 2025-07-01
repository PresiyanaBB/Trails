package com.trails_art.trails.repositories.project;

import com.trails_art.trails.models.Project;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomProjectRepositoryImpl implements CustomProjectRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Project> findAllWithEmptyArtists() {
        String jpql = "SELECT p FROM Project p WHERE p.artists IS EMPTY";
        TypedQuery<Project> query = entityManager.createQuery(jpql, Project.class);
        return query.getResultList();
    }
}
