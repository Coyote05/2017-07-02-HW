package homework15;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Norman on 2017.07.02..
 */

public class ProductStockpile {

    public static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("project_pu");

    public void run() {

        Scanner scanner = new Scanner(System.in);

        int operation = -1;

        while (operation != 0) {

            printMenu();

            operation = scanner.nextInt();

            switch (operation) {
                case 1:
                    createProduct();
                    break;
                case 2:
                    takeInProduct();
                    break;
                case 3:
                    expendingProduct();
                    break;
                case 4:
                    getAllProduct();
                    break;
                case 5:
                    createExcelFromDb();
                case 0:
                    System.out.println("Bye!");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private void printMenu() {

        System.out.println("*********************************************************");
        System.out.println("***             ---PRODUCT MANAGEMENT---              ***");
        System.out.println("***                Create product (1)                 ***");
        System.out.println("***                Take in product (2)                ***");
        System.out.println("***               Expending product (3)               ***");
        System.out.println("***                Get all product (4)                ***");
        System.out.println("***    Create an Excel file about the products (5)    ***");
        System.out.println("***                      Exit (0)                     ***");
        System.out.println("*********************************************************");
        System.out.print("\nPress the chosen number: ");
    }

    private void createProduct() {

        EntityManager manager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        Scanner scanner = new Scanner(System.in);

        //get the largest used id
        int maxProductId = (Integer) manager.createQuery(QueryConstants.SELECT_MAX_PRODUCT_ID_QUERY).getSingleResult();

        System.out.print("\nProduct name: ");
        String name = scanner.next();

        System.out.print("type: ");
        String type = scanner.next();

        System.out.print("unit: ");
        String unit = scanner.next();

        System.out.print("quantity: ");
        Integer quantity = scanner.nextInt();

        try {
            transaction = manager.getTransaction();
            transaction.begin();

            Storage storage = new Storage();

            storage.setId(maxProductId + 1);
            storage.setName(name);
            storage.setType(type);
            storage.setUnit(unit);
            storage.setQuantity(quantity);

            manager.persist(storage);
            transaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollback();
        } finally {
            manager.close();
        }
    }

    private void takeInProduct() {

        EntityManager manager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Type here the id of the product you want to take in!");
        System.out.print("id: ");
        Integer id = scanner.nextInt();

        System.out.print("quantity: ");
        Integer quantity = scanner.nextInt();

        try {
            transaction = manager.getTransaction();
            transaction.begin();

            Storage storage = manager.find(Storage.class, id);

            //get the last quantity in stock
            int quantityInStock = storage.getQuantity();

            storage.setQuantity(quantityInStock + quantity);
            transaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollback();
        } finally {
            manager.close();
        }
    }

    private void expendingProduct() {

        EntityManager manager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Type here the id of the product you want to expend!");
        System.out.print("id: ");
        Integer id = scanner.nextInt();

        System.out.print("quantity: ");
        Integer quantity = scanner.nextInt();

        try {
            transaction = manager.getTransaction();
            transaction.begin();

            Storage storage = manager.find(Storage.class, id);

            //get the last quantity in stock
            int quantityInStock = storage.getQuantity();

            if (quantity < quantityInStock) {

                storage.setQuantity(quantityInStock - quantity);
                transaction.commit();
            } else {
                System.out.println("Not enough quantity in stock!");
                run();
            }

        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollback();
        } finally {
            manager.close();
        }
    }

    private List<Storage> listOfAllProduct() {

        EntityManager manager = ENTITY_MANAGER_FACTORY.createEntityManager();
        EntityTransaction transaction = null;
        List<Storage> storage = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();

            storage = manager.createQuery(QueryConstants.SELECT_ALL_PRODUCT_QUERY).getResultList();
            transaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollback();
        } finally {
            manager.close();
        }

        return storage;
    }

    private void getAllProduct() {

        for (Storage s : listOfAllProduct()) {

            System.out.println(s.toString());
        }
    }

    private void createExcelFromDb() {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Output");

        //create head row with the column names from database
        Row head = sheet.createRow(0);
        head.createCell(0).setCellValue("id");
        head.createCell(1).setCellValue("name");
        head.createCell(2).setCellValue("type");
        head.createCell(3).setCellValue("unit");
        head.createCell(4).setCellValue("quantity");

        for (int i = 0; i < 5; i++) {

            //make the cells of head row bold
            CellStyle rowCellStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            rowCellStyle.setFont(font);
            head.getCell(i).setCellStyle(rowCellStyle);
        }

        int rowNumber = 1;

        for (Storage s : listOfAllProduct()) {

            Row row = sheet.createRow(rowNumber++);

            Cell cellId = row.createCell(0);
            cellId.setCellValue(s.getId());

            Cell cellName = row.createCell(1);
            cellName.setCellValue(s.getName());

            Cell cellType = row.createCell(2);
            cellType.setCellValue(s.getType());

            Cell cellUnit = row.createCell(3);
            cellUnit.setCellValue(s.getUnit());

            Cell cellQuantity = row.createCell(4);
            cellQuantity.setCellValue(s.getQuantity());

            //size the cells automatic to content width
            for (int i = 0; i < 5; i++) {

                sheet.autoSizeColumn(i);
            }
        }

        try {
            //write the data to file
            FileOutputStream outputStream = new FileOutputStream("C:\\Users\\C\\IdeaProjects\\2017-07-02-HW\\src\\main\\java\\output.xlsx");
            workbook.write(outputStream);
            workbook.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Done");
    }
}
