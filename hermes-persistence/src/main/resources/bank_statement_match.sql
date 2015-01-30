-- order, name
SELECT
  CAST(a.Bestellung as CHAR) as orderId, a.GesamtPreis_der_Bestellung_Brutto as amount, a.Datum_Kauf as orderDate, a.typ as type, a.Status as status, a.Kunden_ebay_name as ebayName, a.Kunden_vorname as firstname, a.Kunden_name as lastname, 1.0 as matching
FROM
  hermes_bank_statement b, mage_custom_order a
WHERE
  b.uuid = :uuid
  AND
  (
    b.description LIKE CONCAT("%", a.Kunden_name, "%", a.Kunden_vorname, "%") COLLATE utf8_general_ci
    OR
    b.description LIKE CONCAT("%", a.Kunden_vorname, "%", a.Kunden_name, "%") COLLATE utf8_general_ci
  )
  AND
  b.description like CONCAT("%", a.Bestellung, "%")
  AND
  a.GesamtPreis_der_Bestellung_Brutto = b.amount
  AND
  a.Datum_Kauf >= DATE_SUB(NOW(), INTERVAL :lookup MONTH)

UNION

-- order, lastname or ebay
SELECT
  CAST(a.Bestellung as CHAR) as orderId, a.GesamtPreis_der_Bestellung_Brutto as amount, a.Datum_Kauf as orderDate, a.typ as type, a.Status as status, a.Kunden_ebay_name as ebayName, a.Kunden_vorname as firstname, a.Kunden_name as lastname, 0.99 as matching
FROM
  hermes_bank_statement b, mage_custom_order a
WHERE
  b.uuid = :uuid
  AND
  (
    LOWER(b.description) like CONCAT("%", LOWER(a.Kunden_name), "%") COLLATE utf8_general_ci
    OR
    LOWER(b.description) like CONCAT("%", LOWER(a.Kunden_ebay_name), "%") COLLATE utf8_general_ci
  )
  AND
  b.description like CONCAT("%", a.Bestellung, "%")
  AND
  a.GesamtPreis_der_Bestellung_Brutto = b.amount
  AND
  a.Datum_Kauf >= DATE_SUB(NOW(), INTERVAL :lookup MONTH)

UNION

-- name, ebay
SELECT
  CAST(a.Bestellung as CHAR) as orderId, a.GesamtPreis_der_Bestellung_Brutto as amount, a.Datum_Kauf as orderDate, a.typ as type, a.Status as status, a.Kunden_ebay_name as ebayName, a.Kunden_vorname as firstname, a.Kunden_name as lastname, 0.98 as matching
FROM
  hermes_bank_statement b, mage_custom_order a
WHERE
  b.uuid = :uuid
  AND
  (
    b.description LIKE CONCAT("%", a.Kunden_name, "%", a.Kunden_vorname, "%") COLLATE utf8_general_ci
    OR
    b.description LIKE CONCAT("%", a.Kunden_vorname, "%", a.Kunden_name, "%") COLLATE utf8_general_ci
  )
  AND
  LOWER(b.description) like CONCAT("%", LOWER(a.Kunden_ebay_name), "%") COLLATE utf8_general_ci
  AND
  a.GesamtPreis_der_Bestellung_Brutto = b.amount
  AND
  a.Datum_Kauf >= DATE_SUB(NOW(), INTERVAL :lookup MONTH)

UNION

-- ebay
SELECT
  CAST(a.Bestellung as CHAR) as orderId, a.GesamtPreis_der_Bestellung_Brutto as amount, a.Datum_Kauf as orderDate, a.typ as type, a.Status as status, a.Kunden_ebay_name as ebayName, a.Kunden_vorname as firstname, a.Kunden_name as lastname, 0.97 as matching
FROM
  hermes_bank_statement b, mage_custom_order a
WHERE
  b.uuid = :uuid
  AND
  LOWER(b.description) like CONCAT("%", LOWER(a.Kunden_ebay_name), "%") COLLATE utf8_general_ci
  AND
  a.GesamtPreis_der_Bestellung_Brutto = b.amount
  AND
  a.Datum_Kauf >= DATE_SUB(NOW(), INTERVAL :lookup MONTH)

UNION

-- name
SELECT
  CAST(a.Bestellung as CHAR) as orderId, a.GesamtPreis_der_Bestellung_Brutto as amount, a.Datum_Kauf as orderDate, a.typ as type, a.Status as status, a.Kunden_ebay_name as ebayName, a.Kunden_vorname as firstname, a.Kunden_name as lastname, 0.96 as matching
FROM
  hermes_bank_statement b, mage_custom_order a
WHERE
  b.uuid = :uuid
  AND
  (
    b.description LIKE CONCAT("%", a.Kunden_name, "%", a.Kunden_vorname, "%") COLLATE utf8_general_ci
    OR
    b.description LIKE CONCAT("%", a.Kunden_vorname, "%", a.Kunden_name, "%") COLLATE utf8_general_ci
  )
  AND
  a.GesamtPreis_der_Bestellung_Brutto = b.amount
  AND
  a.Datum_Kauf >= DATE_SUB(NOW(), INTERVAL :lookup MONTH)

UNION

-- order
SELECT
  CAST(a.Bestellung as CHAR) as orderId, a.GesamtPreis_der_Bestellung_Brutto as amount, a.Datum_Kauf as orderDate, a.typ as type, a.Status as status, a.Kunden_ebay_name as ebayName, a.Kunden_vorname as firstname, a.Kunden_name as lastname, 0.95 as matching
FROM
  hermes_bank_statement b, mage_custom_order a
WHERE
  b.uuid = :uuid
  AND
  b.description like CONCAT("%", a.Bestellung, "%")
  AND
  a.GesamtPreis_der_Bestellung_Brutto = b.amount
  AND
  a.Datum_Kauf >= DATE_SUB(NOW(), INTERVAL :lookup MONTH)

UNION

-- lastname
SELECT
  CAST(a.Bestellung as CHAR) as orderId, a.GesamtPreis_der_Bestellung_Brutto as amount, a.Datum_Kauf as orderDate, a.typ as type, a.Status as status, a.Kunden_ebay_name as ebayName, a.Kunden_vorname as firstname, a.Kunden_name as lastname, 0.9 as matching
FROM
  hermes_bank_statement b, mage_custom_order a
WHERE
  b.uuid = :uuid
  AND
  LOWER(b.description) like CONCAT("%", LOWER(a.Kunden_name), "%") COLLATE utf8_general_ci
  AND
  a.GesamtPreis_der_Bestellung_Brutto = b.amount
  AND
  a.Datum_Kauf >= DATE_SUB(NOW(), INTERVAL :lookup MONTH)
  /**
  AND
  (
    (a.typ='ebay_vorkasse' AND a.Status='pending')
    OR
    (a.typ='Shop_Nachname' AND a.Status='complete')
    OR
    (a.typ='Shop_Rechnung' AND a.Status='paystat_pending')
    OR
    (a.typ='Shop_Rechnung' AND a.Status='paystat_reminder1')
    OR
    (a.typ='Shop_Rechnung' AND a.Status='paystat_reminder1')
    OR
    (a.typ='Shop_sofort' AND a.Status='complete')
    OR
    (a.typ='Shop_vorkasse' AND a.Status='pending')
  )
  */
ORDER BY
  orderDate DESC, matching DESC