package com.goorm.team3.repository;

import com.goorm.team3.domain.JinjuDistrict;
import com.goorm.team3.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByDistrict(JinjuDistrict district);

    @Query(value = "SELECT * FROM restaurant WHERE district = :district AND category LIKE %:category%", nativeQuery = true)
    List<Restaurant> findByDistrictAndCategory(String district, String category);

    Optional<Restaurant> findByTitleAndAddress(String title, String address);

    @Query(value = "SELECT * FROM restaurant WHERE district = :district ORDER BY RAND() LIMIT 1", nativeQuery = true)
    List<Restaurant> findRandomByDistrict(String district);

    @Query(value = "SELECT * FROM restaurant WHERE district = :district AND category LIKE %:category% ORDER BY RAND() LIMIT 1", nativeQuery = true)
    List<Restaurant> findRandomByDistrictAndCategory(String district, String category);

    @Query("SELECT DISTINCT r.category FROM Restaurant r WHERE r.district = :district")
    List<String> findCategoriesByDistrict(JinjuDistrict district);

    boolean existsByTitleAndAddress(String title, String address);
}
