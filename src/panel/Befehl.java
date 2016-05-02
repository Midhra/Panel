/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panel;

/**
 *
 * @author debian
 */
public class Befehl {
    private int x;
    private int y;
    private int z;
    private String farbe;
    
    public Befehl() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.farbe = "gruen";
    }
    
    public Befehl(int _x, int _y, int _z, String _farbe) {
        this.x = _x;
        this.y = _y;
        this.z = _z;
        this.farbe = _farbe;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getZ() {
        return z;
    }
    
    public String getFarbe() {
        return farbe;
    }
    
    public void setX (int _x) {
        this.x = _x;
    }
    
    public void setY (int _y) {
        this.y = _y;
    }
    
    public void setZ (int _z) {
        this.z = _z;
    }
    
    public void setFarbe (String _farbe) {
        this.farbe = _farbe;
    }
}
