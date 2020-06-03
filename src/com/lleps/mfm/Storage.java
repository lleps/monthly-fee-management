package com.lleps.mfm;

import com.alee.utils.FileUtils;
import com.lleps.mfm.model.Category;
import com.lleps.mfm.model.Client;
import com.lleps.mfm.model.ExercisePlan;
import com.lleps.mfm.model.Payment;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Leandro B. on 03/11/2015.
 */
public class Storage {
    private static final Storage instance = new Storage();

    public static Storage getInstance() {
        return instance;
    }

    private final static String DATA_FOLDERNAME = "gym";
    private final static String CONFIG_FILENAME = "config.properties";
    private final static String CLIENTS_FILENAME = "clients.list";
    private final static String PAYMENTS_FILENAME = "payments.list";
    private final static String PLANS_FILENAME = "plans.list";

    private final File userFolder;
    private final File mainFolder;
    private final File categoriesFolder;

    private Storage() {
        userFolder = new File("./datos");
        mainFolder = new File(userFolder, DATA_FOLDERNAME);
        mainFolder.mkdirs();

        categoriesFolder = new File(mainFolder + "/" + "categories");
    }

    public void doBackup() throws IOException {
        String tmpBackupFolderName = DATA_FOLDERNAME + "-" + LocalDate.now();
        File tmpBackupFolderDir = new File(userFolder, tmpBackupFolderName);
        File backupZipFile = new File(userFolder, tmpBackupFolderName + ".zip");

        FileUtils.copyDirectory(mainFolder, tmpBackupFolderDir);
        if (backupZipFile.exists()) backupZipFile.delete();
        pack(tmpBackupFolderDir.toPath(),  backupZipFile.toPath());
        deleteDirectory(tmpBackupFolderDir);
    }

    private static void pack(final Path folder, final Path zipFilePath) throws IOException {
        try (
                FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
                ZipOutputStream zos = new ZipOutputStream(fos)
        ) {
            Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    zos.putNextEntry(new ZipEntry(folder.relativize(file).toString()));
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }

                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    zos.putNextEntry(new ZipEntry(folder.relativize(dir).toString() + "/"));
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    public List<Category> loadAllCategories() {
        List<Category> result = new ArrayList<>();
        File[] categoryFolders = categoriesFolder.listFiles();
        if (categoryFolders != null) {
            for (File f : categoryFolders) {
                if (f.isDirectory()) {
                    try {
                        result.add(loadCategoryFromFolder(f));
                    } catch (IOException | ClassNotFoundException e) {
                        Utils.reportException(e, "Error cargando categoria " + f);
                    }
                }
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static Category loadCategoryFromFolder(File folder) throws IOException, ClassNotFoundException {
        String name;
        int monthPrice;
        List<Client> clients;
        List<Payment> payments;
        List<ExercisePlan> plans;

        name = folder.getName();

        try (FileInputStream input = new FileInputStream(new File(folder, CONFIG_FILENAME))) {
            Properties properties = new Properties();
            properties.load(input);
            try {
                monthPrice = Integer.parseInt(properties.getProperty("monthPrice"));
            } catch (NumberFormatException e) {
                monthPrice = 1;
            }
        }
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(new File(folder, CLIENTS_FILENAME)))) {
            clients = (List) input.readObject();
        }
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(new File(folder, PAYMENTS_FILENAME)))) {
            payments = (List) input.readObject();
        }
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(new File(folder, PLANS_FILENAME)))) {
            plans = (List) input.readObject();
        } catch (IOException e) { // Older versions don't have this file
            plans = new ArrayList<>();
        }
        return new Category(name, monthPrice, clients, payments, plans);
    }

    public void saveCategory(Category category) throws IOException {
        saveCategoryClients(category);
        saveCategoryConfig(category);
        saveCategoryPayments(category);
    }

    public void saveCategoryConfig(Category category) throws IOException {
        saveCategoryConfig(category, getCategoryFile(category, CONFIG_FILENAME));
    }

    public void saveCategoryConfig(Category category, File file) throws IOException {
        try (FileOutputStream output = new FileOutputStream(file)) {
            Properties properties = new Properties();
            properties.setProperty("monthPrice", Integer.toString(category.getMonthPrice()));
            properties.store(output, null);
        }
    }

    public void saveCategoryClients(Category category) throws IOException {
        saveCategoryClients(category, getCategoryFile(category, CLIENTS_FILENAME));
    }

    private void saveCategoryClients(Category category, File file) throws IOException {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file))) {
            output.writeObject(category.getClients());
        }
    }

    public void saveCategoryPayments(Category category) throws IOException {
        saveCategoryPayments(category, getCategoryFile(category, PAYMENTS_FILENAME));
    }

    private void saveCategoryPayments(Category category, File file) throws IOException {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file))) {
            output.writeObject(category.getPayments());
        }
    }

    public void saveCategoryPlans(Category category) throws IOException {
        saveCategoryPlans(category, getCategoryFile(category, PLANS_FILENAME));
    }

    private void saveCategoryPlans(Category category, File file) throws IOException {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file))) {
            output.writeObject(category.getPlans());
        }
    }

    public void appendException(String message) {
        File exceptionsFile = new File(mainFolder, "exceptions.txt");

        try {
            exceptionsFile.createNewFile();
            Files.write(exceptionsFile.toPath(), message.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("No se puede escribir: " + e);
            e.printStackTrace();
        }
    }

    public void removeCategory(Category category) {
        deleteDirectory(new File(categoriesFolder, category.getName()));
    }

    private static boolean deleteDirectory(File directory) {
        if(directory.exists()){
            File[] files = directory.listFiles();
            if(null!=files){
                for(int i=0; i<files.length; i++) {
                    if(files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    }
                    else {
                        files[i].delete();
                    }
                }
            }
        }
        return(directory.delete());
    }

    private File getCategoryFile(Category category, String fileName) throws IOException {
        File categoryFolder = new File(categoriesFolder, category.getName());
        categoryFolder.mkdirs();
        return new File(categoryFolder, fileName);
    }
}
