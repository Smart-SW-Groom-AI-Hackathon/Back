package com.goorm.team3.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "restaurants")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String category;

    @Column(length = 500)
    private String address;

    private String roadAddress;

    private String telephone;

    @Column(length = 1000)
    private String link;

    @Enumerated(EnumType.STRING)
    private JinjuDistrict district;

    private Integer mapx;

    private Integer mapy;
}
