package br.com.lavarapido.model;

import java.io.Serializable;

public class Customer implements Serializable {
    private final int id;
    private String name;
    private String cpf;
    private String phone;
    private String email;
    private String plate;
    private String vehicle;

    public Customer(int id, String name, String cpf, String phone, String email, String plate, String vehicle) {
        this.id = id; this.name = name; this.cpf = cpf; this.phone = phone; this.email = email; this.plate = plate; this.vehicle = vehicle;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCpf() { return cpf; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getPlate() { return plate; }
    public String getVehicle() { return vehicle; }
    public void update(String name, String cpf, String phone, String email, String plate, String vehicle) { this.name=name; this.cpf=cpf; this.phone=phone; this.email=email; this.plate=plate; this.vehicle=vehicle; }
    public String toString() { return name + " - " + plate; }
}
