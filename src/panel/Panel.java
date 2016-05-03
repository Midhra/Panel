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
    static int gruenMat = 0;
    static int rotMat = 0;
    static int gelbMat = 0;
    static int gruenAuftrag = 0;
    static int rotAuftrag = 0;
    static int gelbAuftrag = 0;
    static Vector<Plan> Auftraege = new Vector<>();
    static Plan akt;
    static DruckkopfThread druckkopf;
    static MaterialThread material;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        material = new MaterialThread(9999);
        material.start();
        druckkopf = new DruckkopfThread(9998);
        druckkopf.start();
        
        populateVector();
        
        while (waehlenPlan()) {
            countFarben();
            print();
        }
        
    }
    
    public static void populateVector() {
        Auftraege.clear();
        Auftraege.add(new Plan("Muster"));
        Auftraege.get(0).addBefehl(new Befehl(1, 0 ,1 , "gruen"));
        Auftraege.get(0).addBefehl(new Befehl(1, 0 ,2 , "gruen"));
        Auftraege.get(0).addBefehl(new Befehl(1, 0 ,3 , "gruen"));
        Auftraege.get(0).addBefehl(new Befehl(2, 0 ,1 , "gruen"));
        Auftraege.get(0).addBefehl(new Befehl(2, 0 ,2 , "gruen"));
        Auftraege.get(0).addBefehl(new Befehl(2, 0 ,3 , "gruen"));
        Auftraege.get(0).addBefehl(new Befehl(3, 0 ,1 , "gruen"));
        Auftraege.get(0).addBefehl(new Befehl(3, 0 ,2 , "gruen"));
        Auftraege.get(0).addBefehl(new Befehl(3, 0 ,3 , "gruen"));
        Auftraege.get(0).addBefehl(new Befehl(1, 1 ,1 , "gelb"));
        Auftraege.get(0).addBefehl(new Befehl(1, 1 ,2 , "gelb"));
        Auftraege.get(0).addBefehl(new Befehl(2, 1 ,1 , "gelb"));
        Auftraege.get(0).addBefehl(new Befehl(2, 1 ,2 , "gelb"));
        Auftraege.get(0).addBefehl(new Befehl(1, 2 ,1 , "rot"));
        
        System.out.println("Vector filled" + '\n');
    }
    
    public static void countFarben() {
        gruenAuftrag = 0;
        rotAuftrag = 0;
        gelbAuftrag = 0;
        
        for (int tmp = 0; tmp < akt.getSize(); tmp++) {
            if (akt.getBefehl(tmp).getFarbe().equals("gruen")) {
                gruenAuftrag++;
            }
            if (akt.getBefehl(tmp).getFarbe().equals("rot")) {
                rotAuftrag++;
            }
            if (akt.getBefehl(tmp).getFarbe().equals("gelb")) {
                gelbAuftrag++;
            }
        }
        
        System.out.println("gruen: " + gruenAuftrag + " rot: " + rotAuftrag + " gelb: " + gelbAuftrag + '\n');
    }
    
    public static void infoAnzeige() {
        String auftrag = "Muster";
        System.out.println("-------------------------------------------------------------------------------");
        System.out.println("Grün:\t" + gruenMat + " Einheiten\t\tAuftrag:");
        System.out.println("Rot:\t" + rotMat + "Einheiten\t\t" + auftrag);
        System.out.println("Gelb:\t" + gelbMat + "Einheiten");
    }
    
    public static boolean waehlenPlan () {
        int index = -1;
        System.out.println("Bitte waehlen Sie einen Plan.");
        for (int tmp = 0; tmp < Auftraege.size(); tmp++) {
            System.out.println("[" + tmp + "]" + Auftraege.get(tmp).getName());
        }
        try {
            BufferedReader br = new BufferedReader (new InputStreamReader (System.in));
            while (index < 0 || index > Auftraege.size()) {
                String s = br.readLine();
                index = Integer.parseInt(s);
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage() + '\n');
        }
        
        akt = Auftraege.get(index);
        return true;
    }
    
    public static void print () {
        String rueckMat = material.checkMat();
        while (!(rueckMat.equals("OK"))) {
            userError("Die Patrone " + rueckMat + " reicht fuer den Druckauftrag nicht aus.", "Druecken Sie ENTER um einen Patronenwechsel zu simulieren");
            material.sendRefill(rueckMat);
            rueckMat = material.checkMat();
        }
        for (int tmp = 0; tmp < akt.getSize(); tmp++) {
            if (!(sendPrint(akt.getBefehl(tmp)))) {
                //Fehler beim Druck, daher muss der Befehl wiederholt werden
                tmp--;
            }
        }
        akt = null;
        infoAnzeige();
    }
    
    public static boolean sendPrint (Befehl tmp) {
        String befehl = tmp.getX() + "," + tmp.getY() + "," + tmp.getZ() + "," + tmp.getFarbe() + '\n';
        int rueckPrint = druckkopf.sendBefehl(befehl);
        
        switch (rueckPrint) {
            case -1:
                userError("Der Druckkopf ist verschmutzt.", "Druecken Sie ENTER um eine Reinigung zu simulieren.");
                return false;
            default:
                break;
        }
        
        material.sendUsed(tmp.getFarbe());
        infoAnzeige();
        return true;
    }
    
    public static void userError (String error, String loesung) {
        try {
            System.out.println("\n\n\n\tERROR:\t" + error);
            System.out.println("\n\t" + loesung);
            
            BufferedReader reader = new BufferedReader (new InputStreamReader (System.in));
            String input = reader.readLine();
        }
        catch (IOException e) {
            System.out.println(e.getMessage() + '\n');
        }
    }
    
    private static class DruckkopfThread extends Thread {
        ServerSocket welcomeSocket;
        Socket connectionSocket;
        
        public DruckkopfThread (int port) {
            try {
                this.welcomeSocket = new ServerSocket(port);
                this.connectionSocket = welcomeSocket.accept();
            }
            catch (IOException e) {
                System.out.println(e.getMessage() + '\n');
            }
        }
        
        public int sendBefehl (String Befehl) {
            try {
                BufferedReader fromPrint = new BufferedReader (new InputStreamReader (connectionSocket.getInputStream()));
                DataOutputStream toPrint = new DataOutputStream (connectionSocket.getOutputStream());
                toPrint.writeBytes(Befehl);
                String antwort = fromPrint.readLine();
                int code = Integer.parseInt(antwort);
                return code;
            }
            catch (IOException e) {
                System.out.println(e.getMessage() + '\n');
                return -3;
            }
        }
    }
    
    private static class MaterialThread extends Thread {
        ServerSocket welcomeSocket;
        Socket connectionSocket;
        
        public MaterialThread (int port) {
            try {
                this.welcomeSocket = new ServerSocket(port);
                this.connectionSocket = welcomeSocket.accept();
            }
            catch (IOException e) {
                System.out.println(e.getMessage() + '\n');
            }
        }
        
        public int sendRefill (String farbe) {
            try {
                BufferedReader fromMat = new BufferedReader (new InputStreamReader (connectionSocket.getInputStream()));
                DataOutputStream toMat = new DataOutputStream (connectionSocket.getOutputStream());
                
                String nachricht = farbe + " auffuellen" + '\n';
                toMat.writeBytes(nachricht);
                String antwort = fromMat.readLine();
                if (antwort.equals("erfolgreich")) {
                    return 0;
                }
                return 1;
            }
            catch (IOException e) {
                System.out.println(e.getMessage() + '\n');
                return -1;
            }
        }
        
        public int sendUsed (String farbe) {
            try {
                BufferedReader fromMat = new BufferedReader (new InputStreamReader (connectionSocket.getInputStream()));
                DataOutputStream toMat = new DataOutputStream (connectionSocket.getOutputStream());
                
                String nachricht = farbe + '\n';
                toMat.writeBytes(nachricht);
                String antwort = fromMat.readLine();
                if (antwort.equals("erfolgreich")) {
                    return 0;
                }
                return 1;
            }
            catch (IOException e) {
                System.out.println(e.getMessage() + '\n');
                return -1;
            }
        }
        
        public String checkMat () {
            try {
                BufferedReader fromMat = new BufferedReader (new InputStreamReader (connectionSocket.getInputStream()));
                DataOutputStream toMat = new DataOutputStream (connectionSocket.getOutputStream());
                
                String nachricht = "check" + '\n';
                toMat.writeBytes(nachricht);
                
                String antwort = fromMat.readLine();
                String fuellen = "OK";
                /*
                **[0] gruen
                **[1] rot
                **[2] gelb
                */
                String [] parameter = antwort.split(",");
                gruenMat = Integer.parseInt(parameter[0]);
                rotMat = Integer.parseInt(parameter[1]);
                gelbMat = Integer.parseInt(parameter[2]);
                
                if (gruenMat < gruenAuftrag) {
                    fuellen = "gruen";
                }
                if (rotMat < rotAuftrag) {
                    fuellen = "rot";
                }
                if (gelbMat < gelbAuftrag) {
                    fuellen = "gelb";
                }
                return fuellen;
            }
            catch (IOException e) {
                System.out.println(e.getMessage() + '\n');
                return "error";
            }
        }
        
        public void getMat () {try {
                BufferedReader fromMat = new BufferedReader (new InputStreamReader (connectionSocket.getInputStream()));
                DataOutputStream toMat = new DataOutputStream (connectionSocket.getOutputStream());
                
                String nachricht = "check" + '\n';
                toMat.writeBytes(nachricht);
                
                String antwort = fromMat.readLine();
                /*
                **[0] gruen
                **[1] rot
                **[2] gelb
                */
                String [] parameter = antwort.split(",");
                gruenMat = Integer.parseInt(parameter[0]);
                rotMat = Integer.parseInt(parameter[1]);
                gelbMat = Integer.parseInt(parameter[2]);
            }
            catch (IOException e) {
                System.out.println(e.getMessage() + '\n');
            }
        }
    }
    
}
