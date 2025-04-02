export interface OrderRequest{
    created_by: number;
    
    address_id: number;
    stage: string;
    total_amount: number;
    products: Product[];
}

export interface OrderModel{
    id: number;
    shop_name: string;
    shop_id: number;
    stage: string;
    total_amount: number;
    user_id: number;
    created_at: string;
    updated_at: string;
    paymentStatus: any;
    products: any;
}

class Product{
    product_id!: number;
    qty!: number;
}