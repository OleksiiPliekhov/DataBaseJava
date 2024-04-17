import java.util.Scanner;

public class Room {
    private int roomNum;

    private int capacity;

    private double price;

    private int  comfort;

    public Room(int roomNum, int capacity, double price, String comfort) throws Exception {
        if(capacity > 5 || capacity < 1)
            throw new IllegalArgumentException("Capacity value is not between 1 and 4, capacity: [" + capacity + "]");
        if(price < 20)
            throw new IllegalArgumentException("Price value less then 20, price: [" + price + "]");
        this.roomNum = roomNum;
        this.capacity = capacity;
        this.price = price;
        this.comfort = Accessor.getInstance().getComfortId(comfort);
    }

    public int getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(int roomNum) {
        this.roomNum = roomNum;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getComfort() {
        return comfort;
    }

    public void setComfort(int comfort) {
        this.comfort = comfort;
    }

    public static Room roomFactory() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter room number: ");
        int number = scanner.nextInt();
        System.out.println("Enter room capacity: ");
        int capacity = scanner.nextInt();
        System.out.println("Enter room price: ");
        double price = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter room comfort: ");
        String comfort = scanner.nextLine();
        return  new Room(number, capacity, price, comfort);
    }

    @Override
    public String toString() {
        return "Room{" +
                "room number=" + roomNum +
                "capacity=" + capacity +
                ", price=" + price +
                ", comfort=" + comfort +
                '}';
    }
}




/*number_room SMALLINT PRIMARY KEY,
    capacity    SMALLINT NOT NULL,
    ref_comfort INTEGER  NOT NULL,
    price       FLOAT*/