package net.alex.junitapp.models;

import net.alex.junitapp.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    @Test
    void testNombreCuenta() {
        Cuenta cuenta = new Cuenta("Alejandro", new BigDecimal("1000.12"));
        //cuenta.setPersona("Alejandro");
        String valorEsperado = "Alejandro";
        String valorActual = cuenta.getPersona();
        assertNotNull(valorActual);
        assertEquals(valorEsperado, valorActual);
    }

    @Test
    void testSaldoCuenta(){
        Cuenta cuenta = new Cuenta("Alejandro", new BigDecimal("1000.12"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12,cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testReferenciaCuenta() {
        Cuenta cuenta1 = new Cuenta("Alejandro", new BigDecimal("1000.12"));
        Cuenta cuenta2 = new Cuenta("Alejandro", new BigDecimal("1000.12"));
        //Hasta aquí se están comparando las zonas de memoria (referencia)
        //assertNotEquals(cuenta2, cuenta1);
    }

    @Test
    void testValorCuenta() {
        Cuenta cuenta1 = new Cuenta("Alejandro", new BigDecimal("1000.12"));
        Cuenta cuenta2 = new Cuenta("Alejandro", new BigDecimal("1000.12"));
        /*  Para este punto ya se modificó la lógica del equals del objeto Cuenta
            para comparar por valor, es decir por el contenido del objeto
         */
        assertEquals(cuenta2, cuenta1);
    }


    @Test
    void testDebitoCuenta() {
        Cuenta cuenta = new Cuenta("Alejandro", new BigDecimal("1000.12"));
        cuenta.debito(new BigDecimal("100"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900,cuenta.getSaldo().intValue());
        assertEquals("900.12",cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditoCuenta() {
        Cuenta cuenta = new Cuenta("Alejandro", new BigDecimal("1000.12"));
        cuenta.credito(new BigDecimal("100"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100,cuenta.getSaldo().intValue());
        assertEquals("1100.12",cuenta.getSaldo().toPlainString());
    }

    @Test
    void testDineroInsuficienteException() {
        Cuenta cuenta = new Cuenta("Alejandro", new BigDecimal("1000.12"));
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal("1001"));
        });
        String valorActual = exception.getMessage();
        String valorEsperado = "Dinero insuficiente";
        assertEquals(valorEsperado, valorActual);
    }
}