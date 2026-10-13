package travelsync;
import java.util.Scanner;
public class AdminSetup {
 public static void main(String[] args)throws Exception{DB.initialize();Scanner s=new Scanner(System.in);System.out.print("Admin username: ");String u=s.nextLine().trim();System.out.print("Admin password (8+ chars): ");String p=s.nextLine();if(!u.matches("[A-Za-z0-9_]{3,24}")||p.length()<8){System.out.println("Invalid credentials format.");return;}DB.admin(u,p);System.out.println("Admin created.");}
}
