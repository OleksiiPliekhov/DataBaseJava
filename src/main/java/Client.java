public class Client {

    private String fio;

    private String passport;

    public Client(String fio, String passpord) {
        if(!passpord.matches("^[A-ZА-Я]{2} ?[0-9]{6}$"))
            throw new IllegalArgumentException("Wrong password [" + passpord + "]");

        this.fio = fio;
        this.passport = passpord;
    }


    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    @Override
    public String toString() {
        return "Client{" +
                ", fio='" + fio + '\'' +
                ", passport='" + passport + '\'' +
                '}';
    }
}




/*CREATE TABLE Client
(
    id_client SERIAL PRIMARY KEY,
    fio       VARCHAR(50) NOT NULL,
    passport  VARCHAR(50) NOT NULL
);*/