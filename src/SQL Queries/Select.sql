SELECT * FROM customer

SELECT c.name, s.hostname, s.ip, s.type FROM Customer c
JOIN server s ON (c.id = s.id_customer)
WHERE c.name = 'Test One' OR c.BCH4 = 'TEON'