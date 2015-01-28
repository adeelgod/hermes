SELECT uuid, account, amount, currency, receiver1, receiver2, description, firstname, lastname, status, transfer_date as transferDate, valuta_date as valutaDate, order_id as orderId, client_id as clientId, invoice_id as invoiceId, amount_diff as amountDiff, ebay_name as ebayName, matching FROM hermes_bank_statement WHERE status = :status AND amount > 0