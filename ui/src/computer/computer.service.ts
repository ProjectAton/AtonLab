/**
 * Created by camilosampedro on 2/01/17.
 */
import {Injectable} from '@angular/core';
import {Http, Headers, Response} from '@angular/http';
import {Router} from '@angular/router';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/toPromise';
import 'rxjs/add/operator/catch';
import {Computer} from "./computer.model";

@Injectable()
export class ComputerService {
    constructor(private http: Http, private router: Router){}

    addNew(computer: Computer) {
        return this.http.post('/api/computer',computer);
    }

    deleteComputer(ip: number){
        return this.http.delete(`/api/computer/${ip}`).map(res=>res.json());
    }

    getComputer(ip: number) {
        return this.http.get(`/api/computer/${ip}`).map(res=>res.json())
    }

    editComputer(computer: Computer){
        return this.http.put('/api/computer', computer).map(res=>res.json())
    }
}