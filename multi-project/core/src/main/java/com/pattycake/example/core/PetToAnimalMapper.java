package com.pattycake.example.core;

import com.pattycake.example.api.Animal;
import com.pattycake.example.api.Pet;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public interface PetToAnimalMapper {
    Pet map(Animal source);
}
