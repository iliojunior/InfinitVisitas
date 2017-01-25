package br.com.infinitsolucoes.infinitvisitas.Business;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Interfaces.Column;
import br.com.infinitsolucoes.infinitvisitas.Models.ColumnsDB;
import br.com.infinitsolucoes.infinitvisitas.Interfaces.Id;

public class AnnotationUtils {
    private AnnotationUtils() {

    }

    public static List<ColumnsDB> getColumns(Class<?> aClass) throws Exception {
        List<ColumnsDB> columnsDBs = new ArrayList<>();
        for (Field field : aClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                ColumnsDB columnsDB = new ColumnsDB(column.name().isEmpty() ? field.getName() : column.name(), column.type());
                if (column.type().equals("text") || !field.getType().isAssignableFrom(String.class))
                    columnsDB.setType(convertFieldFromDbType(field));
                if (field.isAnnotationPresent(Id.class)) {
                    columnsDB.setPrimaryKey(true);
                    columnsDB.setAutoIncrement(true);
                    columnsDB.setIdentity(true);
                }
                columnsDBs.add(columnsDB);
            }
        }
        Collection<ColumnsDB> columnsDBCollection = filter(columnsDBs, type -> (type.isAutoIncrement() && type.isPrimaryKey() && type.isIdentity()));

        if (columnsDBs.isEmpty()) {
            throw new Exception("Não foi definido nenhuma coluna para o banco");
        }
        if (columnsDBCollection.isEmpty()) {
            throw new Exception("Não foi definida nenhuma coluna como Chave Primária");
        }
        return columnsDBs;
    }

    public static String convertFieldFromDbType(Field field) {
        if (field.getType().isAssignableFrom(int.class)
                || field.getType().isAssignableFrom(boolean.class)) {
            return "integer";
        } else if (field.getType().isAssignableFrom(double.class)
                || field.getType().isAssignableFrom(float.class)) {
            return "real";
        }

        return "text";
    }

    public interface Predicate<T> {
        boolean apply(T type);
    }

    public static <T> Collection<T> filter(Collection<T> collection, Predicate<T> predicate) {
        Collection<T> result = new ArrayList<T>();
        for (T element : collection) {
            if (predicate.apply(element))
                result.add(element);
        }
        return result;
    }
}
