package com.deiz0n.makeorderapi.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "tb_mesa")
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;
    @Column(unique = true, nullable = false)
    private Integer numero;
    @Column(unique = true, nullable = false, length = 75)
    private String cliente;

    @JsonIgnore
    @OneToMany(mappedBy = "mesa", cascade = CascadeType.ALL)
    private List<Pedido> pedidos;
}
