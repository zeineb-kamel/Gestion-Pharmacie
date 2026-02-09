-- ============================================
-- MINI PROJET PHARMACIE - Script SQL Oracle
-- Base de donnees pour la gestion de pharmacie
-- ============================================

-- Suppression des tables existantes (dans l'ordre des dependances)
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE ACHAT CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE MEDICAMENT CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE APPAREIL_MEDICAL CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE CLIENT_FIDELE CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE seq_medicament';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE seq_appareil';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE seq_achat';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/

-- ============================================
-- CREATION DES SEQUENCES
-- ============================================

-- Sequence pour generer les codes uniques des medicaments
CREATE SEQUENCE seq_medicament
    START WITH 1
    INCREMENT BY 1
    NOCACHE;

-- Sequence pour generer les codes uniques des appareils medicaux
CREATE SEQUENCE seq_appareil
    START WITH 1
    INCREMENT BY 1
    NOCACHE;

-- Sequence pour generer les IDs des achats
CREATE SEQUENCE seq_achat
    START WITH 1
    INCREMENT BY 1
    NOCACHE;

-- ============================================
-- CREATION DES TABLES
-- ============================================

-- Table CLIENT_FIDELE
CREATE TABLE CLIENT_FIDELE (
    cin NUMBER(8) PRIMARY KEY,
    nom VARCHAR2(100) NOT NULL,
    prenom VARCHAR2(100) NOT NULL,
    credit NUMBER(10,2) DEFAULT 0,
    montant_total_achats NUMBER(10,2) DEFAULT 0
);

-- Table MEDICAMENT (avec discrimination pour heritage)
CREATE TABLE MEDICAMENT (
    code NUMBER PRIMARY KEY,
    num_serie NUMBER NOT NULL,
    nom VARCHAR2(100) NOT NULL,
    genre VARCHAR2(100) NOT NULL,
    prix NUMBER(10,2) NOT NULL,
    date_expiration DATE,
    type_medicament VARCHAR2(20) NOT NULL CHECK (type_medicament IN ('CHIMIQUE', 'HOMEOPATHIQUE')),
    -- Attributs specifiques MedicamentChimique
    constituant_chimique VARCHAR2(200),
    age_minimum NUMBER(3),
    -- Attributs specifiques MedicamentHomeopathique
    plante_utilisee VARCHAR2(200),
    -- Stock
    quantite_stock NUMBER DEFAULT 0
);

-- Table APPAREIL_MEDICAL
CREATE TABLE APPAREIL_MEDICAL (
    code NUMBER PRIMARY KEY,
    nom VARCHAR2(100) NOT NULL,
    prix NUMBER(10,2) NOT NULL,
    quantite_stock NUMBER DEFAULT 0
);

-- Table ACHAT (historique des achats)
CREATE TABLE ACHAT (
    id_achat NUMBER PRIMARY KEY,
    cin_client NUMBER(8) NOT NULL,
    type_vendable VARCHAR2(20) NOT NULL CHECK (type_vendable IN ('MEDICAMENT', 'APPAREIL')),
    code_vendable NUMBER NOT NULL,
    date_achat DATE DEFAULT SYSDATE,
    prix_paye NUMBER(10,2) NOT NULL,
    quantite NUMBER DEFAULT 1,
    CONSTRAINT fk_achat_client FOREIGN KEY (cin_client) REFERENCES CLIENT_FIDELE(cin)
);

-- ============================================
-- INDEX POUR OPTIMISER LES RECHERCHES
-- ============================================

CREATE INDEX idx_medicament_nom ON MEDICAMENT(nom);
CREATE INDEX idx_medicament_genre ON MEDICAMENT(genre);
CREATE INDEX idx_medicament_type ON MEDICAMENT(type_medicament);
CREATE INDEX idx_medicament_expiration ON MEDICAMENT(date_expiration);
CREATE INDEX idx_client_nom ON CLIENT_FIDELE(nom);
CREATE INDEX idx_achat_client ON ACHAT(cin_client);
CREATE INDEX idx_achat_date ON ACHAT(date_achat);

-- ============================================
-- INSERTION DES DONNEES DE TEST
-- ============================================

-- Clients fideles
INSERT INTO CLIENT_FIDELE (cin, nom, prenom, credit, montant_total_achats) 
VALUES (12345678, 'Ben Ali', 'Mohamed', 0, 50);

INSERT INTO CLIENT_FIDELE (cin, nom, prenom, credit, montant_total_achats) 
VALUES (87654321, 'Trabelsi', 'Fatma', 0, 120);

INSERT INTO CLIENT_FIDELE (cin, nom, prenom, credit, montant_total_achats) 
VALUES (11223344, 'Hammami', 'Ahmed', 0, 30);

INSERT INTO CLIENT_FIDELE (cin, nom, prenom, credit, montant_total_achats) 
VALUES (55667788, 'Jebali', 'Sarra', 0, 200);

-- Medicaments Chimiques
INSERT INTO MEDICAMENT (code, num_serie, nom, genre, prix, date_expiration, type_medicament, constituant_chimique, age_minimum, quantite_stock)
VALUES (seq_medicament.NEXTVAL, 1001, 'Augmentin', 'Antibiotique', 9.00, ADD_MONTHS(SYSDATE, 12), 'CHIMIQUE', 'Amoxicilline', 3, 50);

INSERT INTO MEDICAMENT (code, num_serie, nom, genre, prix, date_expiration, type_medicament, constituant_chimique, age_minimum, quantite_stock)
VALUES (seq_medicament.NEXTVAL, 1002, 'Efferalgan', 'Paracetamol', 6.30, ADD_MONTHS(SYSDATE, 18), 'CHIMIQUE', 'Paracetamol', 0, 100);

INSERT INTO MEDICAMENT (code, num_serie, nom, genre, prix, date_expiration, type_medicament, constituant_chimique, age_minimum, quantite_stock)
VALUES (seq_medicament.NEXTVAL, 1003, 'Doliprane', 'Paracetamol', 5.50, ADD_MONTHS(SYSDATE, 24), 'CHIMIQUE', 'Paracetamol', 0, 80);

INSERT INTO MEDICAMENT (code, num_serie, nom, genre, prix, date_expiration, type_medicament, constituant_chimique, age_minimum, quantite_stock)
VALUES (seq_medicament.NEXTVAL, 1004, 'Amoxil', 'Antibiotique', 12.00, ADD_MONTHS(SYSDATE, 6), 'CHIMIQUE', 'Amoxicilline', 6, 30);

INSERT INTO MEDICAMENT (code, num_serie, nom, genre, prix, date_expiration, type_medicament, constituant_chimique, age_minimum, quantite_stock)
VALUES (seq_medicament.NEXTVAL, 1005, 'Prozac', 'Antidepresseur', 35.00, ADD_MONTHS(SYSDATE, 1), 'CHIMIQUE', 'Fluoxetine', 18, 20);

-- Medicaments Homeopathiques
INSERT INTO MEDICAMENT (code, num_serie, nom, genre, prix, date_expiration, type_medicament, plante_utilisee, quantite_stock)
VALUES (seq_medicament.NEXTVAL, 2001, 'Ferplus', 'Vitamines', 29.30, ADD_MONTHS(SYSDATE, 36), 'HOMEOPATHIQUE', 'Spiruline', 60);

INSERT INTO MEDICAMENT (code, num_serie, nom, genre, prix, date_expiration, type_medicament, plante_utilisee, quantite_stock)
VALUES (seq_medicament.NEXTVAL, 2002, 'ForCapill', 'Vitamines', 16.30, ADD_MONTHS(SYSDATE, 24), 'HOMEOPATHIQUE', 'Ortie', 40);

INSERT INTO MEDICAMENT (code, num_serie, nom, genre, prix, date_expiration, type_medicament, plante_utilisee, quantite_stock)
VALUES (seq_medicament.NEXTVAL, 2003, 'Sedatif PC', 'Calmant', 8.50, ADD_MONTHS(SYSDATE, 18), 'HOMEOPATHIQUE', 'Valeriane', 70);

INSERT INTO MEDICAMENT (code, num_serie, nom, genre, prix, date_expiration, type_medicament, plante_utilisee, quantite_stock)
VALUES (seq_medicament.NEXTVAL, 2004, 'Arnica Montana', 'Anti-inflammatoire', 12.00, ADD_MONTHS(SYSDATE, 2), 'HOMEOPATHIQUE', 'Arnica', 45);

INSERT INTO MEDICAMENT (code, num_serie, nom, genre, prix, date_expiration, type_medicament, plante_utilisee, quantite_stock)
VALUES (seq_medicament.NEXTVAL, 2005, 'Phytolax', 'Laxatif', 7.80, ADD_MONTHS(SYSDATE, 12), 'HOMEOPATHIQUE', 'Sene', 55);

-- Appareils Medicaux
INSERT INTO APPAREIL_MEDICAL (code, nom, prix, quantite_stock)
VALUES (seq_appareil.NEXTVAL, 'Tensiometre Digital', 89.90, 15);

INSERT INTO APPAREIL_MEDICAL (code, nom, prix, quantite_stock)
VALUES (seq_appareil.NEXTVAL, 'Thermometre Infrarouge', 45.00, 25);

INSERT INTO APPAREIL_MEDICAL (code, nom, prix, quantite_stock)
VALUES (seq_appareil.NEXTVAL, 'Glucometre', 120.00, 10);

INSERT INTO APPAREIL_MEDICAL (code, nom, prix, quantite_stock)
VALUES (seq_appareil.NEXTVAL, 'Nebuliseur', 180.00, 8);

INSERT INTO APPAREIL_MEDICAL (code, nom, prix, quantite_stock)
VALUES (seq_appareil.NEXTVAL, 'Oxymetre de Pouls', 35.00, 30);

COMMIT;

-- ============================================
-- VERIFICATION DES DONNEES
-- ============================================

SELECT 'Clients fideles: ' || COUNT(*) AS info FROM CLIENT_FIDELE;
SELECT 'Medicaments: ' || COUNT(*) AS info FROM MEDICAMENT;
SELECT 'Appareils medicaux: ' || COUNT(*) AS info FROM APPAREIL_MEDICAL;

-- Afficher les medicaments qui expirent dans 2 mois
SELECT nom, genre, prix, date_expiration, type_medicament
FROM MEDICAMENT
WHERE date_expiration <= ADD_MONTHS(SYSDATE, 2)
ORDER BY date_expiration;
