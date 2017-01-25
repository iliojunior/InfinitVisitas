package br.com.infinitsolucoes.infinitvisitas.CrudTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Business.AnnotationUtils;
import br.com.infinitsolucoes.infinitvisitas.Models.ColumnsDB;
import br.com.infinitsolucoes.infinitvisitas.Models.Empresas;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(JUnit4.class)
public class Empresa {
    @Test
    public void Test01() throws Exception {
        List<ColumnsDB> columnsDBList = AnnotationUtils.getColumns(Empresas.class);

        assertFalse(columnsDBList.isEmpty());
        assertNotNull(columnsDBList);
        assertTrue(columnsDBList.size() > 0);
    }
}
