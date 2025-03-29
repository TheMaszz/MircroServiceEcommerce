import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { ResponseModel } from "app/models/response.model";
import { environment } from "environments/environment";
import { Observable } from "rxjs";


@Injectable({
    providedIn: 'root',
})
export class UserService{
    private serviceUrl = 'apiendpoint/user';

    constructor(private http: HttpClient){}

    getMyAddress(): Observable<ResponseModel>{
        return this.http.get<ResponseModel>(
            `${environment.apiUrl}/${this.serviceUrl}/address/my`
        )
    }
}