package br.com.infinitsolucoes.infinitvisitas.Models;

import org.jetbrains.annotations.NotNull;

public class ColumnsDB {
    @NotNull
    private String name;
    @NotNull
    private String type;
    @NotNull
    private boolean isIdentity = false;
    @NotNull
    private boolean isPrimaryKey = false;
    @NotNull
    private boolean isAutoIncrement = false;

    public ColumnsDB(@NotNull String name, @NotNull String type) {
        setName(name);
        setType(type);
    }

    public ColumnsDB(@NotNull String name
            , @NotNull String type
            , @NotNull boolean isAutoIncrement
            , @NotNull boolean isIdentity
            , @NotNull boolean isPrimaryKey) {
        setName(name);
        setType(type);
        setAutoIncrement(isAutoIncrement);
        setPrimaryKey(isPrimaryKey);
        setIdentity(isIdentity);
    }

    @NotNull
    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(@NotNull boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    @NotNull
    public boolean isAutoIncrement() {

        return isAutoIncrement;
    }

    public void setAutoIncrement(@NotNull boolean autoIncrement) {
        isAutoIncrement = autoIncrement;
    }

    @NotNull
    public boolean isIdentity() {
        return isIdentity;
    }

    public void setIdentity(@NotNull boolean identity) {
        isIdentity = identity;
    }

    @NotNull
    public String getType() {
        return type;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public void setType(@NotNull String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        String retorno = getName() + " " + getType();
        if (isPrimaryKey())
            retorno = retorno.concat(" primary key");
        if (isAutoIncrement())
            retorno = retorno.concat(" autoincrement");
        return retorno;
    }
}
