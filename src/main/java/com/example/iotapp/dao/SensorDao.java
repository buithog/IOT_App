package com.example.iotapp.dao;

import com.example.iotapp.entity.Sensor;
import org.hibernate.query.SortDirection;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface SensorDao extends JpaRepository<Sensor,Long> {

    @Query(value = "SELECT * FROM sensor ORDER BY sensor.date DESC LIMIT 10", nativeQuery = true)
    List<Sensor> getTenLatestSensor();

    @Query(value = "SELECT * FROM sensor ORDER BY sensor.date DESC LIMIT 1", nativeQuery = true)
    Sensor getLatestSensor();

    @Query("SELECT COUNT(s) FROM Sensor s")
    Long countAllSensors();

    Page<Sensor> findAll(Pageable pageable);

    @Query("SELECT s FROM Sensor s WHERE CAST(s.date AS string) LIKE %:keyword% ")
    Page<Sensor> searchSensorByDate(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT s FROM Sensor s WHERE CAST(s.id AS string) LIKE %:keyword% ")
    Page<Sensor> searchSensorById(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT s FROM Sensor s WHERE CAST(s.lux AS string) LIKE %:keyword% ")
    Page<Sensor> searchSensorByLux(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT s FROM Sensor s WHERE CAST(s.hum AS string) LIKE %:keyword% ")
    Page<Sensor> searchSensorByHum(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT s FROM Sensor s WHERE CAST(s.temp AS string) LIKE %:keyword% ")
    Page<Sensor> searchSensorByTemp(@Param("keyword") String keyword, Pageable pageable);

}
