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
       -- order, Name, Bestellnummer

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
         b.description, 1.0 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE
         (
           lower(b.description_b) LIKE CONCAT("%", lower(a.Kunden_name), "%", lower(a.Kunden_vorname), "%") COLLATE utf8_general_ci
           OR
           lower(b.description_b) LIKE CONCAT("%", lower(a.Kunden_vorname), "%", lower(a.Kunden_name), "%") COLLATE utf8_general_ci
         )
         AND
         b.description_b LIKE CONCAT("%", Bestellung, "%")
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount

         and b.status = "new" and a.payment_id is NULL
         AND
         (
           (a.typ='Shop_Rechnung' )
           OR
           (a.typ='Shop_sofort' AND a.Status='complete')
           OR
           (a.typ='Shop_vorkasse' )
         )
         AND a.Status!='canceled' and a.Status!='closed'

       UNION

       -- order, lastname

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
         b.description, 0.99 as matching FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE

         LOWER(b.description_b) like CONCAT("%", LOWER(a.Kunden_name), "%") COLLATE utf8_general_ci
         and
         length(a.Kunden_name) > 4
         AND
         b.description_b like CONCAT("%", Bestellung, "%")
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount
         and b.status = "new" and a.payment_id is NULL
         AND
         (
           (a.typ='Shop_Rechnung' )
           OR
           (a.typ='Shop_sofort' AND a.Status='complete')
           OR
           (a.typ='Shop_vorkasse' )
         )
         AND a.Status!='canceled' and a.Status!='closed'

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
         b.description, 0.98 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE
         (
           lower(b.description_b) LIKE CONCAT("%", lower(a.Kunden_name), "%", lower(a.Kunden_vorname), "%") COLLATE utf8_general_ci
           OR
           lower(b.description_b) LIKE CONCAT("%", lower(a.Kunden_vorname), "%", lower(a.Kunden_name), "%") COLLATE utf8_general_ci
         )
         AND
         LOWER(b.description_b) like CONCAT("%", LOWER(a.Kunden_ebay_name), "%") COLLATE utf8_general_ci
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount
         AND DATEDIFF(a.Datum_Kauf, b.valuta_date) > -30
         AND DATEDIFF(a.Datum_Kauf, b.valuta_date) < 2
         and b.status = "new" and a.payment_id is NULL

         AND
         (a.typ='ebay_vorkasse' )

         AND a.Status!='canceled' and a.Status!='closed'

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
         b.description, 0.90 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE

         LOWER(b.description_b) like CONCAT("%", LOWER(a.Kunden_ebay_name), "%") COLLATE utf8_general_ci
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount
         and b.status = "new" and a.payment_id is NULL
         AND   (a.typ='ebay_vorkasse' )
         AND DATEDIFF(a.Datum_Kauf, b.valuta_date) > -30
         AND DATEDIFF(a.Datum_Kauf, b.valuta_date) < 2

         AND a.Status!='canceled' and a.Status!='closed'

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
         b.description, 0.83 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE

         (
           lower(b.description_b) LIKE CONCAT("%", lower(a.Kunden_name), "%", lower(a.Kunden_vorname), "%") COLLATE utf8_general_ci
           OR
           lower(b.description_b) LIKE CONCAT("%", lower(a.Kunden_vorname), "%", lower(a.Kunden_name), "%") COLLATE utf8_general_ci
         )
         AND  length(a.Kunden_name) > 4
         AND  length(a.Kunden_vorname) > 4

         AND DATEDIFF(a.Datum_Kauf, b.valuta_date) > -30
         AND DATEDIFF(a.Datum_Kauf, b.valuta_date) < 2
         AND  a.GesamtPreis_der_Bestellung_Brutto = b.amount
         and b.status = "new" and a.payment_id is NULL
         AND
         (
           (a.typ='ebay_vorkasse' )
           OR
           (a.typ='Shop_Rechnung' )
           OR
           (a.typ='Shop_vorkasse' )
         )
         AND a.Status!='canceled' and a.Status!='closed'

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
         b.description, 0.88 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE
         b.description_b like CONCAT("%", Bestellung, "%")
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount
         and b.status = "new" and a.payment_id is NULL

         AND
         (
           (a.typ='Shop_Rechnung' )
           OR
           (a.typ='Shop_vorkasse' )
         )

         AND a.Status!='canceled' and a.Status!='closed'

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
         b.description, 0.60 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE
         lower(b.description_b) LIKE CONCAT("%", lower(a.Kunden_name), "%") COLLATE utf8_general_ci
         AND
         length(a.Kunden_name) > 4
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount
         AND DATEDIFF(a.Datum_Kauf, b.valuta_date) > -30
         AND DATEDIFF(a.Datum_Kauf, b.valuta_date) < 2

         and b.status = "new" and a.payment_id is NULL
         AND
         (
           (a.typ='ebay_vorkasse' )
           OR
           (a.typ='Shop_Rechnung' )
           OR
           (a.typ='Shop_sofort' AND a.Status='complete')
           OR
           (a.typ='Shop_vorkasse' )
         )

         AND a.Status!='canceled' and a.Status!='closed'

       UNION

       -- lastname gleiches datum
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
         b.description, 0.81 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE
         lower(b.description_b) LIKE CONCAT("%", lower(a.Kunden_name), "%") COLLATE utf8_general_ci
         AND
         length(a.Kunden_name) > 4
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount
         AND Abs(DATEDIFF(a.Datum_Kauf, b.valuta_date)) < 3
         and b.status = "new" and a.payment_id is NULL
         AND
         (
           (a.typ='ebay_vorkasse' )
           OR
           (a.typ='Shop_Rechnung' )
           OR
           (a.typ='Shop_vorkasse' )
         )

         AND a.Status!='canceled' and a.Status!='closed'

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
         b.description, 0.95 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE
         (
           lower(b.description_b) LIKE CONCAT("%", lower(a.Kunden_name), "%", lower(a.Kunden_vorname), "%") COLLATE utf8_general_ci
           OR
           lower(b.description_b) LIKE CONCAT("%", lower(a.Kunden_vorname), "%", lower(a.Kunden_name), "%") COLLATE utf8_general_ci
         )
         AND
         b.description_b like CONCAT("%", a.Rechnung, "%")
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount

         and b.status = "new" and a.payment_id is NULL
         AND
         (
           (a.typ='Shop_Rechnung' )
           OR
           (a.typ='Shop_vorkasse' )
         )

         AND a.Status!='canceled' and a.Status!='closed'

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
         b.description, 0.88 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE

         lower(b.description_b) LIKE CONCAT("%", lower(a.Kunden_name), "%") COLLATE utf8_general_ci

         AND
         lower(b.description_b) LIKE CONCAT("%", a.Rechnung, "%")
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount

         and b.status = "new" and a.payment_id is NULL
         AND
         (
           (a.typ='Shop_Rechnung' )
           OR
           (a.typ='Shop_vorkasse' )
         )

         AND a.Status!='canceled' and a.Status!='closed'

       UNION
       -- nur Bestellnr
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
         b.description, 0.89 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE


         b.description_b like CONCAT("%", a.Bestellung, "%")
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount

         and length(b.description_b) < 30

         and b.status = "new" and a.payment_id is NULL
         AND
         (
           (a.typ='Shop_Rechnung' )
           OR
           (a.typ='Shop_vorkasse' )
         )

         AND a.Status!='canceled' and a.Status!='closed'

       UNION

       -- teil Bestellnr
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
         b.description,0.84 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE


         lower(b.description_b) LIKE CONCAT("%", substring(a.Bestellung,4,6), "%")
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount
         and b.status = "new" and a.payment_id is NULL
         AND
         (
           (a.typ='Shop_Rechnung' )
           OR
           (a.typ='Shop_vorkasse' )
         )

         AND a.Status!='canceled' and a.Status!='closed'


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
         b.description, 0.99 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE

         b.description_b like CONCAT("%", a.Rechnung, "%")
         AND
         b.description_b like CONCAT("%", a.Bestellung, "%")
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount


         and b.status = "new" and a.payment_id is NULL
         AND
         (
           (a.typ='Shop_Rechnung' )
           OR
           (a.typ='Shop_vorkasse' )
         )

         AND a.Status!='canceled' and a.Status!='closed'

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
         b.description,0.92 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE

         b.description_b like CONCAT("%", a.Kundenummer, "%")
         AND
         b.description_b like CONCAT("%", a.Bestellung, "%")
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount

         and b.status = "new" and a.payment_id is NULL
         AND
         (
           (a.typ='Shop_Rechnung' )
           OR
           (a.typ='Shop_vorkasse' )
         )

         AND a.Status!='canceled' and a.Status!='closed'

       Union
       -- Vorname  teil nachname1
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
         b.description, 0.82 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE
         (
           lower(b.description_b) LIKE CONCAT("%", substring(lower(a.Kunden_name),1,5), "%", lower(a.Kunden_vorname), "%") COLLATE utf8_general_ci
           OR
           lower(b.description_b) LIKE CONCAT("%", lower(a.Kunden_vorname), "%", substring(lower(a.Kunden_name),1,5), "%") COLLATE utf8_general_ci
         )
         AND DATEDIFF(a.Datum_Kauf, b.valuta_date) > -30
         AND DATEDIFF(a.Datum_Kauf, b.valuta_date) < 2
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount

         and b.status = "new" and a.payment_id is NULL
         AND
         (
           (a.typ='ebay_vorkasse' )
           OR
           (a.typ='Shop_Rechnung' )
           OR
           (a.typ='Shop_sofort' AND a.Status='complete')
           OR
           (a.typ='Shop_vorkasse' )
         )
         AND a.Status!='canceled' and a.Status!='closed'

       Union
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
         b.description, 1.0 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE
         b.description_b LIKE ("%DeutschePost%")

         AND
         b.description_b like CONCAT("%", Bestellung, "%")

         and b.status = "new" and a.payment_id is NULL
         AND

         (a.typ='Shop_Nachnahme' AND a.Status='complete')

         AND a.Status!='canceled' and a.Status!='closed'

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
         b.description, 0.5 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE
         (
           lower(b.description_b) LIKE CONCAT("%", lower(a.Kunden_name), "%", lower(a.Kunden_vorname), "%") COLLATE utf8_general_ci
           OR
           lower(b.description_b) LIKE CONCAT("%", lower(a.Kunden_vorname), "%", lower(a.Kunden_name), "%") COLLATE utf8_general_ci
         )
         AND
         b.description_b like CONCAT("%", Bestellung, "%")


         and b.status = "new" and a.payment_id is NULL
         AND
         (
           (a.typ='Shop_Rechnung' )
           OR
           (a.typ='Shop_vorkasse' )
         )
         AND a.Status!='canceled' and a.Status!='closed'


       UNION
       -- teil name Kundennr
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
         b.description,0.77 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE

         b.description_b like CONCAT("%", a.Kundenummer, "%")
         AND
         lower(b.description_b) LIKE CONCAT("%", lower(substring(a.Kunden_name,1,5)), "%") COLLATE utf8_general_ci
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount
         AND DATEDIFF(a.Datum_Kauf, b.valuta_date) > -30
         AND DATEDIFF(a.Datum_Kauf, b.valuta_date) < 2


         and b.status = "new" and a.payment_id is NULL
         AND
         (
           (a.typ='Shop_Rechnung' )
           OR
           (a.typ='Shop_vorkasse' )
         )
         AND abs(a.Datum_Kauf-b.valuta_date) < 10

         AND a.Status!='canceled' and a.Status!='closed'


       UNION

       -- Kundennummer
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
         b.description, 0.55 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE

         b.description_b like CONCAT("%", a.Kundenummer, "%")
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount

         and b.status = "new" and a.payment_id is NULL
         AND
         (
           (a.typ='Shop_Rechnung' )
           OR
           (a.typ='Shop_vorkasse' )
         )
         AND abs(a.Datum_Kauf-b.valuta_date) < 10

       UNION

       -- nur Bestllnummer bei nachnahme
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
         b.description, 0.85 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE
         b.description_b like CONCAT("%", a.Bestellung, "%")
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount

         and b.status = "new" and a.payment_id is NULL

         AND

         (a.typ='Shop_Nachnahme' AND a.Status='complete')

       UNION

       -- lastname gleiches datum
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
         b.description,0.41 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE
         lower(b.description_b) LIKE CONCAT("%", lower(a.Kunden_name), "%") COLLATE utf8_general_ci
         AND
         length(a.Kunden_name) > 3
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount
         AND
         a.GesamtPreis_der_Bestellung_Brutto = b.amount
         AND Abs(DATEDIFF(a.Datum_Kauf, b.valuta_date)) < 15
         and b.status = "new" and a.payment_id is NULL
         AND
         (
           (a.typ='ebay_vorkasse' )
           OR
           (a.typ='Shop_Rechnung' )
           OR
           (a.typ='Shop_sofort' AND a.Status='complete')
           OR
           (a.typ='Shop_vorkasse' )
         )

         AND a.Status!='canceled' and a.Status!='closed'

       UNION

       -- Gleicher grosser Betrag
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
         b.description, 0.41 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE
         a.GesamtPreis_der_Bestellung_Brutto = b.amount and a.GesamtPreis_der_Bestellung_Brutto > 100
         AND Abs(DATEDIFF(a.Datum_Kauf, b.valuta_date)) < 25
         and b.status = "new" and a.payment_id is NULL
         AND
         (
           (a.typ='ebay_vorkasse' )
           OR
           (a.typ='Shop_Rechnung' )
           OR
           (a.typ='Shop_sofort' AND a.Status='complete')
           OR
           (a.typ='Shop_vorkasse' )
         )

         AND a.Status!='canceled' and a.Status!='closed'

       UNION

       -- Gleicher Betrag
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
         b.description, 0.21 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE
         a.GesamtPreis_der_Bestellung_Brutto = b.amount
         AND Abs(DATEDIFF(a.Datum_Kauf, b.valuta_date)) < 15
         and b.status = "new" and a.payment_id is NULL
         AND
         (
           (a.typ='ebay_vorkasse' )
           OR
           (a.typ='Shop_Rechnung' )
           OR
           (a.typ='Shop_sofort' AND a.Status='complete')
           OR
           (a.typ='Shop_vorkasse' )
         )

         AND a.Status!='canceled' and a.Status!='closed'

       UNION

       -- eine Nummer und aehnlicher betrag

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
         b.description,  0.51 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE
         ABS(a.GesamtPreis_der_Bestellung_Brutto - b.amount) < 10
         and b.status = "new" and a.payment_id is NULL
         and ( b.description_b like CONCAT("%", a.Bestellung, "%") or b.description_b like CONCAT("%", a.rechnung, "%")
         )
         AND
         (
           (a.typ='Shop_Rechnung' )
           OR
           (a.typ='Shop_vorkasse' )
         )
         AND a.Status!='canceled' and a.Status!='closed'

       UNION
       -- eine Nummer und aehnlicher betrag
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
         b.description, 0.31 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE
         ABS(a.GesamtPreis_der_Bestellung_Brutto - b.amount) < 10
         and b.status = "new" and a.payment_id is NULL
         and ( b.description_b like CONCAT("%", a.Bestellung, "%") or b.description_b like CONCAT("%", a.rechnung, "%")
         )
         AND
         (
           (a.typ='Shop_Rechnung' )
           OR
           (a.typ='Shop_vorkasse' )
         )
       UNION

       -- Teil Kundenname
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
         b.description, 0.15 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE
         a.GesamtPreis_der_Bestellung_Brutto = b.amount
         and b.status = "new" and a.payment_id is NULL
         and  lower(b.description_b) LIKE CONCAT("%", lower(substring(a.Kunden_name,1,5)), "%")
         AND DATEDIFF(a.Datum_Kauf, b.valuta_date) > -30
         AND DATEDIFF(a.Datum_Kauf, b.valuta_date) < 2
         AND
         (
           (a.typ='ebay_vorkasse' )
           OR
           (a.typ='Shop_Rechnung' )
           OR
           (a.typ='Shop_sofort' AND a.Status='complete')
           OR
           (a.typ='Shop_vorkasse' )
         )

         AND a.Status!='canceled' and a.Status!='closed'


       UNION
       -- nur name
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
         b.description,0.05 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE
         ABS(a.GesamtPreis_der_Bestellung_Brutto - b.amount) < 10
         and b.status = "new" and a.payment_id is NULL
         and  lower(b.description_b) LIKE CONCAT("%", lower(a.Kunden_name), "%")

         AND
         (
           (a.typ='ebay_vorkasse' )
           OR
           (a.typ='Shop_Rechnung' )
           OR
           (a.typ='Shop_sofort' AND a.Status='complete')
           OR
           (a.typ='Shop_vorkasse' )
         )

         AND a.Status!='canceled' and a.Status!='closed'

       UNION

       -- ebay aehnlicher Betrag
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
         b.description, 0.25 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE

         LOWER(b.description_b) like CONCAT("%", LOWER(a.Kunden_ebay_name), "%") COLLATE utf8_general_ci
         AND
         ABS (a.GesamtPreis_der_Bestellung_Brutto - b.amount) < 7
         AND DATEDIFF(a.Datum_Kauf, b.valuta_date) > -30
         AND DATEDIFF(a.Datum_Kauf, b.valuta_date) < 2
         and b.status = "new" and a.payment_id is NULL

         AND
         (a.typ='ebay_vorkasse' )

         AND a.Status!='canceled' and a.Status!='closed'
       UNION
       -- eine Nummer und aehnlicher betrag
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
         b.description,0.15 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE
         ABS(a.GesamtPreis_der_Bestellung_Brutto - b.amount) < 10
         AND Abs(DATEDIFF(a.Datum_Kauf, b.valuta_date)) < 50
         and b.status = "new" and a.payment_id is NULL
         and ( b.description_b like CONCAT("%", a.Bestellung, "%") or b.description_b like CONCAT("%", a.rechnung, "%")
         )

         AND
         (
           (a.typ='ebay_vorkasse' )
           OR
           (a.typ='Shop_Rechnung' )
           OR
           (a.typ='Shop_sofort' AND a.Status='complete')
           OR
           (a.typ='Shop_vorkasse' )
         )

       UNION

       -- teil Kundenname
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
         b.description, 0.20 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE

         LOWER(b.description_b) like CONCAT("%", LOWER(substring(a.Kunden_name,1,5)), "%") COLLATE utf8_general_ci
         AND
         ABS (a.GesamtPreis_der_Bestellung_Brutto - b.amount) < 10
         AND DATEDIFF(a.Datum_Kauf, b.valuta_date) > -300
         AND DATEDIFF(a.Datum_Kauf, b.valuta_date) < 2
         and b.status = "new" and a.payment_id is NULL

         AND
         (
           (a.typ='ebay_vorkasse' )
           OR
           (a.typ='Shop_Rechnung' )
           OR
           (a.typ='Shop_sofort' AND a.Status='complete')
           OR
           (a.typ='Shop_vorkasse' )
         )


       UNION
       -- Firma
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
         b.description,0.04 as matching
       FROM
         hermes_bank_statement_temp b, mage_custom_order_temp a
       WHERE
         lower(b.description_b) LIKE CONCAT("%", lower(a.Firma), "%")COLLATE utf8_general_ci
         AND
         ABS (a.GesamtPreis_der_Bestellung_Brutto - b.amount) < 10
         AND DATEDIFF(a.Datum_Kauf, b.valuta_date) > -60
         AND DATEDIFF(a.Datum_Kauf, b.valuta_date) < 2
         and b.status = "new" and a.payment_id is NULL

         AND
         (
           (a.typ='ebay_vorkasse' )
           OR
           (a.typ='Shop_Rechnung' )
           OR
           (a.typ='Shop_sofort' AND a.Status='complete')
           OR
           (a.typ='Shop_vorkasse' )
         )

     ) AS c
GROUP BY
  id
ORDER BY
  matching DESC
