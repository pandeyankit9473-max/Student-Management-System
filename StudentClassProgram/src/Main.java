
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Student ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Student Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Email: ");
        String email = sc.nextLine();

        System.out.print("Enter Course: ");
        String course = sc.nextLine();

        System.out.print("Enter Programming Score: ");
        double programming = sc.nextDouble();

        System.out.print("Enter Mathematics Score: ");
        double mathematics = sc.nextDouble();
        sc.nextLine();

        System.out.print("Enter Technical Skills (comma separated): ");
        String skillsInput = sc.nextLine();

        List<String> skills = Arrays.asList(skillsInput.split("\\s*,\\s*"));

        // Creating Student object
        Student s1 = new Student(
            id, name, email, course,
            programming, mathematics, skills
        );

        System.out.println("\n----- Student Details -----");
        System.out.println(s1);

        sc.close();
    }
}
