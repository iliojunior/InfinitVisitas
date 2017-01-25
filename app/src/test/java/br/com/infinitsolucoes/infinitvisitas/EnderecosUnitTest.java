package br.com.infinitsolucoes.infinitvisitas;

import org.junit.Test;

import br.com.infinitsolucoes.infinitvisitas.Models.Enderecos;

import static org.junit.Assert.assertNotNull;

public class EnderecosUnitTest {
    @Test
    public void testReturnStrings_isNotNull() throws Exception {
        Enderecos enderecos = new Enderecos("Rua Angelo Polo", "Paiçandu", "PR");
        assertNotNull(enderecos);
        assertNotNull(enderecos.getLogradouro());
        assertNotNull(enderecos.getUf());
        assertNotNull(enderecos.getMunicipio());
        assertNotNull(enderecos.getNumero());
    }
}
