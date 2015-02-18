SELECT
  orderId,
  invoiceId,
  id,
  amountOrder,
  amount,
  transferDate,
  orderDate,
  status,
  ebayName,
  firstname,
  lastname,
  customerId,
  description,
  MAX(matching) AS matching
FROM (
       SELECT
         CAST(a.Bestellung AS CHAR)          AS orderId,
         CAST(a.rechnung AS CHAR)            AS invoiceId,
         b.uuid                              AS id,
         a.GesamtPreis_der_Bestellung_Brutto AS amountOrder,
         b.amount                            AS amount,
         a.Datum_Kauf                        AS orderDate,
         b.transfer_date                     AS transferDate,
         b.status                            AS status,
         a.Kunden_ebay_name                  AS ebayName,
         a.Kunden_vorname                    AS firstname,
         a.Kunden_name                       AS lastname,
         CAST(a.Kundenummer AS CHAR)         AS customerId,
         b.description,
         1.0                                 AS matching
       FROM
         hermes_bank_statement b, mage_custom_order a
       WHERE
         (
           b.description_b LIKE CONCAT("%", a.Kunden_name, "%", a.Kunden_vorname, "%") COLLATE utf8_general_ci
           OR
           b.description_b LIKE CONCAT("%", a.Kunden_vorname, "%", a.Kunden_name, "%") COLLATE utf8_general_ci
         )
         AND
         b.description_b LIKE CONCAT("%", Bestellung, "%")
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount

         AND b.order_id IS NULL AND a.payment_id IS NULL
         AND
         (
           (a.typ = 'ebay_vorkasse')
           OR
           (a.typ = 'Shop_Rechnung')
           OR
           (a.typ = 'Shop_sofort' AND a.Status = 'complete')
           OR
           (a.typ = 'Shop_vorkasse')
         )
         AND a.Status != 'canceled' AND a.Status != 'closed'

       UNION

       -- order, lastname or ebay
       SELECT
         CAST(a.Bestellung AS CHAR)          AS orderId,
         CAST(a.rechnung AS CHAR)            AS invoiceId,
         b.uuid                              AS id,
         a.GesamtPreis_der_Bestellung_Brutto AS amountOrder,
         b.amount                            AS amount,
         a.Datum_Kauf                        AS orderDate,
         b.transfer_date                     AS transferDate,
         b.status                            AS status,
         a.Kunden_ebay_name                  AS ebayName,
         a.Kunden_vorname                    AS firstname,
         a.Kunden_name                       AS lastname,
         CAST(a.Kundenummer AS CHAR)         AS customerId,
         b.description,
         0.99                                AS matching
       FROM
         hermes_bank_statement b, mage_custom_order a
       WHERE
         (
           LOWER(b.description_b) LIKE CONCAT("%", LOWER(a.Kunden_name), "%") COLLATE utf8_general_ci
           OR
           LOWER(b.description_b) LIKE CONCAT("%", LOWER(a.Kunden_ebay_name), "%") COLLATE utf8_general_ci
         )
         AND
         length(a.Kunden_name) > 4
         AND
         b.description_b LIKE CONCAT("%", Bestellung, "%")
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount
         AND b.order_id IS NULL AND a.payment_id IS NULL
         AND
         (a.typ = 'ebay_vorkasse')

         AND a.Status != 'canceled' AND a.Status != 'closed'

       UNION

       -- name, ebay
       SELECT
         CAST(a.Bestellung AS CHAR)          AS orderId,
         CAST(a.rechnung AS CHAR)            AS invoiceId,
         b.uuid                              AS id,
         a.GesamtPreis_der_Bestellung_Brutto AS amountOrder,
         b.amount                            AS amount,
         a.Datum_Kauf                        AS orderDate,
         b.transfer_date                     AS transferDate,
         b.status                            AS status,
         a.Kunden_ebay_name                  AS ebayName,
         a.Kunden_vorname                    AS firstname,
         a.Kunden_name                       AS lastname,
         CAST(a.Kundenummer AS CHAR)         AS customerId,
         b.description,
         0.98                                AS matching
       FROM
         hermes_bank_statement b, mage_custom_order a
       WHERE
         (
           b.description_b LIKE CONCAT("%", a.Kunden_name, "%", a.Kunden_vorname, "%") COLLATE utf8_general_ci
           OR
           b.description_b LIKE CONCAT("%", a.Kunden_vorname, "%", a.Kunden_name, "%") COLLATE utf8_general_ci
         )
         AND
         LOWER(b.description_b) LIKE CONCAT("%", LOWER(a.Kunden_ebay_name), "%") COLLATE utf8_general_ci
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount

         AND b.order_id IS NULL AND a.payment_id IS NULL

         AND
         (a.typ = 'ebay_vorkasse')

         AND a.Status != 'canceled' AND a.Status != 'closed'

       UNION

       -- ebay
       SELECT
         CAST(a.Bestellung AS CHAR)          AS orderId,
         CAST(a.rechnung AS CHAR)            AS invoiceId,
         b.uuid                              AS id,
         a.GesamtPreis_der_Bestellung_Brutto AS amountOrder,
         b.amount                            AS amount,
         a.Datum_Kauf                        AS orderDate,
         b.transfer_date                     AS transferDate,
         b.status                            AS status,
         a.Kunden_ebay_name                  AS ebayName,
         a.Kunden_vorname                    AS firstname,
         a.Kunden_name                       AS lastname,
         CAST(a.Kundenummer AS CHAR)         AS customerId,
         b.description,
         0.95                                AS matching
       FROM
         hermes_bank_statement b, mage_custom_order a
       WHERE

         LOWER(b.description_b) LIKE CONCAT("%", LOWER(a.Kunden_ebay_name), "%") COLLATE utf8_general_ci
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount
         AND b.order_id IS NULL AND a.payment_id IS NULL

         AND

         (a.typ = 'ebay_vorkasse')

         AND a.Status != 'canceled' AND a.Status != 'closed'

       UNION

       -- name
       SELECT
         CAST(a.Bestellung AS CHAR)          AS orderId,
         CAST(a.rechnung AS CHAR)            AS invoiceId,
         b.uuid                              AS id,
         a.GesamtPreis_der_Bestellung_Brutto AS amountOrder,
         b.amount                            AS amount,
         a.Datum_Kauf                        AS orderDate,
         b.transfer_date                     AS transferDate,
         b.status                            AS status,
         a.Kunden_ebay_name                  AS ebayName,
         a.Kunden_vorname                    AS firstname,
         a.Kunden_name                       AS lastname,
         CAST(a.Kundenummer AS CHAR)         AS customerId,
         b.description,
         0.92                                AS matching
       FROM
         hermes_bank_statement b, mage_custom_order a
       WHERE
         (
           b.description_b LIKE CONCAT("%", a.Kunden_name, "%", a.Kunden_vorname, "%") COLLATE utf8_general_ci
           OR
           b.description_b LIKE CONCAT("%", a.Kunden_vorname, "%", a.Kunden_name, "%") COLLATE utf8_general_ci
         )
         AND
         length(a.Kunden_name) > 4
         AND
         length(a.Kunden_vorname) > 4
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount
         AND b.order_id IS NULL AND a.payment_id IS NULL
         AND
         (
           (a.typ = 'ebay_vorkasse')
           OR
           (a.typ = 'Shop_Rechnung')
           OR
           (a.typ = 'Shop_sofort' AND a.Status = 'complete')
           OR
           (a.typ = 'Shop_vorkasse')
         )
         AND a.Status != 'canceled' AND a.Status != 'closed'

       UNION

       -- order
       SELECT
         CAST(a.Bestellung AS CHAR)          AS orderId,
         CAST(a.rechnung AS CHAR)            AS invoiceId,
         b.uuid                              AS id,
         a.GesamtPreis_der_Bestellung_Brutto AS amountOrder,
         b.amount                            AS amount,
         a.Datum_Kauf                        AS orderDate,
         b.transfer_date                     AS transferDate,
         b.status                            AS status,
         a.Kunden_ebay_name                  AS ebayName,
         a.Kunden_vorname                    AS firstname,
         a.Kunden_name                       AS lastname,
         CAST(a.Kundenummer AS CHAR)         AS customerId,
         b.description,
         0.90                                AS matching
       FROM
         hermes_bank_statement b, mage_custom_order a
       WHERE
         b.description_b LIKE CONCAT("%", Bestellung, "%")
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount
         AND b.order_id IS NULL AND a.payment_id IS NULL

         AND
         (
           (a.typ = 'ebay_vorkasse')
           OR
           (a.typ = 'Shop_Rechnung')
           OR
           (a.typ = 'Shop_sofort' AND a.Status = 'complete')
           OR
           (a.typ = 'Shop_vorkasse')
         )

         AND a.Status != 'canceled' AND a.Status != 'closed'

       UNION

       -- lastname
       SELECT
         CAST(a.Bestellung AS CHAR)          AS orderId,
         CAST(a.rechnung AS CHAR)            AS invoiceId,
         b.uuid                              AS id,
         a.GesamtPreis_der_Bestellung_Brutto AS amountOrder,
         b.amount                            AS amount,
         a.Datum_Kauf                        AS orderDate,
         b.transfer_date                     AS transferDate,
         b.status                            AS status,
         a.Kunden_ebay_name                  AS ebayName,
         a.Kunden_vorname                    AS firstname,
         a.Kunden_name                       AS lastname,
         CAST(a.Kundenummer AS CHAR)         AS customerId,
         b.description,
         0.80                                AS matching
       FROM
         hermes_bank_statement b, mage_custom_order a
       WHERE
         b.description_b LIKE CONCAT("%", a.Kunden_name, "%") COLLATE utf8_general_ci
         AND
         length(a.Kunden_name) > 4
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount

         AND b.order_id IS NULL AND a.payment_id IS NULL
         AND
         (
           (a.typ = 'ebay_vorkasse')
           OR
           (a.typ = 'Shop_Rechnung')
           OR
           (a.typ = 'Shop_sofort' AND a.Status = 'complete')
           OR
           (a.typ = 'Shop_vorkasse')
         )

         AND a.Status != 'canceled' AND a.Status != 'closed'

       UNION

       -- name Rechnungsnr
       SELECT
         CAST(a.Bestellung AS CHAR)          AS orderId,
         CAST(a.rechnung AS CHAR)            AS invoiceId,
         b.uuid                              AS id,
         a.GesamtPreis_der_Bestellung_Brutto AS amountOrder,
         b.amount                            AS amount,
         a.Datum_Kauf                        AS orderDate,
         b.transfer_date                     AS transferDate,
         b.status                            AS status,
         a.Kunden_ebay_name                  AS ebayName,
         a.Kunden_vorname                    AS firstname,
         a.Kunden_name                       AS lastname,
         CAST(a.Kundenummer AS CHAR)         AS customerId,
         b.description,
         0.95                                AS matching
       FROM
         hermes_bank_statement b, mage_custom_order a
       WHERE
         (
           b.description_b LIKE CONCAT("%", a.Kunden_name, "%", a.Kunden_vorname, "%") COLLATE utf8_general_ci
           OR
           b.description_b LIKE CONCAT("%", a.Kunden_vorname, "%", a.Kunden_name, "%") COLLATE utf8_general_ci
         )
         AND
         b.description_b LIKE CONCAT("%", a.Rechnung, "%")
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount

         AND b.order_id IS NULL AND a.payment_id IS NULL
         AND
         (
           (a.typ = 'ebay_vorkasse')
           OR
           (a.typ = 'Shop_Rechnung')
           OR
           (a.typ = 'Shop_sofort' AND a.Status = 'complete')
           OR
           (a.typ = 'Shop_vorkasse')
         )

         AND a.Status != 'canceled' AND a.Status != 'closed'

       UNION
       -- nachname Rechnungsnr
       SELECT
         CAST(a.Bestellung AS CHAR)          AS orderId,
         CAST(a.rechnung AS CHAR)            AS invoiceId,
         b.uuid                              AS id,
         a.GesamtPreis_der_Bestellung_Brutto AS amountOrder,
         b.amount                            AS amount,
         a.Datum_Kauf                        AS orderDate,
         b.transfer_date                     AS transferDate,
         b.status                            AS status,
         a.Kunden_ebay_name                  AS ebayName,
         a.Kunden_vorname                    AS firstname,
         a.Kunden_name                       AS lastname,
         CAST(a.Kundenummer AS CHAR)         AS customerId,
         b.description,
         0.88                                AS matching
       FROM
         hermes_bank_statement b, mage_custom_order a
       WHERE

         b.description_b LIKE CONCAT("%", a.Kunden_name, "%") COLLATE utf8_general_ci

         AND
         b.description_b LIKE CONCAT("%", a.Rechnung, "%")
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount

         AND b.order_id IS NULL AND a.payment_id IS NULL
         AND
         (
           (a.typ = 'ebay_vorkasse')
           OR
           (a.typ = 'Shop_Rechnung')
           OR
           (a.typ = 'Shop_sofort' AND a.Status = 'complete')
           OR
           (a.typ = 'Shop_vorkasse')
         )

         AND a.Status != 'canceled' AND a.Status != 'closed'

       UNION
       -- nur Bestllnr
       SELECT
         CAST(a.Bestellung AS CHAR)          AS orderId,
         CAST(a.rechnung AS CHAR)            AS invoiceId,
         b.uuid                              AS id,
         a.GesamtPreis_der_Bestellung_Brutto AS amountOrder,
         b.amount                            AS amount,
         a.Datum_Kauf                        AS orderDate,
         b.transfer_date                     AS transferDate,
         b.status                            AS status,
         a.Kunden_ebay_name                  AS ebayName,
         a.Kunden_vorname                    AS firstname,
         a.Kunden_name                       AS lastname,
         CAST(a.Kundenummer AS CHAR)         AS customerId,
         b.description,
         0.89                                AS matching
       FROM
         hermes_bank_statement b, mage_custom_order a
       WHERE


         b.description_b LIKE CONCAT("%", a.Bestellung, "%")
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount

         AND length(b.description_b) < 30

         AND b.order_id IS NULL AND a.payment_id IS NULL
         AND
         (
           (a.typ = 'ebay_vorkasse')
           OR
           (a.typ = 'Shop_Rechnung')
           OR
           (a.typ = 'Shop_sofort' AND a.Status = 'complete')
           OR
           (a.typ = 'Shop_vorkasse')
         )

         AND a.Status != 'canceled' AND a.Status != 'closed'

       UNION
       -- nur Bestllnr und RNR
       SELECT
         CAST(a.Bestellung AS CHAR)          AS orderId,
         CAST(a.rechnung AS CHAR)            AS invoiceId,
         b.uuid                              AS id,
         a.GesamtPreis_der_Bestellung_Brutto AS amountOrder,
         b.amount                            AS amount,
         a.Datum_Kauf                        AS orderDate,
         b.transfer_date                     AS transferDate,
         b.status                            AS status,
         a.Kunden_ebay_name                  AS ebayName,
         a.Kunden_vorname                    AS firstname,
         a.Kunden_name                       AS lastname,
         CAST(a.Kundenummer AS CHAR)         AS customerId,
         b.description,
         0.99                                AS matching
       FROM
         hermes_bank_statement b, mage_custom_order a
       WHERE

         b.description_b LIKE CONCAT("%", a.Rechnung, "%")
         AND
         b.description_b LIKE CONCAT("%", a.Bestellung, "%")
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount


         AND b.order_id IS NULL AND a.payment_id IS NULL
         AND
         (
           (a.typ = 'ebay_vorkasse')
           OR
           (a.typ = 'Shop_Rechnung')
           OR
           (a.typ = 'Shop_sofort' AND a.Status = 'complete')
           OR
           (a.typ = 'Shop_vorkasse')
         )

         AND a.Status != 'canceled' AND a.Status != 'closed'

       UNION
       -- nur Bestllnr und Kundennr
       SELECT
         CAST(a.Bestellung AS CHAR)          AS orderId,
         CAST(a.rechnung AS CHAR)            AS invoiceId,
         b.uuid                              AS id,
         a.GesamtPreis_der_Bestellung_Brutto AS amountOrder,
         b.amount                            AS amount,
         a.Datum_Kauf                        AS orderDate,
         b.transfer_date                     AS transferDate,
         b.status                            AS status,
         a.Kunden_ebay_name                  AS ebayName,
         a.Kunden_vorname                    AS firstname,
         a.Kunden_name                       AS lastname,
         CAST(a.Kundenummer AS CHAR)         AS customerId,
         b.description,
         0.77                                AS matching
       FROM
         hermes_bank_statement b, mage_custom_order a
       WHERE

         b.description_b LIKE CONCAT("%", a.Kundenummer, "%")
         AND
         b.description_b LIKE CONCAT("%", a.Bestellung, "%")
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount


         AND b.order_id IS NULL AND a.payment_id IS NULL
         AND
         (
           (a.typ = 'ebay_vorkasse')
           OR
           (a.typ = 'Shop_Rechnung')
           OR
           (a.typ = 'Shop_sofort' AND a.Status = 'complete')
           OR
           (a.typ = 'Shop_vorkasse')
         )

         AND a.Status != 'canceled' AND a.Status != 'closed'

       UNION
       -- Vorname bestellnur teil nachname1
       SELECT
         CAST(a.Bestellung AS CHAR)          AS orderId,
         CAST(a.rechnung AS CHAR)            AS invoiceId,
         b.uuid                              AS id,
         a.GesamtPreis_der_Bestellung_Brutto AS amountOrder,
         b.amount                            AS amount,
         a.Datum_Kauf                        AS orderDate,
         b.transfer_date                     AS transferDate,
         b.status                            AS status,
         a.Kunden_ebay_name                  AS ebayName,
         a.Kunden_vorname                    AS firstname,
         a.Kunden_name                       AS lastname,
         CAST(a.Kundenummer AS CHAR)         AS customerId,
         b.description,
         1.0                                 AS matching
       FROM
         hermes_bank_statement b, mage_custom_order a
       WHERE
         (
           b.description_b LIKE
           CONCAT("%", substring(a.Kunden_name, 1, 5), "%", a.Kunden_vorname, "%") COLLATE utf8_general_ci
           OR
           b.description_b LIKE
           CONCAT("%", a.Kunden_vorname, "%", substring(a.Kunden_name, 1, 5), "%") COLLATE utf8_general_ci
         )
         AND
         b.description_b LIKE CONCAT("%", Bestellung, "%")
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount

         AND b.order_id IS NULL AND a.payment_id IS NULL
         AND
         (
           (a.typ = 'ebay_vorkasse')
           OR
           (a.typ = 'Shop_Rechnung')
           OR
           (a.typ = 'Shop_sofort' AND a.Status = 'complete')
           OR
           (a.typ = 'Shop_vorkasse')
         )
         AND a.Status != 'canceled' AND a.Status != 'closed'

       UNION
       -- nachnahme
       SELECT
         CAST(a.Bestellung AS CHAR)          AS orderId,
         CAST(a.rechnung AS CHAR)            AS invoiceId,
         b.uuid                              AS id,
         a.GesamtPreis_der_Bestellung_Brutto AS amountOrder,
         b.amount                            AS amount,
         a.Datum_Kauf                        AS orderDate,
         b.transfer_date                     AS transferDate,
         b.status                            AS status,
         a.Kunden_ebay_name                  AS ebayName,
         a.Kunden_vorname                    AS firstname,
         a.Kunden_name                       AS lastname,
         CAST(a.Kundenummer AS CHAR)         AS customerId,
         b.description,
         1.0                                 AS matching
       FROM
         hermes_bank_statement b, mage_custom_order a
       WHERE
         b.description_b LIKE ("%DeutschePost%")

         AND
         b.description_b LIKE CONCAT("%", Bestellung, "%")
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount

         AND b.order_id IS NULL AND a.payment_id IS NULL
         AND

         (a.typ = 'Shop_Nachnahme' AND a.Status = 'complete')

         AND a.Status != 'canceled' AND a.Status != 'closed'

       UNION


       -- Name bestellnummer aber falscher Betrag
       SELECT
         CAST(a.Bestellung AS CHAR)          AS orderId,
         CAST(a.rechnung AS CHAR)            AS invoiceId,
         b.uuid                              AS id,
         a.GesamtPreis_der_Bestellung_Brutto AS amountOrder,
         b.amount                            AS amount,
         a.Datum_Kauf                        AS orderDate,
         b.transfer_date                     AS transferDate,
         b.status                            AS status,
         a.Kunden_ebay_name                  AS ebayName,
         a.Kunden_vorname                    AS firstname,
         a.Kunden_name                       AS lastname,
         CAST(a.Kundenummer AS CHAR)         AS customerId,
         b.description,
         0.5                                 AS matching
       FROM
         hermes_bank_statement b, mage_custom_order a
       WHERE
         (
           b.description_b LIKE CONCAT("%", a.Kunden_name, "%", a.Kunden_vorname, "%") COLLATE utf8_general_ci
           OR
           b.description_b LIKE CONCAT("%", a.Kunden_vorname, "%", a.Kunden_name, "%") COLLATE utf8_general_ci
         )
         AND
         b.description_b LIKE CONCAT("%", Bestellung, "%")


         AND b.order_id IS NULL AND a.payment_id IS NULL
         AND
         (
           (a.typ = 'ebay_vorkasse')
           OR
           (a.typ = 'Shop_Rechnung')
           OR
           (a.typ = 'Shop_sofort' AND a.Status = 'complete')
           OR
           (a.typ = 'Shop_vorkasse')
         )
         AND a.Status != 'canceled' AND a.Status != 'closed'


       UNION
       -- name Kundennr
       SELECT
         CAST(a.Bestellung AS CHAR)          AS orderId,
         CAST(a.rechnung AS CHAR)            AS invoiceId,
         b.uuid                              AS id,
         a.GesamtPreis_der_Bestellung_Brutto AS amountOrder,
         b.amount                            AS amount,
         a.Datum_Kauf                        AS orderDate,
         b.transfer_date                     AS transferDate,
         b.status                            AS status,
         a.Kunden_ebay_name                  AS ebayName,
         a.Kunden_vorname                    AS firstname,
         a.Kunden_name                       AS lastname,
         CAST(a.Kundenummer AS CHAR)         AS customerId,
         b.description,
         0.77                                AS matching
       FROM
         hermes_bank_statement b, mage_custom_order a
       WHERE

         b.description_b LIKE CONCAT("%", a.Kundenummer, "%")
         AND
         b.description_b LIKE CONCAT("%", a.Kunden_name, "%") COLLATE utf8_general_ci
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount


         AND b.order_id IS NULL AND a.payment_id IS NULL
         AND
         (
           (a.typ = 'ebay_vorkasse')
           OR
           (a.typ = 'Shop_Rechnung')
           OR
           (a.typ = 'Shop_sofort' AND a.Status = 'complete')
           OR
           (a.typ = 'Shop_vorkasse')
         )
         AND abs(a.Datum_Kauf - b.transfer_date) < 10

         AND a.Status != 'canceled' AND a.Status != 'closed'

       UNION

       -- nachname Rechnungsnr
       SELECT
         CAST(a.Bestellung AS CHAR)          AS orderId,
         CAST(a.rechnung AS CHAR)            AS invoiceId,
         b.uuid                              AS id,
         a.GesamtPreis_der_Bestellung_Brutto AS amountOrder,
         b.amount                            AS amount,
         a.Datum_Kauf                        AS orderDate,
         b.transfer_date                     AS transferDate,
         b.status                            AS status,
         a.Kunden_ebay_name                  AS ebayName,
         a.Kunden_vorname                    AS firstname,
         a.Kunden_name                       AS lastname,
         CAST(a.Kundenummer AS CHAR)         AS customerId,
         b.description,
         0.95                                AS matching
       FROM
         hermes_bank_statement b, mage_custom_order a
       WHERE

         b.description_b LIKE CONCAT("%", a.Kunden_name, "%") COLLATE utf8_general_ci

         AND
         b.description_b LIKE CONCAT("%", a.Rechnung, "%")
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount

         AND b.order_id IS NULL AND a.payment_id IS NULL
     ) AS c
GROUP BY
  orderId,
  invoiceId,
  id,
  amountOrder,
  amount,
  transferDate,
  orderDate,
  status,
  ebayName,
  firstname,
  lastname,
  customerId,
  description
ORDER BY
  matching DESC