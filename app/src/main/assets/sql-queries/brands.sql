--
-- File generated with SQLiteStudio v3.4.4 on Τετ Ιουν 7 12:16:49 2023
--
-- Text encoding used: UTF-8
--
PRAGMA foreign_keys = off;
BEGIN TRANSACTION;

-- Table: companies
CREATE TABLE IF NOT EXISTS companies (company_name TEXT PRIMARY KEY NOT NULL, discrete_title TEXT);
INSERT INTO companies (company_name, discrete_title) VALUES ('ΑΛΦΑ ΒΗΤΑ ΒΑΣΙΛΟΠΟΥΛΟΣ ΜΟΝΟΠΡΟ', 'ΑΒ Βασιλόπουλος');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΣΚΡΟΥΤΖ ΑΝΩΝΥΜΟΣ ΕΤΑΙΡΕΙΑ ΥΠΗΡΕΣΙΕΣ ΔΙΑΔΙΚΤΥΟΥ', 'skroutz');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΜΥΡΤΟ ΜΑΡΙΟΥΣ ΚΑΙ ΣΙΑ ΕΕ', 'Old School Barber Shop');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΔΙΑΜΑΝΤΗΣ ΜΑΣΟΥΤΗΣ ΑΕ ΣΟΥΠΕΡ Μ', 'μασούτης');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΣΟΥΠΕΡ ΜΑΡΚΕΤ ΕΓΝΑΤΙΑ ΑΕ', 'Discount Markt');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΤΣΑΚΙΡΙΔΗΣ ΙΣΑΑΚ', 'Στο Άψε Ψήσε');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΜΑΓΕΙΡΕΙΑ ΣΤΑΓΚΙΔΗ ΕΕ', 'Μαγειρείο-Καντίνα Σταγκίδης');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΧΧ ΚΡΕΑΤΟΣΚΕΥΑΣΜΑΤΑ ΚΑΙ ΣΙΑ Ε', 'Χρήστος Γυράδικο');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΕΛΛΗΝΙΚΕΣ ΥΠΕΡΑΓΟΡΕΣ ΣΚΛΑΒΕΝΙΤ', 'Σκλαβενίτης');

-- Index: sqlite_autoindex_companies_1
CREATE UNIQUE INDEX IF NOT EXISTS sqlite_autoindex_companies_1 ON companies (company_name COLLATE BINARY);

COMMIT TRANSACTION;
PRAGMA foreign_keys = on;
