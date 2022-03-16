package com.axonactive.agiletools.agiledeck;

import javax.inject.Singleton;
import javax.json.bind.JsonbConfig;

import io.quarkus.jsonb.JsonbConfigCustomizer;

@Singleton
public class SerializerRegistrationCustomizer implements JsonbConfigCustomizer {

    @Override
    public void customize(JsonbConfig jsonbConfig) {
        jsonbConfig.withPropertyVisibilityStrategy(new JsonAccessFieldStrategy());
    }
    
}
