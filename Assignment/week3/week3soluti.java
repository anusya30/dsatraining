// --- PROGRAM 1: SMART HOME ---
abstract class Device {
    private String deviceName;
    private boolean powerStatus;

    public Device(String name) {
        this.deviceName = name;
        this.powerStatus = false;
    }

    public String getDeviceName() { return deviceName; }
    public boolean isPowerStatus() { return powerStatus; }
    public void turnOn() { powerStatus = true; }
    public void turnOff() { powerStatus = false; }

    public abstract void displayStatus();
}

class Light extends Device {
    public Light(String name) { super(name); }
    @Override
    public void displayStatus() {
        System.out.println("Device: " + getDeviceName() + " | Status: " + (isPowerStatus() ? "ON" : "OFF"));
    }
}

class Thermostat extends Device {
    private int temperature; 
    public Thermostat(String name, int temp) {
        super(name);
        this.temperature = temp;
    }
    public void setTemperature(int temp) { this.temperature = temp; }
    @Override
    public void displayStatus() {
        System.out.println("Device: " + getDeviceName() + " | Status: " + (isPowerStatus() ? "ON (" + temperature + "°C)" : "OFF"));
    }
}

// --- PROGRAM 2: PAYMENT SYSTEM ---
interface PaymentMethod {
    void processPayment(double amount);
}

class CreditCardPayment implements PaymentMethod {
    @Override
    public void processPayment(double amount) {
        System.out.println("Payment: ₹" + amount + " via Credit Card.");
    }
}

class UPIPayment implements PaymentMethod {
    @Override
    public void processPayment(double amount) {
        System.out.println("Payment: ₹" + amount + " via UPI.");
    }
}

class PaymentProcessor {
    public void execute(PaymentMethod method, double amount) {
        method.processPayment(amount);
    }
}

// --- PROGRAM 3: NOTIFICATION SYSTEM ---
interface EmailSender { void sendEmail(String msg); }
interface SMSSender { void sendSMS(String msg); }

class EmailApp implements EmailSender {
    @Override
    public void sendEmail(String msg) { System.out.println("Email Alert: " + msg); }
}

class SMSApp implements SMSSender {
    @Override
    public void sendSMS(String msg) { System.out.println("SMS Alert: " + msg); }
}

// --- MAIN CLASS (Indha pera 'Main' nu mathithen for Online Compiler) ---
public class week3soluti {
    public static void main(String[] args) {
        
        System.out.println("--- PROGRAM 1: SMART HOME ---");
        Light l = new Light("Hall Light");
        Thermostat t = new Thermostat("AC", 22);
        l.turnOn();
        t.turnOn();
        l.displayStatus();
        t.displayStatus();

        System.out.println("\n--- PROGRAM 2: PAYMENT SYSTEM ---");
        PaymentProcessor pp = new PaymentProcessor();
        pp.execute(new UPIPayment(), 500.00);
        pp.execute(new CreditCardPayment(), 1200.50);

        System.out.println("\n--- PROGRAM 3: NOTIFICATION SYSTEM ---");
        EmailApp email = new EmailApp();
        SMSApp sms = new SMSApp();
        email.sendEmail("New login detected.");
        sms.sendSMS("Your OTP is 4567.");
    }
}