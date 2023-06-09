package com.wrmanager.wrmanagerfx.entities;


import lombok.*;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Stock {


    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @Basic(optional = false)
    @Column(nullable = false)
    private String lot;


    @Builder.Default
    @Basic(optional = false)
    private Timestamp creeLe  = Timestamp.valueOf(LocalDateTime.now());;



    @Basic(optional = false)
    @Column(nullable = false)
    private Float ppa;



    @Basic(optional = false)
    @Column(nullable = false)
    private Date expirationDate;



    private String fournisseur;


    private Integer qty = 0;


    @Transient
    private Integer qtyEnCaisse = 0;


    @ManyToOne
    @JoinColumn(name = "produit_id")
    private Produit produit;


    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", lot='" + lot + '\'' +
                ", ppa=" + ppa +
                ", expirationDate=" + expirationDate +
                ", fournisseur='" + fournisseur + '\'' +
                ", qty=" + qty +
                ", produit=" + produit +
                '}';
    }
}
