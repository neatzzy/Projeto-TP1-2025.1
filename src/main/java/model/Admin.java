package model;

public class Admin extends Pessoa {
    private Liga liga;

    public Admin(String nome, String senha, Liga liga) {
        super(nome, senha);
        this.liga = liga;
    }
}
