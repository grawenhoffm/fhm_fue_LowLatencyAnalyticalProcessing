select * from inventory
  full outer join date_dim on inv_date_sk = d_date_sk
  full outer join item on inv_item_sk = i_item_sk
  full outer join warehouse on inv_warehouse_sk = w_warehouse_sk

