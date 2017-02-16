INSERT INTO Customer(name, BCH4) VALUES ('Demo Kunde', 'DMKU');
INSERT INTO Customer(name, BCH4) VALUES ('Test One', 'TEON');
INSERT INTO Customer(name, BCH4) VALUES ('Cust Two', 'CUTW');

INSERT INTO Server(hostname, ip, type, id_customer) VALUES ('demokundevsp.semester.ch', '10.100.100.15', 'VSP', 1);
INSERT INTO Server(hostname, ip, type, id_customer) VALUES ('demokundesentry.semester.ch', '10.100.100.16', 'Sentry', 1);
INSERT INTO Server(hostname, ip, type, id_customer) VALUES ('demokundesentry2.semester.ch', '10.100.100.17', 'Sentry', 1);

INSERT INTO Server(hostname, ip, type, id_customer) VALUES ('testonevsp.semester.ch', '10.10.100.20', 'VSP', 2);
INSERT INTO Server(hostname, ip, type, id_customer) VALUES ('testonesentry1.semester.ch', '10.10.100.21', 'Sentry', 2);
INSERT INTO Server(hostname, ip, type, id_customer) VALUES ('testonesentry2.semester.ch', '10.10.100.22', 'Sentry', 2);

INSERT INTO Server(hostname, ip, type, id_customer) VALUES ('custtwovsp.semester.ch', '192.168.0.15', 'VSP', 3);
INSERT INTO Server(hostname, ip, type, id_customer) VALUES ('custtwosentry.semester.ch', '192.168.0.16', 'Sentry', 3);