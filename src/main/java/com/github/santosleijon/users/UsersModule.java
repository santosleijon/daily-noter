package com.github.santosleijon.users;

import com.google.inject.AbstractModule;

public class UsersModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(UsersDAO.class).to(UsersDAOImpl.class);
        bind(UserSessionsDAO.class).to(UserSessionsDAOImpl.class);
    }
}
