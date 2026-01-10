package com.goorm.team3.repository;

import com.goorm.team3.domain.JinjuDistrict;
import com.goorm.team3.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByDistrict(JinjuDistrict district);

    Optional<Restaurant> findByTitleAndAddress(String title, String address);

    @Query("SELECT r FROM Restaurant r WHERE r.district = :district ORDER BY FUNCTION('RAND')")
    List<Restaurant> findRandomByDistrict(JinjuDistrict district);

    boolean existsByTitleAndAddress(String title, String address);
}
