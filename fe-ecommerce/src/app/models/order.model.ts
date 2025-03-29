export interface OrderRequest{
    address_id: number;
    stage: string;
    total_amount: number;
    products: Product[];
}

export interface OrderResponse{
    
}

class Product{
    product_id!: number;
}