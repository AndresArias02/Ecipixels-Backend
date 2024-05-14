package edu.eci.arsw.repository;

import edu.eci.arsw.model.Game;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends CrudRepository<Game,String> {
}
