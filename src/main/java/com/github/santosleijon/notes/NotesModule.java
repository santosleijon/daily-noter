package com.github.santosleijon.notes;

import com.github.santosleijon.users.UsersModule;
import com.google.inject.AbstractModule;

public class NotesModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new UsersModule());
        bind(NotesDAO.class).to(NotesDAOImpl.class);
    }
}
