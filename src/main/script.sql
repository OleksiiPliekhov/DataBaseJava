CREATE OR REPLACE FUNCTION register_client(
    room_number integer,
    fullname text,
    passport_data text,
    arrival_date date,
    stay_days integer)
    RETURNS float AS
$$
DECLARE
client_id integer;
    cost float;
BEGIN

IF NOT EXISTS (SELECT * FROM ROOM WHERE number_room = room_number)
    THEN RAISE EXCEPTION 'ROOM % does not exist', room_number;
END IF;

    -- Перевірка вільності номера

IF EXISTS (SELECT * FROM renting  WHERE ref_room = room_number
                          AND (arrival_date > renting.date_in OR arrival_date + stay_days > renting.date_in)
                          AND (date_out IS NOT NULL AND (arrival_date <= date_out OR arrival_date + stay_days <= date_out)))
    THEN  RAISE EXCEPTION 'Room with number % does not empty for this date', room_number;
END IF;
        -- Номер вільний
        -- Перевірка існування клієнта
SELECT id_client INTO client_id FROM client WHERE client.passport = passport_data;
IF client_id IS NULL THEN
            -- Клієнта не знайдено, додаємо його
            INSERT INTO client (fio, passport) VALUES (fullname, passport_data) RETURNING id_client INTO client_id;
END IF;
        -- Поселення клієнта
INSERT INTO renting (ref_client, ref_room, date_in, date_out) VALUES (client_id, room_number, arrival_date, arrival_date + stay_days);
-- Розрахунок вартості проживання
SELECT price * stay_days INTO cost FROM room WHERE room.number_room = room_number;
RETURN cost;

END;
$$ LANGUAGE plpgsql;
