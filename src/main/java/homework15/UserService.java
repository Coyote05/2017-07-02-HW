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

public class UserService {

    public static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("project_pu");

    public void run() {

        Scanner scanner = new Scanner(System.in);

        int operation = -1;

        while (operation != 0) {

            printMenu();

            operation = scanner.nextInt();

            switch (operation) {
                case 1:
                    addNewUser();
                    break;
                case 2:
                    updateUser();
                    break;
                case 3:
                    deleteUser();
                    break;
                case 4:
                    getAllUser();
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

        System.out.println("*********************************");
        System.out.println("***   ---USER MANAGEMENT---   ***");
        System.out.println("***      Add new user (1)     ***");
        System.out.println("***       Update user(2)      ***");
        System.out.println("***       Delete user(3)      ***");
        System.out.println("***      Get all user(4)      ***");
        System.out.println("***          Exit (0)         ***");
        System.out.println("*********************************");
        System.out.print("\nPress the chosen number: ");
    }

    private void addNewUser() {

        int maxUserId;

        EntityManager manager = ENTITY_MANAGER_FACTORY.createEntityManager();

        EntityTransaction transaction = null;

        Scanner scanner = new Scanner(System.in);

        //get the largest used id
        maxUserId = (Integer) manager.createQuery(QueryConstants.SELECT_MAX_USER_ID_QUERY).getSingleResult();

        System.out.println("Type here the new user's...");
        System.out.print("username: ");
        String username = scanner.next();

        System.out.print("password: ");
        String password = scanner.next();

        System.out.print("role: ");
        Integer role = scanner.nextInt();

        try {

            transaction = manager.getTransaction();
            transaction.begin();

            Users users = new Users();

            users.setUser_id(maxUserId + 1);
            users.setUsername(username);
            users.setPassword(password);
            users.setRole(role);

            manager.persist(users);
            transaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollback();
        } finally {
            manager.close();
        }
    }

    private void updateUser() {

        EntityManager manager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Type here the id of the user you want to modificate!");
        System.out.print("id: ");
        Integer id = scanner.nextInt();

        System.out.print("new username: ");
        String username = scanner.next();

        System.out.print("new password: ");
        String password = scanner.next();

        System.out.print("new role: ");
        Integer role = scanner.nextInt();

        try {
            transaction = manager.getTransaction();
            transaction.begin();

            Users users = manager.find(Users.class, id);

            users.setUsername(username);
            users.setPassword(password);

            users.setRole(role);
            transaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollback();
        } finally {
            manager.close();
        }
    }

    private void deleteUser() {

        EntityManager manager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Type here the id of the user you want to delete!");
        System.out.print("id: ");
        Integer id = scanner.nextInt();

        try {
            if (id != 1) {

                transaction = manager.getTransaction();
                transaction.begin();

                Users users = manager.find(Users.class, id);

                manager.remove(users);
                transaction.commit();
            }else{
                System.out.println("Cannot remove admin account!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollback();
        } finally {
            manager.close();
        }
    }

    public List<Users> getAllUser() {

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

            System.out.println(u.toString());
        }
        return users;
    }
}
