DELETE FROM users;
DELETE FROM events;
DELETE FROM users_to_events;

INSERT INTO users (id, email, name, password, non_expired, non_locked, credentials_not_expired, enabled) VALUES ('1', 'janusz@xxx.pl', 'Janusz', 'qwerty123', TRUE, TRUE, TRUE, TRUE );
INSERT INTO users (id, email, name, password, non_expired, non_locked, credentials_not_expired, enabled) VALUES ('2', 'grazia@xxx.pl', 'Grazyna', 'zxcvbn987', TRUE, TRUE, TRUE, TRUE );
INSERT INTO users (id, email, name, password, non_expired, non_locked, credentials_not_expired, enabled) VALUES ('3', 'seba@xxx.pl', 'Sebiastian', 'qwertyuiodhdh', TRUE, TRUE, TRUE, TRUE );
INSERT INTO users (id, email, name, password, non_expired, non_locked, credentials_not_expired, enabled) VALUES ('4', 'kazek@xxx.pl', 'Kazimierz', 'kakakzld', TRUE, TRUE, TRUE, TRUE );
INSERT INTO users (id, email, name, password, non_expired, non_locked, credentials_not_expired, enabled) VALUES ('5', 'wladek@xxx.pl', 'Wladek', 'wldk', TRUE, TRUE, TRUE, TRUE );

INSERT INTO events (id, name, start_date_time, place_name, description, public_event, owner_id) VALUES ('1', 'SomeEvent', TIMESTAMP '2016-12-31 23:45:00', 'Krakow', 'sylwester z jedynka', TRUE, '1');

INSERT INTO news(id, content, create_date, event_id) VALUES ('1', 'Lorem ipsum', TIMESTAMP '2016-12-31 23:45:00', '1');
INSERT INTO news(id, content, create_date, event_id) VALUES ('2', 'Lorem ipsum', TIMESTAMP '2016-12-31 23:43:00', '1');
INSERT INTO news(id, content, create_date, event_id) VALUES ('3', 'Lorem ipsum', TIMESTAMP '2016-12-31 23:47:00', '1');

INSERT INTO users_to_events (event_id, user_id) VALUES ('1', '2');
INSERT INTO users_to_events (event_id, user_id) VALUES ('1', '5');

ALTER sequence credentials_sequence RESTART WITH 5;
