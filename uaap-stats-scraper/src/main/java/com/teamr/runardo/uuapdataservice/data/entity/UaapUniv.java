package com.teamr.runardo.uuapdataservice.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "uaap_univ")
@Getter
@Setter
@NoArgsConstructor
public class UaapUniv {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "univ_code")
    private String univCode;

    @Column(name = "name_univ")
    private String univName;

}
