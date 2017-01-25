package br.com.infinitsolucoes.infinitvisitas.Models;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

import br.com.infinitsolucoes.infinitvisitas.Interfaces.Column;
import br.com.infinitsolucoes.infinitvisitas.Interfaces.Id;
import br.com.infinitsolucoes.infinitvisitas.Interfaces.Models;

public class Enderecos implements Models {
    @Id
    @Column
    private final int _enderecoId;
    @NotNull
    @Column
    private String logradouro = "";
    @NotNull
    @Column
    private String numero = "";
    @NotNull
    @Column
    private String municipio = "";
    @NotNull
    @Column
    private String uf = "";
    @NotNull
    @Column
    private String bairro = "";

    public Enderecos(int _enderecoId, @NotNull String logradouro, @NotNull String municipio, @NotNull String uf) {
        this._enderecoId = _enderecoId;
        setLogradouro(logradouro);
        setMunicipio(municipio);
        setUf(uf);
    }

    public Enderecos(@NotNull String logradouro, @NotNull String municipio, @NotNull String uf) {
        this._enderecoId = 0;
        setLogradouro(logradouro);
        setMunicipio(municipio);
        setUf(uf);
    }

    @NotNull
    public String getUf() {
        return uf;
    }

    public void setUf(@NotNull String uf) {
        if (uf == null)
            throw new NullPointerException("UF não pode ser nulo!");
        this.uf = uf;
    }

    @NotNull
    public String getNumero() {

        return numero;
    }

    public void setNumero(@NotNull String numero) {
        if (numero == null)
            throw new NullPointerException("Numero não pode ser nulo!");
        this.numero = numero;
    }

    @NotNull
    public String getBairro() {
        return bairro;
    }

    @NotNull
    public String getMunicipio() {

        return municipio;
    }

    public void setMunicipio(@NotNull String municipio) {
        if (municipio == null)
            throw new NullPointerException("Municipio não pode ser nulo!");
        this.municipio = municipio;
    }

    @NotNull("")
    public String getLogradouro() {

        return logradouro;
    }


    public void setBairro(@NotNull String bairro) {
        if (bairro == null)
            throw new NullPointerException("Bairro não pode ser Nulo!");
        this.bairro = bairro;
    }

    public void setLogradouro(@NotNull String logradouro) {
        if (logradouro == null)
            throw new NullPointerException("Logradouro não pode ser nulo!");
        this.logradouro = logradouro;
    }


    public int get_enderecoId() {
        return _enderecoId;
    }

    public String getLogradouroCompleto() {
        if (this.getNumero().isEmpty())
            return this.getLogradouro();
        return this.getLogradouro() + ", " + this.getNumero();
    }

    @Override
    public Field getIdColumn() {
        Field idField = null;
        try {
            idField = this.getClass().getField("_enderecoId");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return idField;
    }
}
