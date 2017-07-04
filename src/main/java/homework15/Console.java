package homework15;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Norman on 2017.07.02..
 */

public class Console {

    public static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("project_pu");

    public List<Users> signIn() {
        /**
         * Authenticate the user.
         */
        Scanner scanner = new Scanner(System.in);

        ProductStockpile productStockpile = new ProductStockpile();

        System.out.println("***   Console program for storage   ***");
        System.out.println("---------------------------------------");

        System.out.print("username: ");
        String username = scanner.next();

        System.out.print("password: ");
        String password = scanner.next();


        EntityManager manager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        List<Users> users = null;

        try {

            transaction = manager.getTransaction();
            transaction.begin();

            users = manager.createQuery(QueryConstants.SELECT_ALL_USER_QUERY).getResultList();
            transaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollback();
        } finally {
            manager.close();
        }

        for (Users u : users) {

            //if the list contains the typed username and password the user log in
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {

                System.out.println("\n\t\tWelcome " + username + "!");

                //if user's role is 0 he is admin and can choose from user management or product management option
                if (u.getRole() == 0) {

                    chooserForAdmin();
                    System.exit(1);

                    //if user's role is 1 he can only enter the product management
                } else if (u.getRole() == 1) {

                    productStockpile.run();
                    System.exit(1);
                }
            }
        }
        System.out.println("\nInvalid username or password!");
        System.exit(1);

        return users;
    }

    private void chooserForAdmin() {

        UserService userService = new UserService();
        ProductStockpile productStockpile = new ProductStockpile();
        Scanner scanner = new Scanner(System.in);

        int operation = -1;

        while (operation != 0) {

            printMenu();

            operation = scanner.nextInt();

            switch (operation) {
                case 1:
                    userService.run();
                    break;
                case 2:
                    productStockpile.run();
                    break;
                case 0:
                    System.out.println("Bye!");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private void printMenu() {

        System.out.println("\n**********************************");
        System.out.println("****       ---MENU---         ****");
        System.out.println("****   User management (1)    ****");
        System.out.println("****  Product management (2)  ****");
        System.out.println("****        Exit (0)          ****");
        System.out.println("**********************************");
        System.out.print("\nPress the chosen number: ");
    }
}
