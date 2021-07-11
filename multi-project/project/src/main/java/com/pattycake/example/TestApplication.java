package com.pattycake.example;

import java.util.Objects;
import com.pattycake.example.api.Animal;
import com.pattycake.example.api.Pet;
import com.pattycake.example.core.PetToAnimalMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class TestApplication implements CommandLineRunner {

    
    private PetToAnimalMapper mapper;
    private Pet pet;

    public TestApplication( PetToAnimalMapper mapper) {
        this.mapper = Objects.requireNonNull(mapper);
    }

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Animal source = Animal.builder()
        .name("Totoro")
        .build();

        Pet pet = mapper.map(source);

        log.info("pet[{}]", pet);
    }

}
