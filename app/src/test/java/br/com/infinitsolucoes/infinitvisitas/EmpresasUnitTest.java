package br.com.infinitsolucoes.infinitvisitas;

import org.junit.Test;

import br.com.infinitsolucoes.infinitvisitas.Models.Empresas;
import br.com.infinitsolucoes.infinitvisitas.Models.Enderecos;

import static org.junit.Assert.assertNotNull;

public class EmpresasUnitTest {
    @Test
    public void EmpresasGetterAndSetter_test()
            throws Exception {
        Empresas empresas;
        Enderecos enderecos = new Enderecos("ASD", "sadfasdf", "ASDFASDF");
        empresas = new Empresas("ILIO", enderecos);

        assertNotNull(empresas.getNome());
        assertNotNull(empresas.getEndereco());
        assertNotNull(empresas.getTelefone());
        assertNotNull(empresas.getCelular());
        assertNotNull(empresas.getEmail());
        assertNotNull(empresas.getResponsavel());
    }
}
