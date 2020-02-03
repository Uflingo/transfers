package ru.asentsov.revolut.configuration;


import org.glassfish.hk2.utilities.binding.AbstractBinder;
import ru.asentsov.revolut.dao.AccountsDaoImpl;
import ru.asentsov.revolut.dao.IAccountsDao;
import ru.asentsov.revolut.repository.IRepository;
import ru.asentsov.revolut.repository.RepositoryHibernateImpl;

import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;

public class ApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(AccountsDaoImpl.class).to(IAccountsDao.class);
        bind(RepositoryHibernateImpl.class).to(IRepository.class);
        bindFactory(EMFFactory.class).to(EntityManagerFactory.class).in(Singleton.class);
    }
}
