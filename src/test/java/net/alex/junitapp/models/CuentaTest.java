package net.alex.junitapp.models;

import net.alex.junitapp.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

// @TestInstance(TestInstance.Lifecycle.PER_CLASS) //No recomendado, ya que genera un estado.
public class CuentaTest {

    Cuenta cuenta;

    @BeforeEach
    public void initMetodoTest() {
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
    public void testNombreCuenta(TestInfo info, TestReporter reporter) {
        reporter.publishEntry("Ejecutado: " + info.getDisplayName() + " Método: " + Objects.requireNonNull(info.getTestMethod().orElse(null)).getName());
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

    @Nested
    class OperativeSystemTest {
        @Test
        @EnabledOnOs(OS.WINDOWS)
        public void testSoloWindows() {
        }

        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        public void testLinuxMac() {
        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        public void testNoWindows() {
        }
    }

    @Nested
    class JavaVersionTest {
        @Test
        @EnabledOnJre(JRE.JAVA_11)
        public void testOnlyJava11() {
        }

        @Test
        @EnabledOnJre(JRE.JAVA_18)
        public void testOnlyJava18() {
        }

    }

    @Nested
    class SystemPropertiesTest {
        @Test
        public void printSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach((k, v) -> System.out.println(k + ": " + v));
        }

        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = ".*18.*")
        public void testPropertyJavaVersion() {
        }

        @Test
        @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        public void testSolo62bits() {
        }

        @Test
        @EnabledIfSystemProperty(named = "app.environment", matches = "dev")
        public void testDevEnvironment() {
        }
    }

    @Nested
    class EnvironmentVariableTest {
        @Test
        void printEnvironmentVariables() {
            Map<String, String> variables = System.getenv();
            variables.forEach((k, v) -> System.out.println(k + ": " + v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "USER", matches = "alekz")
        public void testUser() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "PROD")
        public void testProduction() {
        }

        @Test
        @DisabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "PROD")
        public void testDevelopment() {
        }

        @Test
        @DisplayName("Prueba de saldo de cuenta DEV 1")
        public void testSaldoCuentaDev1() {
            boolean isDev = "dev".equals(System.getProperty("app.environment"));
            assumeTrue(isDev);
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
    }


    @Test
    @DisplayName("Prueba de saldo de cuenta DEV 2")
    public void testSaldoCuentaDev2() {
        boolean isDev = "dev".equals(System.getProperty("app.environment"));
        assumingThat(isDev, () -> {
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12, cuenta.getSaldo().doubleValue());
        });
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

    }

    @DisplayName("Prueba de débito repetida")
    @RepeatedTest(value = 4, name = "Repetición {currentRepetition} de {totalRepetitions}")
    public void testDebitoCuentaRepeat(RepetitionInfo info) {
        if (info.getCurrentRepetition() == 3) {
            System.out.println("ya casi terminan las pruebas");
        }
        cuenta.debito(new BigDecimal("100"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12", cuenta.getSaldo().toPlainString());
    }

    @Tag("param")
    @Nested
    class ParamTest {
        @ParameterizedTest(name = "Ejecutando repetición {index} con valor {0}")
        @ValueSource(strings = {"100", "200", "400", "800", "1000"})
        @DisplayName("Prueba parametrizada con valueSource")
        public void testDebitoCuentaValueSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "Ejecutando repetición {index} con valor {0} - {argumentsWithNames}")
        @CsvSource({"1,100", "2,200", "3,400", "4,800", "5,1000"})
        @DisplayName("Prueba parametrizada con csvSource1")
        public void testDebitoCuentaCsvSource1(String index, String value) {
            System.out.println(index + " -> " + value);
            cuenta.debito(new BigDecimal(value));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "Ejecutando repetición {index} con valor {0} - {argumentsWithNames}")
        @CsvSource({"200,100", "250,200", "499,400", "850,800", "1000,1000"})
        @DisplayName("Prueba parametrizada con csvSource2")
        public void testDebitoCuentaCsvSource2(String saldo, String value) {
            System.out.println(saldo + " -> " + value);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(value));
            assertNotNull(cuenta.getSaldo());
            //assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "Ejecutando repetición {index} con valor {0}")
        @CsvFileSource(resources = "/data.csv")
        @DisplayName("Prueba parametrizada con CsvFileSource")
        public void testDebitoCuentaCsvFileSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }


    }

    @Tag("param")
    @ParameterizedTest(name = "Ejecutando repetición {index} con valor {0}")
    @MethodSource("montoList")
    @DisplayName("Prueba parametrizada con MethodSource")
    public void testDebitoCuentaMethodSource(String monto) {
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    static List<String> montoList() {
        return Arrays.asList("100", "200", "400", "800", "1000");
    }

    @Nested
    @Tag("timeout")
    class TestTimeout {
        @Test
        @Timeout(6)
        void testTimeout1() throws InterruptedException {
            TimeUnit.SECONDS.sleep(2);
        }

        @Test
        @Timeout(value = 1100, unit = TimeUnit.MILLISECONDS)
        void testTimeout2() throws InterruptedException {
            TimeUnit.SECONDS.sleep(1);
        }

        @Test
        void testTimeout3() throws InterruptedException {
            assertTimeout(Duration.ofMillis(1700), () -> {
                TimeUnit.MILLISECONDS.sleep(1200);
            });
        }

    }


}









