package com.example.labo;

public class Producto {
    private int id;
    private String codigo;
    private String nombre;
    private String marca;
    private String talla;
    private double precio;
    private double costo;
    private int stock;
    private String descripcion;
    private String fotoPath;
    private String couchId;
    private boolean sincronizado;

    public Producto() {}

    public Producto(String codigo, String nombre, String marca,
                    String talla, double precio, double costo, int stock,
                    String descripcion, String fotoPath) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.marca = marca;
        this.talla = talla;
        this.precio = precio;
        this.costo = costo;
        this.stock = stock;
        this.descripcion = descripcion;
        this.fotoPath = fotoPath;
        this.couchId = "";
        this.sincronizado = false;
    }

    // Calcula el porcentaje de ganancia
    public double getGanancia() {
        if (costo <= 0) return 0;
        return ((precio - costo) / costo) * 100;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getTalla() { return talla; }
    public void setTalla(String talla) { this.talla = talla; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getFotoPath() { return fotoPath; }
    public void setFotoPath(String fotoPath) { this.fotoPath = fotoPath; }
    public String getCouchId() { return couchId; }
    public void setCouchId(String couchId) { this.couchId = couchId; }
    public boolean isSincronizado() { return sincronizado; }
    public void setSincronizado(boolean sincronizado) { this.sincronizado = sincronizado; }
}