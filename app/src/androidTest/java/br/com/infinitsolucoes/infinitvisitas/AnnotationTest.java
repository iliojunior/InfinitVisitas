package br.com.infinitsolucoes.infinitvisitas;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Business.AnnotationUtils;
import br.com.infinitsolucoes.infinitvisitas.Interfaces.Column;
import br.com.infinitsolucoes.infinitvisitas.Models.ColumnsDB;
import br.com.infinitsolucoes.infinitvisitas.Interfaces.Id;

@RunWith(AndroidJUnit4.class)
public class AnnotationTest {
    private class MyModel {
        @Id
        @Column
        private int _testeId;
        @Column
        private String testeString;
        @Column
        private double testeDouble;
        @Column
        private float testeFloat;
        @Column
        private boolean testeBoolean;
    }

    @Test
    public void Test01_Annotation() throws Exception {
        MyModel modelo = new MyModel();
        List<ColumnsDB> fieldList = AnnotationUtils.getColumns(modelo.getClass());
        Assert.assertTrue(!fieldList.isEmpty());
    }
}
