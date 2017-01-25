package br.com.infinitsolucoes.infinitvisitas.CrudTest;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Business.AnnotationUtils;
import br.com.infinitsolucoes.infinitvisitas.Models.ColumnsDB;
import br.com.infinitsolucoes.infinitvisitas.Models.Enderecos;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class Endereco {
    @Test
    public void Test01() throws Exception {
        List<ColumnsDB> columnsDBList = AnnotationUtils.getColumns(Enderecos.class);

        assertFalse(columnsDBList.isEmpty());
        assertNotNull(columnsDBList);
        assertTrue(columnsDBList.size() > 0);
    }
}
