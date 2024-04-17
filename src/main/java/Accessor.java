import java.sql.*;
import java.util.*;
import java.sql.Date;


public class Accessor {

	private static Accessor singletonAccessor = null;
	private Connection con = null;
	Statement stat = null;

	private Accessor() throws Exception {

		String server = "localhost"; // Адреса сервера PostgreSQL
		String port = "5432"; // Порт, на якому працює PostgreSQL
		String database = "hotel"; // Назва бази даних
		String username = "postgres"; // Ваше ім'я користувача PostgreSQL
		String password = "alexonchik"; // Ваш пароль до PostgreSQL

		Class.forName("org.postgresql.Driver"); // Завантаження драйвера

		// Стрічка підключення до бази даних PostgreSQL
		String connectionString = "jdbc:postgresql://" + server + ":" + port + "/" + database;

		try {
			con = DriverManager.getConnection(connectionString, username, password); // Встановлення з'єднання
			stat = con.createStatement(); // Створення об'єкта заяви
		} catch (SQLException e) {
			e.printStackTrace();
			throw e; // Прокидуємо SQLException, якщо виникає проблема з підключенням
		}

	}


	//singelton
	public static Accessor getInstance()
			throws Exception {
		if (singletonAccessor == null)
			singletonAccessor = new Accessor();
		return singletonAccessor;
	}

	//close DB connection
	public void closeConnection()
			throws SQLException {
		if (con != null)
			con.close();
	}

	/*********************************** Exsamples ******************************************/


//return list of Comforts
	public ArrayList<String> getComfort() throws SQLException {
		ArrayList<String> arr = new ArrayList<String>();


		ResultSet rs = stat.executeQuery("SELECT description FROM Comfort");
		while (rs.next()) {
			arr.add(rs.getString("description"));

		}

		rs.close();

		return arr;
	}


	// insert new Client to DB
	public boolean saveClient(String fio, String pasport) throws SQLException {
		int id = 0;
		//checking if client already exists
		ResultSet rs = stat.executeQuery("SELECT fio FROM Client WHERE fio='" + fio + "'");
		if (rs.next())
			return false;
		//get the last ID
		rs = stat.executeQuery("SELECT max(id_client) FROM Client");
		if (rs.next())
			id = rs.getInt(1);
		//insert new Client. executeUpdate returns count of affected rows
		int n = stat.executeUpdate("INSERT INTO Client (fio, passport) VALUES ( '" + fio + "','" + pasport + "')");
		if (n > 0)
			return true;
		return false;
	}

	public Map<Integer, Float> findRoomByCapacity(int capacity) throws SQLException {
		ResultSet rs = stat.executeQuery("SELECT number_room, price FROM ROOM WHERE capacity = '"+ capacity + "'");
		Map<Integer, Float> resultMap = new HashMap<>();
		while (rs.next()){
			resultMap.put(rs.getInt("number_room"), rs.getFloat("price"));
		}
		return resultMap;
	}

	public List<Integer> findRoomsByComfort(String comfort, Date date) throws SQLException {
		ResultSet rs = stat.executeQuery("SELECT number_room FROM ROOM " +
				"WHERE ref_comfort = (SELECT id_comfort FROM COMFORT WHERE description = '"+ comfort +"') " +
				"AND (number_room IN (SELECT ref_room FROM RENTING where date_out < '"+date+"') OR number_room NOT IN (SELECT ref_room FROM RENTING))");
		List<Integer> resultList = new ArrayList<>();
		while(rs.next()){
			resultList.add(rs.getInt("number_room"));
		}
		return  resultList;
	}

	public float totalPrice(int dayNum, int roomNum) throws SQLException {
		ResultSet rs = stat.executeQuery("SELECT price FROM ROOM WHERE number_room = '" + roomNum + "'");
		float res = 0;
		if(rs.next()){
			res += dayNum * rs.getFloat("price");
		}
		return  res;
	}

	public Map<String, Integer> ComfortNumber() throws SQLException {
		ResultSet rs = stat.executeQuery("SELECT COUNT(number_room), description FROM COMFORT JOIN ROOM ON id_comfort = ref_comfort GROUP BY description");
		Map<String, Integer> resultMap = new HashMap<>();
		while(rs.next()){
			resultMap.put(rs.getString("description"), rs.getInt(1));
		}
		return  resultMap;
	}

	public List<Integer> dateOutFromRoom() throws SQLException {
		ResultSet rs = stat.executeQuery("SELECT ref_room FROM RENTING WHERE date_out = CURRENT_DATE");
		List<Integer> resultList = new ArrayList<>();
		if(rs.next()){
			resultList.add(rs.getInt("ref_room"));
		}
		return resultList;
	}

	//1. Написати функцію, яка додає до БД кімнату під номером «110», місткістю «2» та комфортністю «напівлюкс». Номер кімнати,
	// місткість та назва комфортності у функцію передаються як параметри. Якщо назва комфортності, передана в функцію, не існує
	// у БД, тоді видати повідомлення про неможливість додати номер до БД. Також, якщо кімната із заданим номером у БД є, тоді також не додавати.

	public int createRoom(int roomNum, int roomCapacity, String roomComfort) throws Exception {
		ResultSet rs = stat.executeQuery("SELECT id_comfort FROM COMFORT WHERE description = '" + roomComfort + "'");
		int roomCap = 0;
		if(!rs.next()){
			throw new Exception("Data base does not contain comfort + [" + roomComfort + "]");
		}
		else
			roomCap = rs.getInt("id_comfort");

		rs = stat.executeQuery("SELECT number_room FROM ROOM WHERE number_room = '" + roomNum + "'");
		if(rs.next()){
			throw new Exception("Room with room number + [" + roomNum + "] already exist");
		}

		PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO ROOM(number_room, capacity, ref_comfort, price) VALUES (?, ?, ?, null)");
		preparedStatement.setInt(1, roomNum);
		preparedStatement.setInt(2, roomCapacity);
		preparedStatement.setInt(3, roomCap);

		int n =preparedStatement.executeUpdate();
		preparedStatement.close();
		return n;
	}

	//2. Написати функцію, яка поселяє клієнта Іванова до кімнати під номером «110» 25.02.2023. ПІБ клієнта, номер кімнати та дата передаються
	// у функцію як параметри. При поселенні клієнта перевірити, чи вільний заданий номер на дату. Якщо ні або якщо клієнт раніше не зареєстрований
	// у БД, видати повідомлення про помилку та не поселяти.
	public int checkIn(String personFIO, int roomNum, Date dateIn) throws Exception {
		ResultSet rs = stat.executeQuery("SELECT id_client FROM CLIENT WHERE fio = '" + personFIO + "'");
		int person = 0;
		if(!rs.next()){
			throw new Exception("Client with fio: does not exist + [" + personFIO + "]");
		}
		else{
			person = rs.getInt("id_client");
		}
		rs = stat.executeQuery("SELECT * FROM RENTING WHERE ref_client = '" + person + "' AND ref_room = '" + roomNum + "' AND (date_out > '" + dateIn + "' OR date_out IS NULL) ");
		if(rs.next()){
			throw new Exception("Room is not empty for the date + [" + dateIn + "]");
		}

		PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO RENTING(ref_client, ref_room, date_in, date_out) VALUES (?, ?, ?, null)");
		preparedStatement.setInt(1, person);
		preparedStatement.setInt(2, roomNum);
		preparedStatement.setDate(3, (java.sql.Date) dateIn);
		int n = preparedStatement.executeUpdate();

		preparedStatement.close();
		return n;
	}

	//3. Написати функцію, яка встановлює вартість кімнати під номером «110» 230 грн. Номер кімнати та вартість передаються у
	// функцію як параметри. Якщо кімнати із заданим номером немає, видати повідомлення про помилку.
	public int changePrice(int roomNum, float newPrice) throws Exception {
		ResultSet rs = stat.executeQuery("SELECT number_room FROM ROOM WHERE number_room = '" + roomNum + "'");
		if(!rs.next())
			throw new Exception("Rood  + [" + roomNum + "] doesn`t exist");


		PreparedStatement preparedStatement = con.prepareStatement("UPDATE ROOM SET price = ? WHERE number_room = ?");
		preparedStatement.setDouble(1, newPrice);
		preparedStatement.setInt(2, roomNum);
		int n = preparedStatement.executeUpdate();

		preparedStatement.close();
		return n;
	}

	//4. Написати функцію, що видаляє клієнта Іванова з БД. ПІБ клієнта передається у функцію як параметри. При видаленні
	// клієнта також видалити факти його проживання в номерах готелю.
	public int deleteClient(String clientFIO) throws Exception {
		ResultSet resultSet = stat.executeQuery("SELECT id_client FROM CLIENT WHERE fio = '" + clientFIO + "'");
		int person_id = 0;
		if(!resultSet.next())
			throw new Exception("Client  + [" + clientFIO + "] doesn`t exist");


		else
			person_id = resultSet.getInt("id_client");


		PreparedStatement preparedStatement = con.prepareStatement("DELETE FROM RENTING WHERE ref_client = ?");
		preparedStatement.setInt(1, person_id);
		preparedStatement.executeUpdate();
		preparedStatement = con.prepareStatement("DELETE FROM CLIENT WHERE id_client = ?");
		preparedStatement.setInt(1, person_id);
		int n = preparedStatement.executeUpdate();
		preparedStatement.close();
		return n;
	}

	//Third practice

	public int getComfortId(String description) throws Exception {
		PreparedStatement preparedStatement = con.prepareStatement("SELECT id_comfort FROM COMFORT WHERE description = ?");

		preparedStatement.setString(1, description);

		ResultSet resultSet = preparedStatement.executeQuery();
		if(!resultSet.next())
			throw new Exception("Comfort with description [" + description + "] not found");

		return resultSet.getInt(1);
	}



	public int readAndSaveRoom() throws Exception {
		Room room = Room.roomFactory();
		PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO ROOM(number_room, capacity, ref_comfort, price) VALUES (?, ?, ?, ?)");

		preparedStatement.setInt(1, room.getRoomNum());
		preparedStatement.setInt(2, room.getCapacity());
		preparedStatement.setInt(3, room.getComfort());
		preparedStatement.setDouble(4, room.getPrice());

		return preparedStatement.executeUpdate();
	}

	public List<Client> getClientsList() throws SQLException {
		PreparedStatement preparedStatement = con.prepareStatement("SELECT fio, passport FROM CLIENT");
		ResultSet resultSet = preparedStatement.executeQuery();

		List<Client> resultList= new ArrayList<>();

		while(resultSet.next()){
			resultList.add(new Client(resultSet.getString(1), resultSet.getString(2)));
		}

		return resultList;
	}


	public void printTableMetadata(String tableName) throws SQLException {


			// Створення запиту SQL для отримання метаданих таблиці
			String sql = "SELECT * FROM " + tableName + " WHERE 1=0"; // WHERE 1=0 для того, щоб не вибирати жоден рядок

			ResultSet rs = stat.executeQuery(sql);

			// Отримання метаданих результату запиту
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount(); // Кількість стовпців

			// Виведення заголовку
			System.out.println("Метадані таблиці " + tableName + ":");
			System.out.println("Кількість стовпців: " + columnCount);
			System.out.println("---------------------------------------------------");

			// Виведення метаданих кожного стовпця
			for (int i = 1; i <= columnCount; i++) {
				String columnName = rsmd.getColumnName(i); // Ім'я стовпця
				String columnType = rsmd.getColumnTypeName(i); // Тип даних стовпця
				System.out.println("Стовпець " + i + ": " + columnName + " (" + columnType + ")");
			}

	}


	public float callSignInProcedura(int room_number, String fio, String passport, Date date_in, int stay_days) throws SQLException {

		CallableStatement cstmt = con.prepareCall("{CALL register_client (?, ?, ?, ?, ?, ?)}");

		cstmt.setInt(1, room_number);
		cstmt.setString(2, fio);
		cstmt.setString(3, passport);
		cstmt.setDate(4, date_in);
		cstmt.setInt(5, stay_days);

		cstmt.registerOutParameter(6, Types.REAL);

		cstmt.executeUpdate();

		return cstmt.getFloat(6);
	}

	//Practice 5
	public ArrayList<Integer> getAllComforts() throws SQLException {
		ArrayList<Integer> arr = new ArrayList<>();


		ResultSet rs = stat.executeQuery("SELECT id_comfort FROM COMFORT");
		while (rs.next()) {
			arr.add(rs.getInt("id_comfort"));
		}
		rs.close();
		return arr;
	}

	public void roomGenerator(int roomCapacity, float roomPrice) throws SQLException {
		List<Integer> comfortList;
		int maxRoomNum = 1;
		PreparedStatement preparedStatement = null;

		try{
			con.setAutoCommit(false);
			comfortList = getAllComforts();
			preparedStatement = con.prepareStatement("SELECT MAX(number_room) FROM ROOM");
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()){
				maxRoomNum = resultSet.getInt(1) + 1;
			}

			preparedStatement = con.prepareStatement("INSERT INTO ROOM VALUES (?, ?, ?, ?)");
			for(int comfort : comfortList){
				preparedStatement.setInt(1, maxRoomNum);
				preparedStatement.setInt(2, roomCapacity);
				preparedStatement.setInt(3, comfort);
				preparedStatement.setFloat(4, roomPrice);
				preparedStatement.addBatch();
				maxRoomNum++;
			}

			int[] result = preparedStatement.executeBatch();
			for (int aResult : result) {
				if (aResult != 1) {
					con.rollback();
					preparedStatement.clearBatch();
					break;
				}
			}

		} catch (SQLException e) {
            throw new RuntimeException(e);
        }
		finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
			con.setAutoCommit(true);
		}

    }

}

