import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {LoginRequest, LoginResponse} from "../model/login.model";
import {BehaviorSubject, Observable} from "rxjs";
import {environment} from "../../environments/environment";
import {JwtHelperService} from "@auth0/angular-jwt";
import {LoggedUser} from "../model/logged-user.model";
import {Router} from "@angular/router";
import {InstructorsService} from "./instructors.service";
import {StudentsService} from "./students.service";
import {Student} from "../model/student.model";
import {Instructor} from "../model/instructor.model";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  jwtHelperService = new JwtHelperService();
  user = new BehaviorSubject<LoggedUser | null>(null);
  tokenExpirationTimer: any;

  constructor(private readonly http: HttpClient,
              private readonly router: Router,
              private readonly instructorService: InstructorsService,
              private readonly studentService: StudentsService) { }

  public login(user: LoginRequest) : Observable<LoginResponse> {
    const formData = new FormData();
    formData.append('username', user.username);
    formData.append('password', user.password);
    return this.http.post<LoginResponse>(environment.backendHost + "/login", formData);
  }

  saveToken(jwtTokens: LoginResponse) {
    const decodedAccessToken = this.jwtHelperService.decodeToken(jwtTokens.access_token);
    const loggedUser = new LoggedUser(decodedAccessToken.sub, decodedAccessToken.roles,
      jwtTokens.access_token, this.getExpirationDate(decodedAccessToken.exp), undefined, undefined);
    this.user.next(loggedUser);
    this.autoLogout(this.getExpirationDate(decodedAccessToken.exp).valueOf() - new Date().valueOf());
    localStorage.setItem('userData', JSON.stringify(loggedUser));
    this.redirectLoggedInUser(decodedAccessToken, jwtTokens.access_token);
  }

  redirectLoggedInUser(decodedToken: any, accessToken: string) {
    if(decodedToken.roles.includes("Admin")) {
     this.router.navigateByUrl("/courses");
    } else if(decodedToken.roles.includes("Instructor")) {
      this.instructorService.loadInstructorByEmail(decodedToken.sub).subscribe(instructor => {
        const loggedUser = new LoggedUser(decodedToken.sub, decodedToken.roles,
          accessToken, this.getExpirationDate(decodedToken.exp), undefined, instructor);
        localStorage.setItem('userData', JSON.stringify(loggedUser));
        this.user.next(loggedUser);
        this.router.navigateByUrl("/instructor-courses/" + instructor.instructorId);
      })
    } else if(decodedToken.roles.includes("Student")) {
      this.studentService.loadStudentByEmail(decodedToken.sub).subscribe(student => {
        const loggedUser = new LoggedUser(decodedToken.sub, decodedToken.roles,
          accessToken, this.getExpirationDate(decodedToken.exp), student, undefined);
        localStorage.setItem('userData', JSON.stringify(loggedUser));
        this.user.next(loggedUser);
        this.router.navigateByUrl("/student-courses/" + student.studentId);
      })
    }
  }

  getExpirationDate(exp: number) {
    const date = new Date(0);
    date.setUTCSeconds(exp);
    return date;
  }

  autoLogin() {
    const userData: {
      username: string,
      roles: string[],
      _token: string,
      _expiration: Date,
      student: Student | undefined,
      instructor: Instructor | undefined
    } = JSON.parse(localStorage.getItem('userData')!);
    if(!userData) return;
    const loadedUser = new LoggedUser(userData.username, userData.roles, userData._token,
      new Date(userData._expiration), userData.student, userData.instructor);
    if(loadedUser.token) {
      this.user.next(loadedUser);
      this.autoLogout(loadedUser._expiration.valueOf() - new Date().valueOf());
    }
  }

  logout() {
    localStorage.clear();
    this.user.next(null);
    this.router.navigate(['/']);
    if(this.tokenExpirationTimer) {
      clearTimeout(this.tokenExpirationTimer);
    }
    this.tokenExpirationTimer = null;
  }

  autoLogout(expirationDuration: number){
    this.tokenExpirationTimer = setTimeout(() => {
      this.logout();
    }, expirationDuration);
  }

  refreshInstructor(instructor: Instructor) {
    const userData : {
      username: string,
      roles: string[],
      _token: string,
      _expiration: Date,
      student: Student | undefined,
      instructor: Instructor | undefined
    } = JSON.parse(localStorage.getItem('userData')!);
    if(!userData) return;
    const loggedUser = new LoggedUser(userData.username, userData.roles, userData._token,
      new Date(userData._expiration), userData.student, instructor);
    this.user.next(loggedUser);
    localStorage.setItem('userData', JSON.stringify(loggedUser));
  }

  refreshStudent(student: Student) {
    const userData : {
      username: string,
      roles: string[],
      _token: string,
      _expiration: Date,
      student: Student | undefined,
      instructor: Instructor | undefined
    } = JSON.parse(localStorage.getItem('userData')!);
    if(!userData) return;
    const loggedUser = new LoggedUser(userData.username, userData.roles, userData._token,
      new Date(userData._expiration), student, userData.instructor);
    if(loggedUser.token) {
      this.user.next(loggedUser);
      localStorage.setItem('userData', JSON.stringify(loggedUser));
    }
  }
}
