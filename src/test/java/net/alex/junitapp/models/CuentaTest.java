package net.alex.junitapp.models;

import net.alex.junitapp.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class CuentaTest {

    @Test
    public void testNombreCuenta() {
        Cuenta cuenta = new Cuenta("Alejandro", new BigDecimal("1000.12"));
        //cuenta.setPersona("Alejandro");
        String valorEsperado = "Alejandro";
        String valorActual = cuenta.getPersona();
        assertNotNull(valorActual, () -> "La cuenta no puede ser nula");
        assertEquals(valorEsperado, valorActual, () -> "El valor esperado debe ser igual al actual");
    }

    @Test
    public void testSaldoCuenta() {
        Cuenta cuenta = new Cuenta("Alejandro", new BigDecimal("1000.12"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    public void testReferenciaCuenta() {
        Cuenta cuenta1 = new Cuenta("Alejandro", new BigDecimal("1000.12"));
        Cuenta cuenta2 = new Cuenta("Alejandro", new BigDecimal("1000.12"));
        //Hasta aquí se están comparando las zonas de memoria (referencia)
        //assertNotEquals(cuenta2, cuenta1);
    }

    @Test
    public void testValorCuenta() {
        Cuenta cuenta1 = new Cuenta("Alejandro", new BigDecimal("1000.12"));
        Cuenta cuenta2 = new Cuenta("Alejandro", new BigDecimal("1000.12"));
        /*  Para este punto ya se modificó la lógica del equals del objeto Cuenta
            para comparar por valor, es decir por el contenido del objeto
         */
        assertEquals(cuenta2, cuenta1);
    }


    @Test
    public void testDebitoCuenta() {
        Cuenta cuenta = new Cuenta("Alejandro", new BigDecimal("1000.12"));
        cuenta.debito(new BigDecimal("100"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12", cuenta.getSaldo().toPlainString());
    }

    @Test
    public void testCreditoCuenta() {
        Cuenta cuenta = new Cuenta("Alejandro", new BigDecimal("1000.12"));
        cuenta.credito(new BigDecimal("100"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.12", cuenta.getSaldo().toPlainString());
    }

    @Test
    public void testDineroInsuficienteException() {
        Cuenta cuenta = new Cuenta("Alejandro", new BigDecimal("1000.12"));
        Exception exception = assertThrows(DineroInsuficienteException.class,
                () -> cuenta.debito(new BigDecimal("1001")));
        String valorActual = exception.getMessage();
        String valorEsperado = "Dinero insuficiente";
        assertEquals(valorEsperado, valorActual);
    }

    @Test
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









