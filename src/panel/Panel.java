/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panel;


import java.io.*;
import java.net.*;
import java.util.Vector;


/**
 *
 * @author debian
 */
public class Panel {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Vector<Befehl> Auftrag1 = new Vector<Befehl>();
//            Vector<Befehl> Auftrag2 = new Vector<Befehl>();
//            Vector<Befehl> Auftrag3 = new Vector<Befehl>();
            String bufferPrint;
            String bufferMat;
            String line;
            boolean verbunden = true;
            
            /*
            Hier Auftrag einpflegen
            */
            Auftrag1.add(new Befehl(1, 0 ,1 , "gruen"));
            Auftrag1.add(new Befehl(1, 0 ,2 , "gruen"));
            Auftrag1.add(new Befehl(1, 0 ,3 , "gruen"));
            Auftrag1.add(new Befehl(2, 0 ,1 , "gruen"));
            Auftrag1.add(new Befehl(2, 0 ,2 , "gruen"));
            Auftrag1.add(new Befehl(2, 0 ,3 , "gruen"));
            Auftrag1.add(new Befehl(3, 0 ,1 , "gruen"));
            Auftrag1.add(new Befehl(3, 0 ,2 , "gruen"));
            Auftrag1.add(new Befehl(3, 0 ,3 , "gruen"));
            Auftrag1.add(new Befehl(1, 1 ,1 , "gelb"));
            Auftrag1.add(new Befehl(1, 1 ,2 , "gelb"));
            Auftrag1.add(new Befehl(2, 1 ,1 , "gelb"));
            Auftrag1.add(new Befehl(2, 1 ,2 , "gelb"));
            Auftrag1.add(new Befehl(1, 2 ,1 , "rot"));
            
            Socket matSocket = new Socket("localhost",9999);
            Socket printSocket = new Socket("localhost",9998);
            
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            DataOutputStream toMat = new DataOutputStream(matSocket.getOutputStream());
            BufferedReader fromMat = new BufferedReader(new InputStreamReader(matSocket.getInputStream()));
            DataOutputStream toPrint = new DataOutputStream(printSocket.getOutputStream());
            BufferedReader fromPrint = new BufferedReader(new InputStreamReader(printSocket.getInputStream()));
            
            int vecID = 0;
            
            while (verbunden){
                //Druckauftrag
                toPrint.writeBytes(Auftrag1.elementAt(vecID).getX() + "," + Auftrag1.elementAt(vecID).getY() + "," + Auftrag1.elementAt(vecID).getZ() + "," + Auftrag1.elementAt(vecID).getFarbe() + '\n');
                //Antwort des Druckkopfes
                bufferPrint = fromPrint.readLine();
                //Druck erfolgreich
                if (bufferPrint.equals("erfolgreich")) {
                    System.out.println("Befehl erfolgreich" + '\n');
                    toMat.writeBytes(Auftrag1.elementAt(vecID).getFarbe() + '\n');
                    bufferMat = fromMat.readLine();
                    if (bufferMat.equals("erfolgreich")) {
                        System.out.println("Material " + Auftrag1.elementAt(vecID).getFarbe() + " verringert." + '\n');
                    }
                    else if (bufferMat.equals("leer")) {
                        System.out.println("Material " + Auftrag1.elementAt(vecID).getFarbe() + " leer." + '\n');
                        System.out.println("Auffuellen mit ENTER bestaetigen!" + '\n');
                        line = stdIn.readLine();
                        toMat.writeBytes(Auftrag1.elementAt(vecID).getFarbe() + "auffuellen" + '\n');
                    }
                }
                //Druckkopf hat Fehler
                else {
                    System.out.println();
                }
            } // end while verbunden
            fromMat.close();
            matSocket.close();
            printSocket.close();
        }
        catch(IOException e) {
            
        }
    }
    
}
