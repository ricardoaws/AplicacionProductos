package com.proyectotfg.rest.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyectotfg.rest.modelo.Categoria;

public interface CategoriaRepositorio extends JpaRepository<Categoria, Long> {

}
