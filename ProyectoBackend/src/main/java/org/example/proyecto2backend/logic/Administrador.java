package org.example.proyecto2backend.logic;

import org.hibernate.annotations.OnDeleteAction;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.OnDelete;
import jakarta.persistence.*;

@Entity
@Table(name = "administrador")
public class Administrador {
    @Id
    @Size(max = 100)
    @Column(name = "id", nullable = false, length = 100)
    private String id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id", nullable = false)
    private Usuario usuario;

    public String getId() {
        return id;
    }
    public Usuario getUsuario() {
        return usuario;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}