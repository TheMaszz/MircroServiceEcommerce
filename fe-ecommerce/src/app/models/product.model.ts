export interface ProductModel {
  id: number;
  name: string;
  description: string;
  price: number;
  qty: number;
  product_image: [
    {
      id: number;
      product_id: number;
      image_url: string;
    }
  ];
  created_at: string;
  created_by: number;
  created_user: string;
  updated_at: string;
  updated_by: number;
  updated_user: string;
}