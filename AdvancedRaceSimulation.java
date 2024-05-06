import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class AdvancedRaceSimulation {
    public static void main(String[] args) {
        final int NUM_CARS = 5;

        // Создаем Phaser с одним регистрированным главным потоком
        Phaser phaser = new Phaser(1) {
            protected boolean onAdvance(int phase, int registeredParties) {
                // Эта функция вызывается при завершении каждой фазы
                System.out.println("Фаза " + phase + " завершена. Участников: " + registeredParties);
                return super.onAdvance(phase, registeredParties);
            }
        };

        for (int i = 1; i <= NUM_CARS; i++) {
            String carName = "Автомобиль " + i;
            new Thread(() -> {
                try {
                    prepare(carName);
                    // Подготовка завершена, сообщаем Phaser'у
                    System.out.println(carName + " готов к старту.");
                    phaser.arriveAndAwaitAdvance();

                    race(carName);
                    // Гонка завершена, сообщаем Phaser'у
                    System.out.println(carName + " финишировал.");
                    phaser.arriveAndDeregister();
                } catch (InterruptedException e) {
                    System.out.println(carName + " был прерван.");
                    Thread.currentThread().interrupt();
                }
            }).start();
            phaser.register(); // Регистрируем участника гонки
        }

        // Главный поток инициализирует старт гонки
        try {
            System.out.println("Гонка начнется после подготовки всех автомобилей.");
            phaser.arriveAndAwaitAdvance();
            System.out.println("Все готовы. Гонка начинается!");
        } finally {
            phaser.arriveAndDeregister(); // Главный поток выходит из фазера
        }
    }

    private static void prepare(String carName) throws InterruptedException {
        // Имитация времени подготовки
        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(1, 5));
    }

    private static void race(String carName) throws InterruptedException {
        // Имитация времени, необходимого для прохождения дистанции
        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(5, 10));
    }
}
