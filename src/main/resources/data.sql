-- 1. Creazione delle Squadre (Tabella indipendente)
INSERT INTO squadra (id, nome, citta) VALUES (nextval('squadra_seq'), 'Runners Club Roma', 'Roma');
INSERT INTO squadra (id, nome, citta) VALUES (nextval('squadra_seq'), 'Fiamme Gialle', 'Roma');

-- 2. Creazione degli Utenti
INSERT INTO utente (id, nome, cognome, email, squadra_id) VALUES (nextval('utente_seq'), 'Andrea', 'Rossi', 'andrea.rossi@email.com', (SELECT id FROM squadra WHERE nome = 'Runners Club Roma'));
INSERT INTO utente (id, nome, cognome, email, squadra_id) VALUES (nextval('utente_seq'), 'Marco', 'Bianchi', 'marco.bianchi@email.com', (SELECT id FROM squadra WHERE nome = 'Fiamme Gialle'));
INSERT INTO utente (id, nome, cognome, email, squadra_id) VALUES (nextval('utente_seq'), 'Pietro', 'Savelli', 'pietrodavelli04@gmail.com', NULL);
INSERT INTO utente (id, nome, cognome, email) VALUES (nextval('utente_seq'), 'Pier', 'Admin', 'Pier@gmail.com');
INSERT INTO utente (id, nome, cognome, email) VALUES (nextval('utente_seq'), 'admin', 'p', 'admin@gmail.com');

-- 3. Creazione delle Credenziali
INSERT INTO credentials (id, username, password, role, utente_id) VALUES (nextval('credentials_seq'), 'andrea_run', '$2a$10$yWAIDyuEr78BBBFZ5cYh8.Nw4gUHFTRG5FwaWqNCGeOD8M4mh3.xy', 'DEFAULT', (SELECT id FROM utente WHERE email = 'andrea.rossi@email.com'));
INSERT INTO credentials (id, username, password, role, utente_id) VALUES (nextval('credentials_seq'), 'marco_runner', '$2a$10$yWAIDyuEr78BBBFZ5cYh8.Nw4gUHFTRG5FwaWqNCGeOD8M4mh3.xy', 'DEFAULT', (SELECT id FROM utente WHERE email = 'marco.bianchi@email.com'));
INSERT INTO credentials (id, username, password, role, utente_id) VALUES (nextval('credentials_seq'), 'pietro', '$2a$10$yWAIDyuEr78BBBFZ5cYh8.Nw4gUHFTRG5FwaWqNCGeOD8M4mh3.xy', 'DEFAULT', (SELECT id FROM utente WHERE email = 'pietrodavelli04@gmail.com'));
INSERT INTO credentials (id, username, password, role, utente_id) VALUES (nextval('credentials_seq'), 'Pier', '$2a$12$aFt4.TJh/5SOTtcuXJO0ae061vdD.rsB50w7QczRnvjc7oBtwCHxC', 'ADMIN', (SELECT id FROM utente WHERE email = 'Pier@gmail.com'));
INSERT INTO credentials (id, username, password, role, utente_id) VALUES (nextval('credentials_seq'), 'admin', '$2a$10$yWAIDyuEr78BBBFZ5cYh8.Nw4gUHFTRG5FwaWqNCGeOD8M4mh3.xy', 'ADMIN', (SELECT id FROM utente WHERE email = 'admin@gmail.com'));

-- 4. Creazione delle Scarpe
INSERT INTO scarpa (id, marca, modello, km_totali, km_massimi, atleta_id) VALUES (nextval('scarpa_seq'), 'Saucony', 'Endorphin Azura 2026', 150.5, 600.0, (SELECT id FROM utente WHERE email = 'andrea.rossi@email.com'));
INSERT INTO scarpa (id, marca, modello, km_totali, km_massimi, atleta_id) VALUES (nextval('scarpa_seq'), 'Brooks', 'GTS 25', 320.0, 800.0, (SELECT id FROM utente WHERE email = 'andrea.rossi@email.com'));
INSERT INTO scarpa (id, marca, modello, km_totali, km_massimi, atleta_id) VALUES (nextval('scarpa_seq'), 'Xtep', '160X', 50.0, 500.0, (SELECT id FROM utente WHERE email = 'andrea.rossi@email.com'));

-- 5. Creazione degli Allenamenti
INSERT INTO allenamento (id, data, distanza_in_km, tipo_di_allenamento, titolo, atleta_id, scarpa_id) VALUES (nextval('allenamento_seq'), '2026-05-12', 10.0, 'Z2', 'Gara 10km (Tempo: 44:53)', (SELECT id FROM utente WHERE email = 'andrea.rossi@email.com'), (SELECT id FROM scarpa WHERE modello = 'Endorphin Azura 2026'));
INSERT INTO allenamento (id, data, distanza_in_km, tipo_di_allenamento, titolo, atleta_id, scarpa_id) VALUES (nextval('allenamento_seq'), '2026-05-18', 21.1, 'Z3', 'Lungo verso la Roma-Ostia', (SELECT id FROM utente WHERE email = 'andrea.rossi@email.com'), (SELECT id FROM scarpa WHERE modello = 'GTS 25'));
INSERT INTO allenamento (id, data, distanza_in_km, tipo_di_allenamento, titolo, atleta_id, scarpa_id) VALUES (nextval('allenamento_seq'), '2026-05-25', 9.9, 'Z2', 'Ripetute con transizioni', (SELECT id FROM utente WHERE email = 'andrea.rossi@email.com'), (SELECT id FROM scarpa WHERE modello = '160X'));

-- 6. Creazione dei Commenti
INSERT INTO commento (id, data_ora, testo, allenamento_id, autore_id) VALUES (nextval('commento_seq'), '2026-05-12 11:00:00', 'Ottimo test, le scarpe hanno risposto benissimo alla nuova strategia.', (SELECT id FROM allenamento WHERE titolo = 'Gara 10km (Tempo: 44:53)'), (SELECT id FROM utente WHERE email = 'andrea.rossi@email.com'));
INSERT INTO commento (id, data_ora, testo, allenamento_id, autore_id) VALUES (nextval('commento_seq'), '2026-05-12 11:15:00', 'Grande ritmo, stai andando alla grande!', (SELECT id FROM allenamento WHERE titolo = 'Gara 10km (Tempo: 44:53)'), (SELECT id FROM utente WHERE email = 'marco.bianchi@email.com'));

-- 7. Creazione dei Follower
INSERT INTO follower (utente_id, seguito_id) VALUES ((SELECT id FROM utente WHERE email = 'marco.bianchi@email.com'), (SELECT id FROM utente WHERE email = 'andrea.rossi@email.com'));
INSERT INTO follower (utente_id, seguito_id) VALUES ((SELECT id FROM utente WHERE email = 'andrea.rossi@email.com'), (SELECT id FROM utente WHERE email = 'marco.bianchi@email.com'));
