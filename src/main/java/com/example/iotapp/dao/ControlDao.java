package com.example.iotapp.dao;

import com.example.iotapp.entity.Control;
import com.example.iotapp.entity.Sensor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

@Repository
public interface ControlDao extends JpaRepository<Control,Long> {

    Page<Control> findAll(Pageable pageable);

    @Query("SELECT c FROM Control c WHERE CAST(c.date AS string) LIKE %:keyword% ")
    Page<Control> searchControlByDate(@Param("keyword") String keyword, Pageable pageable);

}
