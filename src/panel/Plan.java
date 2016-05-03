/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panel;

import java.util.Vector;
/**
 *
 * @author debian
 */
public class Plan {
    private String name;
    private Vector<Befehl> befehle;
    
    public Plan (String _name) {
        this.name = _name;
        befehle = new Vector<>();
    }
    
    public void addBefehl (Befehl _befehl) {
        befehle.add(_befehl);
    }
    
    public String getName () {
        return name;
    }
    
    public int getSize () {
        return befehle.size();
    }
    
    public Befehl getBefehl (int pos) {
        return befehle.get(pos);
    }
}
