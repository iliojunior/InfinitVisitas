package br.com.infinitsolucoes.infinitvisitas.Models;

import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.text.Collator;
import java.util.Comparator;

import br.com.infinitsolucoes.infinitvisitas.Interfaces.Column;
import br.com.infinitsolucoes.infinitvisitas.Interfaces.Id;
import br.com.infinitsolucoes.infinitvisitas.Interfaces.Models;
import br.com.infinitsolucoes.infinitvisitas.Utils.MaskUtil;

public class Empresas implements Models {
    @Id
    @Column
    private final int _empresaId;
    @NotNull
    @Column
    private String nome = "";
    @NotNull
    @Column
    private String telefone = "";
    @NotNull
    @Column
    private String cpf_cnpj = "";
    @NotNull
    @Column
    private String celular = "";
    @NotNull
    @Column
    private String responsavel = "";
    @NotNull
    @Column
    private String email = "";
    @Column
    private int enderecoId;
    @Column
    private int nivelInteresse;
    @NotNull
    private Enderecos endereco;

    public int getNivelInteresse() {
        return nivelInteresse;
    }

    public void setNivelInteresse(int nivelInteresse) {
        this.nivelInteresse = nivelInteresse;
    }

    public static Comparator<Empresas> COMPARATOR_QUALIFICADOS = (o1, o2) -> {
        final Collator collator = Collator.getInstance();
        if (o1.getNivelInteresse() > o2.getNivelInteresse())
            return -1;
        else if (o1.getNivelInteresse() == o2.getNivelInteresse()) {
            if (o1.get_empresaId() > o2.get_empresaId())
                return -1;
            else if (o1.get_empresaId() == o2.get_empresaId()) {
                collator.compare(o1.getNome(), o2.getNome());
            }
            return 0;
        }
        return 1;
    };

    public static Comparator<Empresas> COMPARATOR_LEADS = (o1, o2) -> {
        final Collator collator = Collator.getInstance();
        if (o1.get_empresaId() > -o2.get_empresaId())
            return -1;
        else
            return collator.compare(o1.getNome(), o2.getNome());
    };

    public static Comparator<Empresas> SORT_ALPHABETICALLY = (o1, o2) -> {
        final Collator collator = Collator.getInstance();
        return collator.compare(o1.getNome(), o2.getNome());
    };


    public int getEnderecoId() {
        return getEndereco().get_enderecoId();
    }

    public Empresas(int _empresaId, @NonNull String nome, @NonNull Enderecos endereco) {
        this._empresaId = _empresaId;
        setNome(nome);
        setEndereco(endereco);
    }

    public Empresas(@NonNull String nome, @NonNull Enderecos endereco) {
        this._empresaId = 0;
        setNome(nome);
        setEndereco(endereco);
    }


    public int get_empresaId() {
        return _empresaId;
    }

    @NotNull
    public String getTelefoneWithMask() {
        final String formatDDD = "(%s) %s-%s";
        final String format = "%s-%s";
        switch (getTelefone().length()) {
            case 8:
                return String.format(format, getTelefone().substring(0, 4), getTelefone().substring(4, 8));
            case 9:
                return String.format(format, getTelefone().substring(0, 5), getTelefone().substring(5, 9));
            case 10:
                return String.format(formatDDD, getTelefone().substring(0, 2), getTelefone().substring(2, 6), getTelefone().substring(6, 10));
            case 11:
                return String.format(formatDDD, getTelefone().substring(0, 2), getTelefone().substring(2, 7), getTelefone().substring(7, 11));
        }
        return getTelefone();
    }

    @NotNull
    public String getTelefoneUnMasked() {
        return MaskUtil.unMask(getTelefone());
    }

    @NotNull
    public String getTelefone() {
        if (telefone != null && !telefone.isEmpty())
            return telefone;
        
        return getCelular();
    }

    public void setTelefone(@NotNull String telefone) {
        if (telefone == null)
            throw new NullPointerException("Telefone não pode ser Nulo!");
        this.telefone = telefone;
    }

    @NotNull
    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(@NotNull String responsavel) {
        if (responsavel == null)
            throw new NullPointerException("Responsável não pode ser Nulo!");
        this.responsavel = responsavel;
    }

    @NotNull
    public String getNome() {

        return nome;
    }

    public void setNome(@NotNull String nome) {
        if (nome == null)
            throw new NullPointerException("Nome não pode ser Nulo!");
        this.nome = nome;
    }

    @NotNull
    public Enderecos getEndereco() {

        return endereco;
    }

    public void setEndereco(@NotNull Enderecos endereco) {
        if (endereco == null)
            throw new NullPointerException("Endereço não pode ser Nulo!");
        this.endereco = endereco;
    }

    @NotNull
    public String getEmail() {

        return email;
    }

    public void setEmail(@NotNull String email) {
        if (email == null)
            throw new NullPointerException("Email não pode ser Nulo!");
        this.email = email;
    }

    @NotNull
    public String getCelular() {
        return celular;
    }

    public void setCelular(@NotNull String celular) {
        if (celular == null)
            throw new NullPointerException("Celular não pode ser Nulo!");
        this.celular = celular;
    }

    public String getCpf_cnpjWithMask() {
        final String cpfMask = "%s.%s.%s-%s";
        final String cnpjMask = "%s.%s.%s/%s-%s";
        switch (getCpf_cnpj().length()) {
            case 14:
                return String.format(cnpjMask,
                        getCpf_cnpj().substring(0, 2),
                        getCpf_cnpj().substring(2, 5),
                        getCpf_cnpj().substring(5, 8),
                        getCpf_cnpj().substring(8, 12),
                        getCpf_cnpj().substring(12, 14));
            case 11:
                return String.format(cpfMask,
                        getCpf_cnpj().substring(0, 3),
                        getCpf_cnpj().substring(3, 6),
                        getCpf_cnpj().substring(6, 9),
                        getCpf_cnpj().substring(9, 11));
            default:
                return getCpf_cnpj();
        }
    }

    @NotNull
    public String getCpf_cnpj() {
        return cpf_cnpj;
    }

    public void setCpf_cnpj(@NotNull String cpf_cnpj) {
        this.cpf_cnpj = cpf_cnpj;
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
