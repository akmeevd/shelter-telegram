package com.skypro.telegram_team.repositories;

import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {

    Animal findAnimalsByUserId(Long userId);

    Optional<List<Animal>> findAnimalsByName(String name);

    List<Animal> findAllByUserIdNotNullAndState(Animal.AnimalStateEnum inTest);

    List<Animal> findByUserContainsOrderByState(User.OwnerStateEnum ownerStateEnum);
}
