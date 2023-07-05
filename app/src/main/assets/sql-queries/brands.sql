--
-- File generated with SQLiteStudio v3.4.4 on Τετ Ιουν 7 12:16:49 2023
--
-- Text encoding used: UTF-8
--
PRAGMA foreign_keys = off;
BEGIN TRANSACTION;

-- Table: companies
CREATE TABLE IF NOT EXISTS companies (company_name TEXT PRIMARY KEY NOT NULL, discrete_title TEXT, category TEXT);
INSERT INTO companies (company_name, discrete_title) VALUES ('ΑΛΦΑ ΒΗΤΑ ΒΑΣΙΛΟΠΟΥΛΟΣ ΜΟΝΟΠΡΟ', 'ΑΒ Βασιλόπουλος', 'Καθημερινά Ψώνια');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΔΙΑΜΑΝΤΗΣ ΜΑΣΟΥΤΗΣ ΑΕ ΣΟΥΠΕΡ Μ', 'μασούτης', 'Καθημερινά Ψώνια');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΣΟΥΠΕΡ ΜΑΡΚΕΤ ΕΓΝΑΤΙΑ ΑΕ', 'Discount Markt', 'Καθημερινά Ψώνια');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΕΛΛΗΝΙΚΕΣ ΥΠΕΡΑΓΟΡΕΣ ΣΚΛΑΒΕΝΙΤ', 'Σκλαβενίτης', 'Καθημερινά Ψώνια');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΛΙΝΤΛ ΕΛΛΑΣ ΚΑΙ ΣΙΑ ΟΜΟΡΡΥΘΜΗ', 'LIDL', 'Καθημερινά Ψώνια');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΜΕΤΡΟ ΑΝΩΝΥΜΟΣ ΕΜΠΟΡΙΚΗ ΚΑΙ ΒΙ', 'My market', 'Καθημερινά Ψώνια');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΠΕΝΤΕ ΑΕ ΑΝΩΝΥΜΗ ΕΜΠΟΡΙΚΗ ΕΤΑΙ', 'Γαλαξίας', 'Καθημερινά Ψώνια');
INSERT INTO companies (company_name, discrete_title) VALUES ('BAZAAR ΑΝΩΝΥΜΗ ΨΥΚΤΙΚΗ ΒΙΟΜΗΧΑ', 'Bazaar', 'Καθημερινά Ψώνια');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΤΕΡΤΙΒΑΝΙΔΗΣ ΧΑΡΙΛ', 'ΤΕΡΤΙΒΑΝΙΔΗΣ', 'Καθημερινά Ψώνια');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΓΚΕΚΤΣΙΑΝ ΑΡΣΕΝ', 'Prime Market Mini Market', 'Καθημερινά Ψώνια');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΗΛΙΑΔΗΣ ΙΩΑΚΕ', 'Καφεκοπτείο Ηλιάδη', 'Καθημερινά Ψώνια');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΧΟΝΔΡΑΝΤΩΝΗΣ ΚΩΝΣΤ', 'Φούρνος Χονδραντώνης', 'Καθημερινά Ψώνια');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΜΙΝΔΗΣ ΚΩΝΣΤ', 'Ζαχαροπλαστείο Φίνο', 'Καθημερινά Ψώνια');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΤΣΙΛΙΓΚΕΡΙΔΟΥ ΚΑΙ ΣΙΑ ΟΕ', 'Ιχθυοπωλείο Τσιλιγκερίδου', 'Καθημερινά Ψώνια');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΧΟΥΛΙΑΡΑΣ ΠΡΟΔΡ', 'Ιχθυοπωλείο Χουλιάρας', 'Καθημερινά Ψώνια');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΚΥΡΤΣΙΟΣ ΑΠΟΣΤΟΛΟΣ ΤΣΙΑΠΑΝΙΤ', 'Κρεοπωλείο Φάρμα Κύρτσιος', 'Καθημερινά Ψώνια');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΜΠΑΡΜΠΑΡΟΥΣΗΣ ΣΩΤΗΡ', 'Οπωροπωλείο Μπαρμπαρούσης', 'Καθημερινά Ψώνια');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΔΗΜΗΤΡΙΟΥ ΙΩΑΝΝ', 'Παντοπωλείο Δημητρίου', 'Καθημερινά Ψώνια');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΓΩΤΑΣ ΓΕΩΡΓ', 'Περίπτερο Γώτας', 'Καθημερινά Ψώνια');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΤΣΑΚΙΡΙΔΗΣ ΙΣΑΑΚ', 'Στο Άψε Ψήσε', 'Εστίαση');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΜΑΓΕΙΡΕΙΑ ΣΤΑΓΚΙΔΗ ΕΕ', 'Μαγειρείο-Καντίνα Σταγκίδης', 'Εστίαση');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΧΧ ΚΡΕΑΤΟΣΚΕΥΑΣΜΑΤΑ ΚΑΙ ΣΙΑ Ε', 'Χρήστος Γυράδικο', 'Εστίαση');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΓΕΣΑΓΙΑΝ ΟΒΑΝΝ', 'Θράκα 18', 'Εστίαση');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΚΑΡΑΤΖΟΓΛΟΥ ΕΛΕΥΘ', 'Vitro', 'Εστίαση');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΦΩΤΕΙΝΗ ΤΡΥΠΤΣΙΑ ΧΡΗΣΤΟΣ ΓΚΟΛΦ', 'Το Κάτι Άλλο', 'Εστίαση');
INSERT INTO companies (company_name, discrete_title) VALUES ('FIVE PLUS Ι Κ Ε', 'ΓΡΝ', 'Εστίαση');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΣΑΟΥΛΙΔΗΣ Ν ΟΥΖΟΥΝΗΣ Ι ΟΕ', 'Από την σχάρα στην λαδόκολλα', 'Εστίαση');
INSERT INTO companies (company_name, discrete_title) VALUES ('Β ΓΕΩΡΓΙΟΥ 24 ΕΣΤΙΑΣΗ Ε Ε', 'Φάτε Σκάστε', 'Εστίαση');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΣΤΕΦΑΝΟΥ ΠΑΡΘΕ', 'Porkito', 'Εστίαση');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΙΟΡΔΑΝΙΔΗΣ ΠΑΝΑΓ', 'Pizza Jordani', 'Εστίαση');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΛΟΓΙΣΜΙΚΗ ΕΠΕ', 'Ticket Services', 'Ψυχαγωγία');
INSERT INTO companies (company_name, discrete_title) VALUES ('ODEON ENTERTAINMENT MANAGEMENT', 'Odeon', 'Ψυχαγωγία');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΣΚΡΟΥΤΖ ΑΝΩΝΥΜΟΣ ΕΤΑΙΡΕΙΑ ΥΠΗΡΕΣΙΕΣ ΔΙΑΔΙΚΤΥΟΥ', 'skroutz', 'E-commerce');
INSERT INTO companies (company_name, discrete_title) VALUES ('DIGITAL SIMPLY ΟΕ', 'metabook', 'E-commerce');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΤΖΕΒΕΛΕΚΙΔΗΣ ΑΕ ΕΜΠΟΡΙΑ ΗΛΕΚΤΡ', 'Electromarkt', 'E-commerce');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΜΥΡΤΟ ΜΑΡΙΟΥΣ ΚΑΙ ΣΙΑ ΕΕ', 'Old School Barber Shop', 'Υπηρεσίες Αισθητικής');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΑΝΩΝΥΜΟΣ ΕΤΑΙΡΕΙΑ ΜΕΤΑΦΟΡΩΝ ΕΚ', 'Shell', 'Καύσιμα');
INSERT INTO companies (company_name, discrete_title) VALUES ('Ι ΝΑΚΟΠΟΥΛΟΥ ΣΙΑ Ε Ε', 'Φαρμακείο Νακοπούλου', 'Ιατροφαρμακευτικά');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΝΙΚΟΛΑΙΔΗΣ ΜΙΛΤΙ', 'Φαρμακείο Νικολαΐδης', 'Ιατροφαρμακευτικά');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΠΑΠΑΡΗ ΟΛΥΜΠ', 'Φαρμακείο Πάπαρη', 'Ιατροφαρμακευτικά');
INSERT INTO companies (company_name, discrete_title) VALUES ('ΜΠΟΖΑΣ ΣΤΕΡΓ', 'Οφθαλμίατρος Μπόζας', 'Ιατροφαρμακευτικά');

-- Index: sqlite_autoindex_companies_1
CREATE UNIQUE INDEX IF NOT EXISTS sqlite_autoindex_companies_1 ON companies (company_name COLLATE BINARY);

COMMIT TRANSACTION;
PRAGMA foreign_keys = on;
