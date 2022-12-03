package com.proyectotfg.rest.repos;

import com.proyectotfg.rest.modelo.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepositorio extends JpaRepository<Producto, Long> {

}
