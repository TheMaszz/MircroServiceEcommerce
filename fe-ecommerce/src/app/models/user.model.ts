export interface Address {
  id: number;
  name: string;
  fullname: string;
  address: string;
  description: string;
  phone: string;
  default_address: number;
}

export interface UserModel {
  id: number;
  username: string;
  email: string;
  profile_url: string;
  role: number;
  created_at: string;
  updated_at: string;
  last_login: string;
}

export interface ShopDetail {
  count_products: number;
  id: number;
  username: string;
  profile_url: string;
  last_login: string;
}