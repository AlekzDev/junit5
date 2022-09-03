package net.alex.junitapp.models;

import net.alex.junitapp.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

// @TestInstance(TestInstance.Lifecycle.PER_CLASS) //No recomendado, ya que genera un estado.
public class CuentaTest {

    Cuenta cuenta;

    @BeforeEach
    public void initMetodoTest(){
        this.cuenta = new Cuenta("Alejandro", new BigDecimal("1000.12"));
        System.out.println("Iniciando método...");
    }

    @AfterEach
    void afterEach() {
        System.out.println("Finalizando método...");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando el test...");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el test...");
    }

    @Test
    @DisplayName("Prueba de nombre de cuenta")
    public void testNombreCuenta() {
        //cuenta.setPersona("Alejandro");
        String valorEsperado = "Alejandro";
        String valorActual = cuenta.getPersona();
        assertNotNull(valorActual, () -> "La cuenta no puede ser nula");
        assertEquals(valorEsperado, valorActual, () -> "El valor esperado debe ser igual al actual");
    }

    @Test
    @DisplayName("Prueba de saldo de cuenta")
    public void testSaldoCuenta() {
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Prueba de referencia entre cuentas")
    public void testReferenciaCuenta() {
        Cuenta cuenta1 = new Cuenta("Alejandro", new BigDecimal("1000.12"));
        Cuenta cuenta2 = new Cuenta("Alejandro", new BigDecimal("1000.12"));
        //Hasta aquí se están comparando las zonas de memoria (referencia)
        //assertNotEquals(cuenta2, cuenta1);
    }

    @Test
    @DisplayName("Prueba de valor de cuentas")
    public void testValorCuenta() {
        Cuenta cuenta1 = new Cuenta("Alejandro", new BigDecimal("1000.12"));
        Cuenta cuenta2 = new Cuenta("Alejandro", new BigDecimal("1000.12"));
        /*  Para este punto ya se modificó la lógica del equals del objeto Cuenta
            para comparar por valor, es decir por el contenido del objeto
         */
        assertEquals(cuenta2, cuenta1);
    }


    @Test
    @DisplayName("Prueba de débito")
    public void testDebitoCuenta() {
        cuenta.debito(new BigDecimal("100"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12", cuenta.getSaldo().toPlainString());
    }

    @Test
    @DisplayName("Prueba de crédito")
    public void testCreditoCuenta() {
        cuenta.credito(new BigDecimal("100"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.12", cuenta.getSaldo().toPlainString());
    }

    @Test
    @DisplayName("Prueba de saldo insuficiente")
    public void testDineroInsuficienteException() {
        Exception exception = assertThrows(DineroInsuficienteException.class,
                () -> cuenta.debito(new BigDecimal("1001")));
        String valorActual = exception.getMessage();
        String valorEsperado = "Dinero insuficiente";
        assertEquals(valorEsperado, valorActual);
    }

    @Test
    //@Disabled
    @DisplayName("Prueba de transferencia entre cuentas")
    public void testTransferirDineroCuentas() {
        Cuenta origen = new Cuenta("Ernesto", new BigDecimal("2500"));
        Cuenta destino = new Cuenta("Alejandro", new BigDecimal("1500.8989"));
        Banco banco = new Banco();
        banco.setNombre("Banco del estado");
        banco.transferir(origen, destino, new BigDecimal("500"));
        assertEquals("2000", origen.getSaldo().toPlainString());
        assertEquals("2000.8989", destino.getSaldo().toPlainString());
    }

    @Test
    @DisplayName("Prueba de relación de cuentas y banco")
    public void testRelacionBancoCuentas() {
        Cuenta cuenta1 = new Cuenta("Ernesto", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Alejandro", new BigDecimal("1500.8989"));
        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);
        banco.setNombre("Banco del estado");

        assertAll(
                () -> assertEquals(2, banco.getCuentas().size()),
                () -> assertEquals("Banco del estado", cuenta1.getBanco().getNombre()),
                () -> assertTrue(banco.getCuentas()
                        .stream()
                        .anyMatch(c -> c.getPersona().equals("Ernesto"))
                ),
                () -> assertEquals("Ernesto",
                        banco.getCuentas()
                                .stream()
                                .filter(c -> c.getPersona().equals("Ernesto"))
                                .findFirst()
                                .get()
                                .getPersona()
                )
        );
    }
}









