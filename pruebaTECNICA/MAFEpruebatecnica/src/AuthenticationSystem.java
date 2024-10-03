

import java.io.File;
import java.util.Scanner;

public class AuthenticationSystem {
    public static String hash(String input) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean authenticateUser(String username, String password, String passwordFilePath) {
        try {
            File file = new File(passwordFilePath);
            Scanner fileScanner = new Scanner(file);
            
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(",");
                
                if (parts.length == 4 && parts[0].equals(username)) {
                    String storedHash = parts[2];
                    String salt = parts[3];
                    String calculatedHash = hash(password + salt);
                    
                    fileScanner.close();
                    return calculatedHash.equals(storedHash);
                }
            }
            
            fileScanner.close();
        } catch (Exception e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
        return false;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese el nombre de usuario: ");
        String username = scanner.nextLine();

        System.out.print("Ingrese la contraseña: ");
        String password = scanner.nextLine();

        String passwordFilePath = "src/passwords.txt";
        boolean isAuthenticated = authenticateUser(username, password, passwordFilePath);

        if (isAuthenticated) {
            System.out.println("Autenticación exitosa. Usuario autorizado.");
        } else {
            System.out.println("Autenticación fallida. Usuario rechazado o no encontrado.");
        }

        scanner.close();
    }
}