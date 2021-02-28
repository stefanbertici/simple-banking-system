package banking;

public class Card {
    private String number;
    private String pin;

    public Card() {
        this.number = null;
        this.pin = null;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPin() {
        return this.pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
