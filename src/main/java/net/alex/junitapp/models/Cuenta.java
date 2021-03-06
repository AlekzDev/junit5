package net.alex.junitapp.models;

import net.alex.junitapp.exceptions.DineroInsuficienteException;
import java.math.BigDecimal;

public class Cuenta {
    private String persona;
    private BigDecimal saldo;
    private Banco banco;

    public Cuenta(String persona, BigDecimal saldo) {
        this.persona = persona;
        this.saldo = saldo;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public void debito(BigDecimal monto){
        //BigDecimal es inmutable por lo que hay que asignarlo así
        BigDecimal nuevoSaldo = this.saldo.subtract(monto);
        if(nuevoSaldo.compareTo(BigDecimal.ZERO) < 0)
            throw new DineroInsuficienteException("Dinero insuficiente");
        this.saldo = nuevoSaldo;
    }

    public void credito(BigDecimal monto){
        //BigDecimal es inmutable por lo que hay que asignarlo así
        this.saldo = this.saldo.add(monto);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Cuenta c))
            return false;

        if(this.persona == null || this.saldo == null)
            return false;

        return (this.persona.equals(c.getPersona()) && this.saldo.equals(c.saldo));
    }
}
