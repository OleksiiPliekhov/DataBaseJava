import java.sql.SQLException;
import java.util.*;
import java.sql.Date;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			Accessor ac = Accessor.getInstance();
			if(ac!=null)
				System.out.println("Connection successful");
			else
			{
				System.out.println("Connection faild");
				System.exit(0);
			}
			
		
			ArrayList<String> v=ac.getComfort();
			System.out.println("List of comfort:");
			for(int i=0;i<v.size();i++)
				System.out.println("\t"+v.get(i));
				
			//call method to add new Client
			/*if(ac.saveClient("Kirilov A.K.", "VK 567899"))
				System.out.println("successful");
			else
				System.out.println("not successful");*/

			/*System.out.println("Task 1:");
			Map<Integer, Float> task1 = ac.findRoomByCapacity(2);
			for (var e : task1.entrySet()) {
				System.out.println("\t" + e.getKey() + " " + e.getValue());
			}
			List<Integer> task2 = ac.findRoomsByComfort("полу-люкс", new Date(2024, 2, 16));
			System.out.println("List of task2:");
			for (int i = 0; i < task2.size(); i++)
				System.out.println("\t" + task2.get(i));

			System.out.println("Task 3:");
			System.out.println("\t" + ac.totalPrice(1, 105));

			System.out.println("Task 4:");
			Map<String, Integer> task4 = ac.ComfortNumber();
			for (var e : task4.entrySet()) {
				System.out.println("\t" + e.getKey() + " " + e.getValue());
			}
			List<Integer> task5 = ac.dateOutFromRoom();
			System.out.println("List of task5:");
			for (int i = 0; i < task5.size(); i++)
				System.out.println("\t" + task5.get(i));*/

			/*//task 2.1

			int res = ac.createRoom(15, 5, "люкс");
			System.out.println("Inserted " + res + " rows");
			res = ac.createRoom(14, 1, "всё очень плохо");
			System.out.println("Inserted " + res + " rows");
			//task 2.2
			res = ac.checkIn("Kirilov A.K.", 13, new Date(124, Calendar.FEBRUARY, 22));
			System.out.println("Created " + res + " rows");
			//task 2.3
			res = ac.changePrice(13, 321);
			System.out.println("Updated " + res + " rows");
			//task 2.4
			res = ac.deleteClient("Kirilov A.K.");
			System.out.println("deleted " + res + " rows");*/

			/*
			Практична No3 (Робота з об'єктами класів)

			1. Створити класи Room та Client. Забезпечити коректність об'єктів, що
			створюються: Room - місткість в діапазоні від 1 до 4 осіб, ціна - більше
			20; Client - паспортні дані починаються серією 2 літери, а потім 6 цифр. (Done)
			2. Створити функцію додавання об'єкта класу Room до БД. Зробити всі
			необхідні перевірки. При тестуванні забезпечити зчитування з
			клавіатури значення полів класу для створення об'єкту класу. (Done)
			3. Створити функцію, яка повертає список всіх клієнтів як колекцію
			об'єктів класу Client. (Done)
			4. Написати функцію, яка приймає як параметр ім'я таблиці бази даних, та
			виводити метадані таблиці: вивести кількість стовпців таблиці, а для
			кожного стовпця таблиці вивести ім'я стовпця та тип даних.
			(Для виведення метаданих таблиці використовується інтерфейс
			ResultSetMetaData, об'єкт якого можна створити методом
			getMetaData() у об'єкта типу ResultSet)
* */

			/*try{
				ac.readAndSaveRoom();
			}
			catch (IllegalAccessException exception){
				System.out.println(exception.getMessage());
			}

			try{
				List<Client> clients = ac.getClientsList();
				for(Client client : clients){
					System.out.println(client);
				}
			}
			catch (Exception exception){
				System.out.println(exception.getMessage());
			}

			try{
				ac.printTableMetadata("Room");
			}
			catch (Exception exception){

			}*/



			//Practice 4
			/*try{
			float res = ac.callSignInProcedura(13, "Kirilov A.K.", "VK 567899", new Date(124, 3, 20), 2);
			System.out.println(res);
			}
			catch (Exception exception){
				exception.printStackTrace();
			}*/

			/*try{
				float res = ac.callSignInProcedura(13, "Ivanov I.I.", "AB 123456", new Date(124, 3, 23), 3);
				System.out.println(res);
			}
			catch (Exception exception){
				exception.printStackTrace();
			}
*/

			//Practice 5
			try{
				ac.roomGenerator(2, 30);
			}
			catch (SQLException exception){
				exception.printStackTrace();
			}

			ac.closeConnection();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
