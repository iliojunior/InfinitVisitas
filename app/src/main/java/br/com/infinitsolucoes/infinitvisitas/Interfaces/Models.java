package br.com.infinitsolucoes.infinitvisitas.Interfaces;

import java.lang.reflect.Field;

public interface Models {
    Field getIdColumn() throws NoSuchFieldException;
}
