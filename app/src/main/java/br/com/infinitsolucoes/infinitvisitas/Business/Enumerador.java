package br.com.infinitsolucoes.infinitvisitas.Business;

public final class Enumerador {
    private static Enumerador INSTANCE = new Enumerador();

    private Enumerador() {

    }

    public Enumerador getInstance() {
        if (INSTANCE != null)
            return INSTANCE;
        INSTANCE = new Enumerador();
        return INSTANCE;
    }

    public enum ResponseCode {
        ERROR(-1),
        UNSUCCESS(0),
        SUCCESS(1);

        private int responseCode;

        public int getResponseCode() {
            return responseCode;
        }

        public static ResponseCode getResponse(final boolean item) {
            return getResponse(item ? 1 : 0);
        }

        public static ResponseCode getResponse(final int item) {
            switch (item) {
                case -1:
                    return ERROR;
                case 0:
                    return UNSUCCESS;
                case 1:
                    return SUCCESS;
                default:
                    return ERROR;
            }
        }

        ResponseCode(int responseCode) {
            this.responseCode = responseCode;
        }
    }

    public enum OrdenacaoColunaFiltro {
        DATA_HORA(0), NIVEL_INTERESSE(1);
        private final int valor;

        OrdenacaoColunaFiltro(int valor) {
            this.valor = valor;
        }

        public int getValor() {
            return valor;
        }
    }

    public enum RamosAtividade {

        ALIMENTACAO(0, "Alimentação"),
        BEBIDAS(1, "Bebidas"),
        CALCADOS(2, "Calçados"),
        COMBUSTIVEIS(3, "Combustíveis"),
        EDUCACAO(4, "Educação"),
        FERRAGENS(5, "Ferragens"),
        SAUDE(6, "Saúde"),
        VESTUARIO(7, "Vestuário"),
        TRANSPORTE(8, "Transporte"),
        VEICULOS(9, "Veículos"),
        TODOS(10, "TODOS");

        private final int value;
        private final String description;

        RamosAtividade(int value, String description) {
            this.description = description;
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public String getDescription() {

            return description;
        }
    }
}

