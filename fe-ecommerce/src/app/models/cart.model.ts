export interface CartItem {
  id: number;
  name: string;
  price: number;
  qtyInStock: number;
  qty: number;
  totalPrice?: number;
  imageUrl: string;
  created_by: number;
  created_user: string;
  selected: boolean;
}

export interface CartGroup {
  created_by: number;
  created_user: string;
  products: CartItem[];
  selected: boolean;
}
