import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ApiLoginService {

  constructor(private httpClient: HttpClient) { }


}
