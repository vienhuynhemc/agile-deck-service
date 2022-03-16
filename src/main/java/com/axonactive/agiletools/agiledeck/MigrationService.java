package com.axonactive.agiletools.agiledeck;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.flywaydb.core.Flyway;

@ApplicationScoped
public class MigrationService{

    @Inject
    Flyway flyway;

    public void checkMigration(){
        flyway.clean(); 
        flyway.migrate();
    }
}
