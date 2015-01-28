SELECT
  CAST(a.Bestellung as CHAR) as orderId, a.GesamtPreis_der_Bestellung_Brutto as amount, a.Datum_Kauf as orderDate, a.typ as type, a.Status as status, a.Kunden_ebay_name as ebayName, a.Kunden_vorname as firstname, a.Kunden_name as lastname, 1.0 as matching
FROM
  mage_custom_order AS a,
  hermes_bank_statement AS b
WHERE
  b.uuid = :uuid
  AND
  b.description like CONCAT("%", a.Bestellung, "%")
  AND
  a.GesamtPreis_der_Bestellung_Brutto = b.amount
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
