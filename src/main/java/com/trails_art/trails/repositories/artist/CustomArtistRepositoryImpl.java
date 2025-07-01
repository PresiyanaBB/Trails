package com.trails_art.trails.repositories.artist;

import com.trails_art.trails.models.Artist;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomArtistRepositoryImpl implements CustomArtistRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Artist> findAllWithEmptyProjects() {
        String jpql = "SELECT a FROM Artist a WHERE a.projects IS EMPTY";
        TypedQuery<Artist> query = entityManager.createQuery(jpql, Artist.class);
        return query.getResultList();
    }
}
