package br.com.lavarapido.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class ServiceType implements Serializable {
    private final int id;
    private String name;
    private String description;
    private BigDecimal price;
    private int durationMinutes;
    public ServiceType(int id, String name, String description, BigDecimal price, int durationMinutes) { this.id=id; this.name=name; this.description=description; this.price=price; this.durationMinutes=durationMinutes; }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public int getDurationMinutes() { return durationMinutes; }
    public void update(String name, String description, BigDecimal price, int durationMinutes) { this.name=name; this.description=description; this.price=price; this.durationMinutes=durationMinutes; }
    public String toString() { return name + " - R$ " + price; }
}
