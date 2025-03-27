import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { ResponseModel } from "app/models/response.model";
import { environment } from "environments/environment";
import { Observable } from "rxjs";

@Injectable({
  providedIn: 'root',
})
export class ProductService{
      private serviceUrl = 'apiendpoint/product';
    
      constructor(private http: HttpClient) {}
    
      getProducts(
        params: {
          search?: string;
          page_number?: number;
          page_size?: number;
          sort?: string;
          sort_type?: 'asc' | 'desc';
        } = {
          search: '',
          page_number: 1,
          page_size: 10,
          sort: 'created_at',
          sort_type: 'desc',
        }
      ): Observable<ResponseModel> {
        let httpParams = new HttpParams();
        Object.entries(params).forEach(([key, value]) => {
          if (value !== undefined && value !== null) {
            httpParams = httpParams.set(key, String(value));
          }
        });
    
        return this.http.get<ResponseModel>(
          `${environment.apiUrl}/${this.serviceUrl}/getAll`,
          { params: httpParams }
        );
      }
    
      getProductById(id: number): Observable<ResponseModel> {
        return this.http.get<ResponseModel>(
          `${environment.apiUrl}/${this.serviceUrl}/${id}`
        );
      }
    
      getAutoCompleteProduct(
        params: {
          search: string;
          limit: number;
        } = {
          search: '',
          limit: 10,
        }
      ): Observable<ResponseModel> {
        let httpParams = new HttpParams();
        Object.entries(params).forEach(([key, value]) => {
          if (value !== undefined && value !== null) {
            httpParams = httpParams.set(key, String(value));
          }
        });
    
        return this.http.get<ResponseModel>(
          `${environment.apiUrl}/${this.serviceUrl}/getAutoComplete`,
          { params: httpParams }
        );
      }
}