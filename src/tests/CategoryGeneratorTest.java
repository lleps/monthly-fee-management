package tests;

import com.lleps.mfm.Main;
import com.lleps.mfm.Storage;
import com.lleps.mfm.Utils;
import com.lleps.mfm.model.Category;
import com.lleps.mfm.model.Client;
import com.lleps.mfm.model.Payment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leandro on 20/11/2015.
 */
public class CategoryGeneratorTest {
    public static void main(String[] args) {
        new CategoryGeneratorTest();
    }

    CategoryGeneratorTest() {
        String categoriess[] = {"Yoga", "M�quinas", "MMA"};
        for (String category : categoriess) {
            Category category1 = new Category(category, 300, getRandomClients(RAND_CLIENT_COUNT),
                    getRandomPayments(RAND_PAYMENT_COUNT));
            System.out.println("Category " + category + " added.");
            try {
                Storage.getInstance().saveCategory(category1);
            } catch (Exception e) {
                Utils.reportException(e, "err");
            }
        }
        Main.main(new String[]{});
    }

    int RAND_CLIENT_COUNT = 15000;
    int RAND_PAYMENT_COUNT = RAND_CLIENT_COUNT * 5;

    private LocalDate getRandomDate() {
        return LocalDate.of(randomInt(2013, 2016), randomInt(1, 13), randomInt(1, 28));
    }

    private int randomInt(int startInclusive, int endExclusive) {
        return startInclusive + (int)(Math.random() * (endExclusive - startInclusive));
    }

    private List<Payment> getRandomPayments(int count) {
        List<Payment> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Payment payment = new Payment(randomInt(1, RAND_CLIENT_COUNT), 250, getRandomDate(), getRandomDate());
            result.add(payment);
        }
        return result;
    }

    private String getRandomName() {
        String names[] = {"Agustin", "Sofia", "Leandro", "Martina", "Daniel", "Tomas", "Lionel", "Gustavo", "Mariano", "Pedro",
                "Marcelo", "Agustina", "Camila", "Yamila", "Andrea", "Fernanda", "Fernando", "Horacio", "Silvia", "Amanda", "Erica",
                "Constanza", "Uriel", "Mateo", "Mauricio"};
        return names[(int)(Math.random() * names.length)];
    }

    private String getRandomLastName() {
        String lasts[] = {"Barbero", "Boiero", "Messi", "Macri", "Gonzales", "Perez", "Federer", "Rodriguez", "Koyro",
                "Navarro", "Acosta", "Acevedo", "Aguilar", "Guerrero"};
        return lasts[(int)(Math.random() * lasts.length)];
    }

    private String getRandomMail() {
        String[] domains = {"@gmail.com", "@hotmail.com", "@outlook.com", "@yahoo.com"};
        return (getRandomName() + getRandomLastName() + domains[(int)(Math.random() * domains.length)]).toLowerCase();
    }

    private String getRandomPhone() {
        return "4" + Integer.toString((int)(Math.random() * 1000000));
    }

    private String getRandomHomeAddress() {
        String types[] = {"Villa", "Gobernador", "Intendente", "General", "Don"};
        return types[(int)(Math.random() * types.length)] + " " + getRandomLastName() + " " + ((int)(Math.random()*500));
    }

    private List<Client> getRandomClients(int count) {
        List<Client> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Client client = new Client(i, true, getRandomName(), getRandomLastName(), getRandomPhone(),
                    getRandomHomeAddress(), getRandomMail(), getRandomDate(), "");
            if (randomInt(1, 3) == 1) client.setInactive(true);
            result.add(client);
        }
        return result;
    }
}
