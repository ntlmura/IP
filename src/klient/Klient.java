/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package klient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Natalia Mura
 */
public class Klient {

        public static void main(String[] args) throws IOException {

        // gniazdo polaczenia
        Socket socket = null;
        
        try {
            // laczenie z serverem
            socket = new Socket("127.0.0.1", 10105);
        } catch (IOException e) {
            System.out.println("Nie można połączyć się z serwerem.");
        }
        //tworzenie strumieni danych i dostarczanych do socketu
        BufferedReader daneOdebrane = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedReader konsola = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter daneWyslane = new PrintWriter(socket.getOutputStream(), true);

        // bufory wejsciowy i wyjsciowy
        String wej = null;
        String wyj;

        boolean poprawny = false;       
        String user = null;
        
        // Pobieranie nazwy uzytkownika i zapezpieczenie przed duplikatem loginów.
        while (poprawny == false)
        {
            wej = null;
            System.out.println("Podaj nazwe uzytkownika: ");
            user = konsola.readLine();
            
            daneWyslane.println("LOGIN " + user);
            while (daneOdebrane.ready()) {
                wej = daneOdebrane.readLine();
            }
            if (!"Podany login juz jest zajety!".equals(wej))
            {
                poprawny = true;
            }    
        }
        
        // Ustawienie flagi autoreceive na true
        daneWyslane.println("AUTORECEIVE TRUE");
        
        System.out.println("Witaj " + user);
        boolean connected = true;

        while (connected == true) {
            // Pobieranie danych z socketa i przeslanie ich na ekran
            while (daneOdebrane.ready()) {
                wej = daneOdebrane.readLine();
                // wyjscie z aplikacji
                if (wej.equals("EXIT"))
                {
                    connected = false;
                }
                System.out.println(wej);
            }

            // pobieranie danych z klawiatury i wyslanie ich do serwera
            while (konsola.ready()) {
                wyj = parsujPolecenie(konsola);
                if (wyj != null)
                {
                    daneWyslane.println(wyj);
                }
            }
        }
        
        System.out.println("Żegnaj" + user);
        socket.close();
        System.exit(0);
    }
    
    // parsowanie i walidacja polecenia
    public static String parsujPolecenie(BufferedReader in) throws IOException
    {
        String wejscie = in.readLine();
        
        // podzielenie polecenia na czesci
        String [] czesci = wejscie.split(" ");
        int length = czesci.length;
        Boolean blad = false;
      
        // sprawdzenie poprawnosci parametrow i polecen
        if ("LOGIN".equals(czesci[0]))
        {
            if (length != 2)
            {
                blad = true;
            }
        }
        else if ("SEND".equals(czesci[0]))
        {
            if (length < 3)
            {
                blad = true;
            }
        }
        else if ("SENDALL".equals(czesci[0]))
        {
            if (length < 2)
            {
                blad = true;
            }
        }
        else if ("GET".equals(czesci[0]))
        {
            if (length != 2)
            {
                blad = true;
            }
        }
        else if ("GETALL".equals(czesci[0]))
        {
            if (length != 1)
            {
                blad = true;
            }
        }
        /*else if ("AUTORECEIVE".equals(czesci[0]))
        {
            if (length != 2)
            {
                blad = true;
            }
        }*/
        else if ("LISTCLIENTS".equals(czesci[0]))
        {
            if (length != 1)
            {
                blad = true;
            }
        }
        else if ("EXIT".equals(czesci[0]))
        {
            if (length != 1)
            {
                blad = true;
            }
        }
        else
        {
            blad = true;
        }
        if (blad)
        {
            System.out.println("Błędne polecenie: " + wejscie) ;
            return null;
        }
        return wejscie;
    }
}