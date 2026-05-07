package br.edu.ifsuldeminas.app.repository;

import org.springframework.data.repository.CrudRepository;

import br.edu.ifsuldeminas.app.model.User;

public interface UserRepository extends CrudRepository<User, Long> {

}
