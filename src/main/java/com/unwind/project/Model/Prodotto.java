package com.unwind.project.Model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "prodotti")
@Data
public class Prodotto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String categoria; // 'cartine', 'filtri', 'accessori'

    @Column(nullable = false)
    private String titolo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descrizione;

    @Column(nullable = false)
    private BigDecimal prezzo;

    private String foto;
}