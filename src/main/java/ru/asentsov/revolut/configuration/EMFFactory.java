package ru.asentsov.revolut.configuration;

import org.glassfish.hk2.api.Factory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EMFFactory implements Factory<EntityManagerFactory> {
    private final EntityManagerFactory emf;

    public EMFFactory() {
        this.emf = Persistence.createEntityManagerFactory("ru.asentsov.revolut.model.entity.AccountEntity");
    }

    @Override
    public EntityManagerFactory provide() {
        return emf;
    }

    @Override
    public void dispose(EntityManagerFactory entityManagerFactory) {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
