package tests;

import com.lleps.mfm.Main;
import com.lleps.mfm.Storage;
import com.lleps.mfm.Utils;
import com.lleps.mfm.model.Category;
import com.lleps.mfm.model.Client;
import com.lleps.mfm.model.ExercisePlan;
import com.lleps.mfm.model.Payment;
import org.apache.commons.lang3.RandomUtils;

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

    private CategoryGeneratorTest() {
        String categoriess[] = {"Yoga", "Máquinas", "MMA", "Pilates"};
        for (String category : categoriess) {
            List<Payment> payments = new ArrayList<>();
            List<Client> clients = getRandomClients(RAND_CLIENT_COUNT, payments);
            Category category1 = new Category(category, FEE_PRICE, clients, payments, new ArrayList<>());
            System.out.println("Category " + category + " added.");
            try {
                Storage.getInstance().saveCategory(category1);
            } catch (Exception e) {
                Utils.reportException(e, "err");
            }
        }
        Main.main(new String[]{});
    }

    private int RAND_CLIENT_COUNT = 500;
    private int RAND_PAYMENT_COUNT = RAND_CLIENT_COUNT * 15;
    private int FEE_PRICE = 750;
    private LocalDate MAX_DATE = LocalDate.now();

    private LocalDate getRandomDate() {
        return LocalDate.of(randomInt(2018, 2021), randomInt(1, 6), randomInt(1, 28));
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

    private Client generateRandomClient(int id, LocalDate inscription, LocalDate max, List<Payment> payments) {
        // Create client con planes
        Client client = new Client(id, true, randomInt(20000000, 45000000), getRandomName(), getRandomLastName(), getRandomPhone(),
                getRandomHomeAddress(), getRandomMail(), getRandomDate(), "", getRandomPlans());
        if (randomInt(1, 4) == 1) client.setInactive(true);

        // fees
        int paidFees = randomInt(1, 8);
        LocalDate d = inscription;
        for (int i = 0; i < paidFees; i++) {
            Payment payment = new Payment(id, FEE_PRICE, d, d);
            payments.add(payment);
            if (d.isBefore(max)) {
                d = d.plusMonths(1);
            } else break;
        }

        return client;
    }

    private String getRandomName() {
        String names[] = {"Agustin", "Sofia", "Leandro", "Martina", "Daniel", "Tomas", "Lionel", "Gustavo", "Mariano", "Pedro",
                "Marcelo", "Agustina", "Camila", "Yamila", "Andrea", "Fernanda", "Fernando", "Horacio", "Silvia", "Amanda", "Erica",
                "Constanza", "Uriel", "Mateo", "Mauricio"};
        return names[(int)(Math.random() * names.length)];
    }

    private String getRandomLastName() {
        String lasts[] = {"Buenavera", "Boiero", "Demarco", "Gutierrez", "Gonzales", "Perez", "Hernandes", "Rodriguez", "Korgo",
                "Navarro", "Acosta", "Acevedo", "Aguilar", "Guerrero", "Oclavo", "Sanchez", "Mendes", "Johnson"};
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

    private List<Client> getRandomClients(int count, List<Payment> payments) {
        List<Client> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Client client = generateRandomClient(i, getRandomDate(), MAX_DATE, payments);
            result.add(client);
        }
        return result;
    }

    private String randString(String[] chances) {
        return chances[RandomUtils.nextInt(0, chances.length)];
    }

    private List<ExercisePlan> getRandomPlans() {
        List<ExercisePlan> result = new ArrayList<>();
        int planCount = RandomUtils.nextInt(0, 4);
        String[] planNames = { "pecho", "piernas", "basico", "avanzado", "inicial", "hombros", "volumen", "resistencia"};
        String[][] exercises = new String[36][4];
        int exerciseCount = RandomUtils.nextInt(0, 20);
        String[] exerciseName = { "mancuerna", "hombro", "pecho", "bici", "biceps", "triceps", "espalda", "disco", "spinning"};
        String[] series = { "2", "3", "4" };
        String[] repetitions = { "10", "15", "12" };
        String[] pause = { "15", "20", "25", "30", "40", "50", "60"};
        for (int i = 0; i < exerciseCount; i++) {
            exercises[i][0] = randString(exerciseName);
            exercises[i][1] = randString(series);
            exercises[i][2] = randString(repetitions);
            exercises[i][3] = randString(pause);
        }
        for (int i = exerciseCount; i < exercises.length; i++) {
            exercises[i][0] = "";
            exercises[i][1] = "";
            exercises[i][2] = "";
            exercises[i][3] = "";
        }
        for (int i = 0; i < planCount; i++) {
            ExercisePlan plan = new ExercisePlan(planNames[RandomUtils.nextInt(0, planNames.length)],
                    LocalDate.now().minusDays(RandomUtils.nextInt(0, 90)), exercises);
            result.add(plan);
        }
        return result;
    }
}
