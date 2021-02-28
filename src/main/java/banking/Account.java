package banking;

import java.util.Random;

public class Account {
    private final int id;
    private final Card card;

    public Account(int id) {
        this.id = id;
        this.card = new Card();
        this.card.setNumber(this.generateNumber());
        this.card.setPin(this.generatePin());
    }

    public int getId() {
        return this.id;
    }

    public String getNumber() {
        return this.card.getNumber();
    }

    public String getPin() {
        return this.card.getPin();
    }


    private String generateNumber() {
        String issuerIdentificationNumber = "400000";
        String customerAccountNumber = String.format("%09d", this.id);
        StringBuilder number = new StringBuilder();

        number.append(issuerIdentificationNumber).append(customerAccountNumber);
        String checkSum = generateCheckSum(number.toString());
        number.append(checkSum);

        return number.toString();
    }

    private String generateCheckSum(String number) {
        int[] digits = new int[15];
        int sum = 0;

        for (int i = 0; i < digits.length; i++) {
            digits[i] = Integer.parseInt(String.valueOf(number.charAt(i)));
        }

        //we multiply the odd digits by 2 then subtract 9 if the resulting number > 9
        for (int i = 0; i < digits.length; i += 2) {
            digits[i] = digits[i] * 2;
            if (digits[i] > 9) {
                digits[i] -= 9;
            }
        }

        //we calculate the sum of digits
        for (int digit : digits) {
            sum += digit;
        }

        //we add the check sum that makes our sum divisible by 10
        for (int i = 0; i <= 9; i++) {
            if ((sum + i) % 10 == 0) {
                return String.valueOf(i);
            }
        }

        return "Something went wrong with check sum";
    }

    private String generatePin() {
        Random random = new Random();
        int randomNumber = random.nextInt(10000);

        return String.format("%04d", randomNumber);
    }
}

